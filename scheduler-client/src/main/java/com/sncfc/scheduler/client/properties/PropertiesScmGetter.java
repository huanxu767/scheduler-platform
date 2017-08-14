package com.sncfc.scheduler.client.properties;

import com.sncfc.scheduler.client.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PropertiesScmGetter implements PropertiesGetter {

    public static final String PROPERTIES_VALUE_NULL = "_NULL_VALUE";

    private static final Logger logger = LoggerFactory.getLogger(PropertiesScmGetter.class.getName());

    private static final String ATTRIBUTE_PROPERTIES_FILE = "uts.properties";

    private static final String UTS_BASE_URL_KEY = "utsServerBaseUrl";

    private static final String PROXY_ADDR_KEY = "proxyAddress";

    private static final int DEFAULT_PROXY_PORT = 8080;

    private Map<String, String> properties = new HashMap<String, String>();

    private final List<String> validateUrlList = new ArrayList<String>();

    private final List<String> reportUrlList = new ArrayList<String>();

    private String proxyHost;

    private int proxyPort = DEFAULT_PROXY_PORT;

    @Override
    public void init(Properties props) throws Exception {
//        String scmFileName = props.getProperty("scmFileName");
//        if (StringUtils.isEmpty(scmFileName)) {
//            scmFileName = ATTRIBUTE_PROPERTIES_FILE;
//        }
//        SCMClient scmClient = SCMClientImpl.getInstance();
//        SCMNode node = scmClient.getConfig(scmFileName);
//        node.sync();
//        String propertiesContent = node.getValue();
//        try {
//            save(propertiesContent);
//        } catch (Exception e1) {
//            logger.error("Exception occurred when handle new_content form scm. Exception is [{}]", e1);
//        }
//        node.monitor(new SCMListener() {
//            @Override
//            public void execute(String oldValue, String newValue) {
//                save(newValue);
//            }
//        });
    }

    public int getProperty(String name, int defaultValue) {
        String value = getProperty(name, null);
        return StringUtils.isEmpty(value) ? defaultValue : Integer.valueOf(value);
    }

    public String getProperty(String name, String defaultValue) {
        String value = properties.get(name);
        return StringUtils.isEmpty(value) || PROPERTIES_VALUE_NULL.equals(value) ? defaultValue : value;
    }

    private void save(String propertiesContent) {
        if (StringUtils.isEmpty(propertiesContent)) {
            return;
        }
        String content = propertiesContent.replaceAll(" +", "");
        String[] keysvalues = content.split("\n");
        for (String keyvalue : keysvalues) {
            if (!StringUtils.isEmpty(keyvalue) && keyvalue.contains("=")) {
                String[] strs = keyvalue.split("=");
                String key = strs[0];
                String value = strs.length > 1 ? strs[1] : PROPERTIES_VALUE_NULL;
                properties.put(key, value);
                if (UTS_BASE_URL_KEY.equalsIgnoreCase(key)) {
                    processUrls(value);
                }
                if (PROXY_ADDR_KEY.equalsIgnoreCase(key)) {
                    processProxyAddrs(value);
                }
            }
        }
    }

    private void processUrls(String urls) {
        if (StringUtils.isEmpty(urls)) {
            return;
        }
        List<String> baseUrls = Arrays.asList(StringUtils.split(urls, ","));
        synchronized (validateUrlList) {
            validateUrlList.clear();
            for (String url : baseUrls) {
                validateUrlList.add(StringUtils.removeEnd(StringUtils.trimAllWhitespace(url), "/") + "/jobCmdValidate");
            }
        }
        synchronized (reportUrlList) {
            reportUrlList.clear();
            for (String url : baseUrls) {
                reportUrlList.add(StringUtils.removeEnd(StringUtils.trimAllWhitespace(url), "/") + "/jobExecutionReport");
            }
        }
    }

    private void processProxyAddrs(String proxyAddrs) {
        if (StringUtils.isEmpty(proxyAddrs)) {
            return;
        }
        String[] proxyAddrSplit = proxyAddrs.trim().split(":");
        proxyHost = proxyAddrSplit[0].trim();
        if (proxyAddrSplit.length == 2) {
            proxyPort = Integer.parseInt(proxyAddrSplit[1].trim());
        }
    }

    @Override
    public List<String> getValidateUrlList() {
        synchronized (validateUrlList) {
            if (validateUrlList.isEmpty()) {
                String utsServerBaseUrl = getProperty(UTS_BASE_URL_KEY, null);
                if(!StringUtils.isEmpty(utsServerBaseUrl)){
                    processUrls(utsServerBaseUrl);
                }
            }
            return validateUrlList;
        }
    }

    @Override
    public List<String> getReportUrlList() {
        synchronized (reportUrlList) {
            if (reportUrlList.isEmpty()) {
                String utsServerBaseUrl = getProperty(UTS_BASE_URL_KEY, null);
                if(!StringUtils.isEmpty(utsServerBaseUrl)){
                    processUrls(utsServerBaseUrl);
                }
            }
            return reportUrlList;
        }
    }

    @Override
    public String getProxyHost() {
        return proxyHost;
    }

    @Override
    public int getProxyPort() {
        return proxyPort;
    }

    @Override
    public int getCoreJobThread() {
        return getProperty("coreJobThread", 2);
    }

    @Override
    public int getMaxJobThread() {
        return getProperty("maxJobThread", 10);
    }

    @Override
    public int getJobQueueSize() {
        return getProperty("jobQueueSize", 0);
    }
}
