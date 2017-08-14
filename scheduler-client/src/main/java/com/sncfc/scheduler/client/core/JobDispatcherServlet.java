package com.sncfc.scheduler.client.core;

import com.sncfc.scheduler.client.properties.PropertiesDefaultGetter;
import com.sncfc.scheduler.client.properties.PropertiesGetter;
import com.sncfc.scheduler.client.util.ExceptionUtils;
import com.sncfc.scheduler.client.util.HttpClient;
import com.sncfc.scheduler.client.util.NetUtil;
import com.sncfc.scheduler.client.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.*;


public class JobDispatcherServlet extends HttpServlet {

    private static final long serialVersionUID = -3932783704101430036L;

    private static final long REPORT_PERIOD_TIME = 60000L;

    private static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    private final Logger logger = LoggerFactory.getLogger(JobDispatcherServlet.class.getName());

    // private static final int DEFAULT_PROXY_PORT = 8080;
    //
    // private String proxyHost;
    //
    // private int proxyPort = DEFAULT_PROXY_PORT;
    //
    // private List<String> validateUrlList = new ArrayList<String>();
    //
    // private List<String> reportUrlList = new ArrayList<String>();

    private ThreadPoolExecutor taskExecutor;

    private ConcurrentHashMap<String, JobBroker> jobBrokerMap = new ConcurrentHashMap<String, JobBroker>();

    private ConcurrentHashMap<String, JobExecutionContext> fireInstances = new ConcurrentHashMap<String, JobExecutionContext>();

    /* for test usage,work in single server,not work in cluster. */
    private ConcurrentHashMap<String, String> concurrentMonitorMap = new ConcurrentHashMap<String, String>();

    private CheckerThread checkerThread;

    private ServletContext servletContext;

    private PropertiesGetter propertiesGetter;

