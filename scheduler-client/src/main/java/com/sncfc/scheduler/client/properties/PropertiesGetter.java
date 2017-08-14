package com.sncfc.scheduler.client.properties;

import java.util.List;
import java.util.Properties;

public interface PropertiesGetter {

    public void init(Properties props) throws Exception;
    
    public List<String> getValidateUrlList();
    
    public List<String> getReportUrlList();
    
    public String getProxyHost();

    public int getProxyPort();
    
    public int getCoreJobThread();
    
    public int getMaxJobThread();
    
    public int getJobQueueSize();
}
