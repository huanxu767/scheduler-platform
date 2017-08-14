package com.sncfc.scheduler.client.broker;

import com.sncfc.scheduler.client.core.JobBroker;
import com.sncfc.scheduler.client.core.JobExecutionContext;
import com.sncfc.scheduler.client.core.JobExecutionException;
import com.sncfc.scheduler.client.util.StringUtils;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.util.Map;


public class CommenceJobBroker implements JobBroker {

    protected static String CLASS_NAME = "com.suning.schedule.cmd.Controller.SNWebController";

    public void execute(ServletContext servletContext, JobExecutionContext jobContext) throws JobExecutionException {
        String commandClassName = (String) jobContext.getData("commandClassName");
        if (StringUtils.isEmpty(commandClassName)) {
            throw new JobExecutionException("Can not find commandClassName in jobExecutionContext data.");
        }
        try {
            Class clazz = Class.forName(CLASS_NAME);
            Class[] parameterTypes = {String.class, String.class, Integer.class, Long.class};
            Object[] paramObjects = new Object[4];
            Method method = clazz.getMethod("executeAction", parameterTypes);
            paramObjects[0] = commandClassName;
            String queryStr = (String) jobContext.getData("queryStr");
            paramObjects[1] = queryStr == null ? "" : queryStr;
            paramObjects[2] = 10052;
            paramObjects[3] = -1000l;
            Object result = method.invoke(clazz.newInstance(), paramObjects);
            if (result != null) {
                Map resultMap = (Map) result;
                Boolean success = (Boolean) resultMap.get("isSuccess");
                if (success != null && !success) {
                    throw new Exception("Exception occur when Command executing:" + resultMap.get("expInfo"));
                }
            }
        } catch (Exception ex) {
            throw new JobExecutionException(ex);
        }
    }
}
