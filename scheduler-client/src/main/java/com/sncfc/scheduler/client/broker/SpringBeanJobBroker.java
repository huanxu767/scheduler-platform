package com.sncfc.scheduler.client.broker;

import com.sncfc.scheduler.client.core.JobBroker;
import com.sncfc.scheduler.client.core.JobExecutionContext;
import com.sncfc.scheduler.client.core.JobExecutionException;
import com.sncfc.scheduler.client.util.StringUtils;

import javax.servlet.ServletContext;
import java.lang.reflect.Method;


public class SpringBeanJobBroker implements JobBroker {

    public void execute(ServletContext servletContext, JobExecutionContext jobContext) throws JobExecutionException {
        String beanId = (String) jobContext.getData("beanId");
        if (StringUtils.isEmpty(beanId)) {
            throw new JobExecutionException("Can not find beanId in jobExecutionContext data.");
        }
        String methodName = (String) jobContext.getData("methodName");
        if (StringUtils.isEmpty(methodName)) {
            throw new JobExecutionException("Can not find methodName in jobExecutionContext data.");
        }
        try {
            Class webApplicationContextUtilsClass =
                    Class.forName("org.springframework.web.context.support.WebApplicationContextUtils");
            Method getWebAppContextMethod = webApplicationContextUtilsClass.getMethod
                    ("getWebApplicationContext", ServletContext.class);
            Object appContext = getWebAppContextMethod.invoke(null, servletContext);
            Method getBeanMethod = appContext.getClass().getMethod("getBean", String.class);
            Object bean = getBeanMethod.invoke(appContext, beanId);
            Class[] argumentTypes = (Class[]) jobContext.getData("argumentTypes");
            if (argumentTypes == null || argumentTypes.length == 0) {
                Method method = bean.getClass().getMethod(methodName);
                method.invoke(bean);
            } else {
                Method method = bean.getClass().getMethod(methodName, argumentTypes);
                Object[] arguments = (Object[]) jobContext.getData("arguments");
                method.invoke(bean, arguments);
            }
        } catch (Exception ex) {
            throw new JobExecutionException(ex);
        }
    }
}
