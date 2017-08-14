package com.sncfc.scheduler.client.broker;

import com.sncfc.scheduler.client.core.JobBroker;
import com.sncfc.scheduler.client.core.JobExecutionContext;
import com.sncfc.scheduler.client.core.JobExecutionException;
import com.sncfc.scheduler.client.util.StringUtils;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;


public class StaticMethodJobBroker implements JobBroker {

    public void execute(ServletContext servletContext, JobExecutionContext jobContext) throws JobExecutionException {
        String staticClassName = (String) jobContext.getData("staticClassName");
        if (StringUtils.isEmpty(staticClassName)) {
            throw new JobExecutionException("Can not find staticClassName in jobExecutionContext data.");
        }
        String methodName = (String) jobContext.getData("methodName");
        if (StringUtils.isEmpty(methodName)) {
            throw new JobExecutionException("Can not find methodName in jobExecutionContext data.");
        }
        try {
            Class clazz = Class.forName(staticClassName);
            Class[] argumentTypes = (Class[]) jobContext.getData("argumentTypes");
            if (argumentTypes == null || argumentTypes.length == 0) {
                Method method = clazz.getMethod(methodName);
                method.invoke(null);
            } else {
                Method method = clazz.getMethod(methodName, argumentTypes);
                Object[] arguments = (Object[]) jobContext.getData("arguments");
                method.invoke(null, arguments);
            }
        } catch (Exception ex) {
            throw new JobExecutionException(ex);
        }
    }
}
