package com.sncfc.scheduler.client.properties;


import com.sncfc.scheduler.client.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertiesDefaultGetter implements PropertiesGetter {


    private static final int DEFAULT_PROXY_PORT = 8080;
    
    private String proxyHost;
    
    private int proxyPort = DEFAULT_PROXY_PORT;

    private List<String> validateUrlList = new ArrayList<String>();

    private List<String> reportUrlList = new ArrayList<String>();
    
    private int coreJobThread;
    
    private int maxJobThread;
    
    private int jobQueueSize;
    
    @Override
    public void init(Properties props) throws Exception {
        String utsServerBaseUrl = props.getProperty("utsServerBaseUrl");
        if (utsServerBaseUrl == null || utsServerBaseUrl.trim().equals(""))
            throw new Exception("utsServerBaseUrl must be set in uts.properties.");
        List<String> baseUrls = Arrays.asList(StringUtils.split(utsServerBaseUrl, ","));
        for (String url : baseUrls) {
            validateUrlList.add(StringUtils.removeEnd(StringUtils.trimAllWhitespace(url)
                    , "/") + "/jobCmdValidate");
        }
        for (String url : baseUrls) {
            reportUrlList.add(StringUtils.removeEnd(StringUtils.trimAllWhitespace(url)
                    , "/") + "/jobExecutionReport");
        }
        String proxyAddr = props.getProperty("proxyAddress");
        if (proxyAddr != null && proxyAddr.trim().length() > 0) {
            String[] proxyAddrSplit = proxyAddr.trim().split(":");
            proxyHost = proxyAddrSplit[0].trim();
            if (proxyAddrSplit.length == 2) {
                proxyPort = Integer.parseInt(proxyAddrSplit[1].trim());
            }
        }
        String coreJobThreadStr = props.getProperty("coreJobThread");
        coreJobThread = (StringUtils.isEmpty(coreJobThreadStr)) ? 2 : Integer.parseInt(coreJobThreadStr);
        String maxJobThreadStr = props.getProperty("maxJobThread");
        maxJobThread = (StringUtils.isEmpty(maxJobThreadStr)) ? 10 : Integer.parseInt(maxJobThreadStr);
        String jobQueueSizeStr = props.getProperty("jobQueueSize");
        jobQueueSize = (StringUtils.isEmpty(jobQueueSizeStr)) ? 0 : Integer.parseInt(jobQueueSizeStr);
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
    public List<String> getValidateUrlList() {
        return validateUrlList;
    }

    @Override
    public List<String> getReportUrlList() {
        return reportUrlList;
    }

    @Override
    public int getCoreJobThread() {
        return coreJobThread;
    }

    @Override
    public int getMaxJobThread() {
        return maxJobThread;
    }

    @Override
    public int getJobQueueSize() {
        return jobQueueSize;
    }

}
