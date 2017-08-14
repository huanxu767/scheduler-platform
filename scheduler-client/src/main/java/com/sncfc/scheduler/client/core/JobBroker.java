package com.sncfc.scheduler.client.core;

import javax.servlet.ServletContext;

public interface JobBroker {

    public void execute(ServletContext servletContext, JobExecutionContext jobContext) throws JobExecutionException;

}