    public final void init() throws ServletException {
        servletContext = getServletConfig().getServletContext();
        InputStream is = null;
        try {
            String filePath = "/WEB-INF/classes/uts.properties";
            is = servletContext.getResourceAsStream(filePath);

            if (is == null) throw new FileNotFoundException(filePath);
            Properties props = new Properties();
            props.load(is);
            String propertieClass = props.getProperty("propertieClass");
            if (StringUtils.isEmpty(propertieClass) || propertieClass.equals(PropertiesDefaultGetter.class)) {
                propertiesGetter  = new PropertiesDefaultGetter();
            } else {
                propertiesGetter = (PropertiesGetter) Class.forName(propertieClass).newInstance();
            }

            if (propertiesGetter == null) {
                throw new ServletException("PropertiesGetter init error.");
            } else {
                propertiesGetter.init(props);
                int coreJobThread = propertiesGetter.getCoreJobThread();
                int maxJobThread = propertiesGetter.getMaxJobThread();
                int jobQueueSize = propertiesGetter.getJobQueueSize();

                taskExecutor = new ThreadPoolExecutor(coreJobThread, maxJobThread, 60, TimeUnit.SECONDS, jobQueueSize == 0 ? new SynchronousQueue<Runnable>() : (jobQueueSize == -1 ? new LinkedBlockingQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(jobQueueSize)));
                checkerThread = new CheckerThread(REPORT_PERIOD_TIME, "checkerThread");
                checkerThread.setDaemon(true);
                checkerThread.start();
            }
        } catch (Exception ex) {
            logger.error("Exception occur when init", ex);
            throw new ServletException(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignore) {
                    // ignore it
                }
            }
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    public void destroy() {
        taskExecutor.shutdown();
        checkerThread.shutdown();
        try {
            checkerThread.join(3000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("unchecked")
    protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> reqParams = readRequest(req);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("nodeName", getNodeName());
        result.put("errMsg", "");
        if (reqParams.containsKey("test")) {
            result.put("flag", "success");
            writeResponse(resp, result);
            return;
        }
        final String fireInstanceId = (String) reqParams.remove("fireInstanceId");
        if (StringUtils.isEmpty(fireInstanceId)) {
            String errMsg = "Parameter fireInstanceId is empty, access is denied.";
            logger.warn(errMsg);
            result.put("flag", "failure");
            result.put("errMsg", errMsg);
            writeResponse(resp, result);
            return;
        }
        String brokerClassName = (String) reqParams.remove("brokerClassName");
        if (StringUtils.isEmpty(brokerClassName)) {
            String errMsg = "Parameter brokerClassName not found.";
            logger.warn(errMsg);
            result.put("flag", "failure");
            result.put("errMsg", errMsg);
            writeResponse(resp, result);
            return;
        }
        JobBroker jobBroker;
        try {
            jobBroker = getJobBroker(brokerClassName);
        } catch (Exception ex) {
            String errMsg = "Can not find broker Class named " + brokerClassName;
            logger.warn(errMsg, ex);
            result.put("flag", "failure");
            result.put("errMsg", errMsg);
            writeResponse(resp, result);
            return;
        }
        String jobGroup = (String) reqParams.remove("jobGroup");
        String jobName = (String) reqParams.remove("jobName");
        String triggerGroup = (String) reqParams.remove("triggerGroup");
        String triggerName = (String) reqParams.remove("triggerName");
        Boolean concurrentExectionDisallowed = (Boolean) reqParams.remove("concurrentExectionDisallowed");
        JobExecutionContext jobExecutionContext = new JobExecutionContext(fireInstanceId, jobGroup, jobName, triggerGroup, triggerName, jobBroker, reqParams, concurrentExectionDisallowed == null ? false : concurrentExectionDisallowed);
        // JobCmd maybe duplicate because of http is unstable.
        // (request processed on server, but response not get by requestor)
        if (fireInstances.putIfAbsent(fireInstanceId, jobExecutionContext) != null) {
            String errMsg = "FireInstanceId " + fireInstanceId + " is duplicate.";
            logger.warn(errMsg);
            result.put("flag", "failure");
            result.put("errMsg", errMsg);
            writeResponse(resp, result);
            return;
        }
        try {
            addToJobThreadPool(jobExecutionContext);
            result.put("flag", "success");
            writeResponse(resp, result);
        } catch (RejectedExecutionException ex) {
            fireInstances.remove(fireInstanceId);
            String errMsg = "All Job thread is busy,access denied, please try other node," + " fireInstanceId is " + fireInstanceId + ".";
            result.put("flag", "failure");
            result.put("errMsg", errMsg);
            writeResponse(resp, result);
            return;
        }
    }

    private void addToJobThreadPool(final JobExecutionContext jobExecutionContext) {
        taskExecutor.execute(new Runnable() {
            public void run() {
                if (jobExecutionContext.acquireExecutingLock()) {
                    try {
                        if (jobExecutionContext.isExecuted()) return;
                        String fireInstanceId = jobExecutionContext.getFireInstanceId();
                        logger.info("Job run in threadPool," + " which fireInstanceId is " + fireInstanceId);
                        try {
                            boolean validateResult = validateJobCmd(fireInstanceId);
                            if (validateResult) {
                                jobExecutionContext.setValidated(true);
                            } else {
                                jobExecutionContext.setExecuted(true);
                                fireInstances.remove(fireInstanceId);
                                logger.warn("JobCmd which FireInstanceId is " + fireInstanceId + ", not valid, has been discard.");
                                return;
                            }
                        } catch (UnDetermineResultException ex) {
                            logger.info("Job validate get unDetermined result, will try later.");
                            // return this time, run next time.
                            return;
                        }
                        logger.info("Job has pass command validate," + " which fireInstanceId is " + fireInstanceId);
                        boolean isConcurrentExectionDisallowed = jobExecutionContext.isConcurrentExectionDisallowed();
                        String key = jobExecutionContext.getJobGroup() + jobExecutionContext.getJobName();
                        try {
                            JobBroker jobBroker = jobExecutionContext.getJobBroker();
                            if (isConcurrentExectionDisallowed) {
                                String old = concurrentMonitorMap.putIfAbsent(key, fireInstanceId);
                                if (old != null) {
                                    logger.error("***************Concurrent error************" + "old:" + old + ",current:" + fireInstanceId);
                                }
                            }
                            jobBroker.execute(servletContext, jobExecutionContext);
                            jobExecutionContext.setExecuted(true);
                        } catch (Throwable ex) {
                            logger.error("Exception occur when Job executing.", ex);
                            jobExecutionContext.setExecuted(true);
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            Throwable rootException = ExceptionUtils.getRootCause(ex);
                            jobExecutionContext.setRootExceptionClassName(rootException.getClass().getName());
                            String rootMsg = StringUtils.isEmpty(rootException.getMessage()) ? "No exception message." : rootException.getMessage();
                            jobExecutionContext.setRootExceptionMsg(StringUtils.substring(rootMsg, 0, 1000));
                            jobExecutionContext.setExceptionStackTrace(sw.toString());
                        } finally {
                            if (isConcurrentExectionDisallowed) {
                                concurrentMonitorMap.remove(key);
                            }
                            List<JobExecutionContext> contexts = new ArrayList<JobExecutionContext>();
                            contexts.add(jobExecutionContext);
                            reportJobExecution(contexts);
                        }
                    } finally {
                        jobExecutionContext.releaseExecutingLock();
                    }
                }
            }
        });
    }

    private JobBroker getJobBroker(String brokerClassName) throws Exception {
        JobBroker jobBroker = jobBrokerMap.get(brokerClassName);
        if (jobBroker == null) {
            Class clazz = Class.forName(brokerClassName);
            Object object = clazz.newInstance();
            if (!(object instanceof JobBroker)) {
                throw new Exception("Class named " + brokerClassName + " is not a JobBroker.");
            }
            jobBroker = (JobBroker) object;
            jobBrokerMap.putIfAbsent(brokerClassName, jobBroker);
        }
        return jobBroker;
    }

    private void reportJobExecution(Collection<JobExecutionContext> contexts) {
        if (contexts.isEmpty()) return;
        String nodeName = getNodeName();
        List<Map<String, Object>> instants = new ArrayList<Map<String, Object>>();
        List<String> executedFireInstanceIds = new ArrayList<String>();
        for (JobExecutionContext context : contexts) {
            Map<String, Object> fireInstance = new HashMap<String, Object>();
            fireInstance.put("fireInstanceId", context.getFireInstanceId());
            fireInstance.put("nodeName", nodeName);
            fireInstance.put("jobGroup", context.getJobGroup());
            fireInstance.put("jobName", context.getJobName());
            fireInstance.put("triggerGroup", context.getTriggerGroup());
            fireInstance.put("triggerName", context.getTriggerName());
            fireInstance.put("executed", context.isExecuted());
            if (context.isExecuted()) {
                executedFireInstanceIds.add(context.getFireInstanceId());
            }
            if (!StringUtils.isEmpty(context.getExceptionStackTrace())) {
                fireInstance.put("exceptionStackTrace", context.getExceptionStackTrace());
                fireInstance.put("rootExceptionClassName", context.getRootExceptionClassName());
                fireInstance.put("rootExceptionMsg", context.getRootExceptionMsg());
            }
            instants.add(fireInstance);
        }
        List<String> reportUrlList = propertiesGetter.getReportUrlList();
        try {
            List unCompletedInstances = (List) HttpClient.sendByPost(reportUrlList, instants, 30000, 3, propertiesGetter.getProxyHost(), propertiesGetter.getProxyPort());
            if (unCompletedInstances != null && !unCompletedInstances.isEmpty()) {
                for (Object unCompletedInstance : unCompletedInstances) {
                    logger.warn("Uts server can not handler report of instance:" + unCompletedInstance);
                }
            }
            for (String instanceId : executedFireInstanceIds) {
                if (unCompletedInstances == null || !unCompletedInstances.contains(instanceId))
                    fireInstances.remove(instanceId);
            }
        } catch (IOException ex) {
            String toString = StringUtils.join(reportUrlList.toArray(), ",");
            logger.info("IOException occur when report job execution,reportUrlList is " + toString, ex);
        } catch (Exception ex) {
            logger.error("Exception occur when report job execution.", ex);
        }
    }

    private boolean validateJobCmd(String fireInstanceId) throws UnDetermineResultException {
        Map<String, String> data = new HashMap<String, String>();
        data.put("fireInstanceId", fireInstanceId);
        data.put("nodeName", getNodeName());
        for (int i = 0; i < 10; i++) {
            int interval = 500;
            List<String> validateUrlList = propertiesGetter.getValidateUrlList();
            try {
                Thread.sleep(interval);
                Object result = HttpClient.sendByPost(validateUrlList, data, 30000, propertiesGetter.getProxyHost(), propertiesGetter.getProxyPort());
                // Result is null, instructing retry after interval.
                if (result == null) {
                    logger.info("Job validate " + i + " get no result, " + "will try after " + interval + "ms, fireInstanceId is " + fireInstanceId);
                }
                if (result != null && result instanceof Boolean) {
                    return ((Boolean) result);
                }
            } catch (IOException ex) {
                String toString = StringUtils.join(validateUrlList.toArray(), ",");
                logger.info("Job validate " + i + " get IOException:" + ex.getMessage() + ", " + "will try after " + interval + "ms, fireInstanceId is " + fireInstanceId + ", " + "validateUrlList is " + toString);
            } catch (Exception ex) {
                logger.warn("Exception occur when validate JobCmd," + " access is denied, fireInstanceId is " + fireInstanceId, ex);
                return false;
            }
        }
        throw new UnDetermineResultException();
    }

    private class UnDetermineResultException extends Exception {

        private static final long serialVersionUID = -628701132185125642L;

        private UnDetermineResultException() {
        }
    }

    private String getNodeName() {
        String nodeIp = NetUtil.getLocalIP();
        String processorId = ManagementFactory.getRuntimeMXBean().getName();
        if (processorId != null) {
            processorId = processorId.split("@")[0];
        }
        return nodeIp + "/" + processorId;
    }

    private Map readRequest(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            Object obj = ois.readObject();
            if (!(obj instanceof Map)) {
                throw new IOException("Deserialized object needs to be assignable to type [" + Map.class.getName() + "]: " + obj);
            }
            return (Map) obj;
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        } finally {
            ois.close();
        }
    }

    private void writeResponse(HttpServletResponse response, Object result) throws IOException {
        response.setContentType(CONTENT_TYPE_SERIALIZED_OBJECT);
        ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
        try {
            oos.writeObject(result);
        } finally {
            oos.close();
        }
    }

    private class CheckerThread extends Thread {

        private final long sleepMillis;

        private volatile boolean shutdown = false;

        public CheckerThread(long sleepMillis, String name) {
            super(name);
            this.sleepMillis = sleepMillis;
        }

        synchronized void shutdown() {
            shutdown = true;
            // XXX: We might want to consider calling interrupt() here but I'm not sure how effective it will be
            notifyAll();
        }

        public void run() {
            while (true) {
                if (shutdown) return;
                List<String> needRemoved = new ArrayList<String>();
                for (JobExecutionContext context : fireInstances.values()) {
                    if (!context.hasValidated()) {
                        // Ignore context received in 5s, to simple avoid concurrent conflict with
                        // process-main-thread's (repeat addToJobThreadPool).
                        if (context.getReceivedTime() + 1000 * 5 > System.currentTimeMillis()) continue;
                        // Not pass validation in 30 min.
                        if (context.getReceivedTime() < System.currentTimeMillis() - 1000 * 60 * 30) {
                            needRemoved.add(context.getFireInstanceId());
                            logger.warn("FireInstanceId " + context.getFireInstanceId() + " not pass validation in 30 min, has been discard.");
                        } else {
                            try {
                                addToJobThreadPool(context);
                            } catch (RejectedExecutionException ex) {
                                // ignore the ex.
                            }
                        }
                    }
                }
                for (String fireInstanceId : needRemoved) {
                    fireInstances.remove(fireInstanceId);
                }
                reportJobExecution(fireInstances.values());
                try {
                    changeNameAndSleep(sleepMillis);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private synchronized void changeNameAndSleep(long time) throws InterruptedException {
            String prevName = getName();
            setName(prevName + " (sleeping for " + time + " milliseconds, starting from " + new Date() + ")");
            try {
                wait(time);
            } finally {
                setName(prevName);
            }
        }
    }
}
