package com.sncfc.scheduler.server.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 2017/1/18.
 */
public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private String address;
    private int connReqTimeout = 25000;
    private int connTimeout = 25000;
    private int soTimeout = 25000;
    private int maxConn = 100;
    private HttpClient httpClient = null;
    PoolingHttpClientConnectionManager connManager = null;

    public HttpClientUtil(String address){
        this.address = address;
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(this.connTimeout).setConnectionRequestTimeout(this.connReqTimeout).setSocketTimeout(this.soTimeout).build();
        this.connManager = new PoolingHttpClientConnectionManager();
        this.connManager.setMaxTotal(this.maxConn);
        this.httpClient = HttpClientBuilder.create().setConnectionManager(this.connManager).setDefaultRequestConfig(requestConfig).build();
    }
    public HttpClientUtil(String address, int connReqTimeout, int connTimeout, int soTimeout, int maxConn) {
        this.address = address;
        this.connReqTimeout = connReqTimeout;
        this.connTimeout = connTimeout;
        this.soTimeout = soTimeout;
        this.maxConn = maxConn;
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(this.connTimeout).setConnectionRequestTimeout(this.connReqTimeout).setSocketTimeout(this.soTimeout).build();
        this.connManager = new PoolingHttpClientConnectionManager();
        this.connManager.setMaxTotal(this.maxConn);
        this.httpClient = HttpClientBuilder.create().setConnectionManager(this.connManager).setDefaultRequestConfig(requestConfig).build();
    }

    public Map sendReq(Map map) throws Exception {
        HttpPost post = new HttpPost(address);
        CloseableHttpResponse response = null;
        HttpEntity responseEntity = null;
        Map resultMap = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(map);
            byte[] data = baos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            InputStreamEntity en = new InputStreamEntity(bis);
            post.setEntity(en);
            response = (CloseableHttpResponse) this.httpClient.execute(post);
//            System.out.println(response.getStatusLine());
            responseEntity = response.getEntity();
            InputStream inputStream = responseEntity.getContent();
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            Object obj = ois.readObject();
            EntityUtils.consume(responseEntity);
            resultMap = (Map)obj;
        }catch (EOFException e){
//            System.out.println("这是合法的，客户端已经关闭");
        }catch (Exception e) {
            throw e;
        }finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("流关闭异常!", e);
                }
            }
        }
        return resultMap;
    }
//    public static void main(String[] args) throws UnsupportedEncodingException {
//
//        Map map = new HashMap();
//        map.put("foo","bar");
//        map.put("fireInstanceId","123");
//        map.put("brokerClassName","com.suning.framework.uts.client.broker.StaticMethodJobBroker");
//
//        map.put("staticClassName","com.sncfc.test.Test");
//        map.put("methodName","test");
//        Class[] obsTypes = new Class[]{String.class,String.class};
//        Object[] obsValues = new Object[]{"许","欢"};
//        map.put("argumentTypes",obsTypes);
//        map.put("arguments",obsValues);
//
//        Map responseMap = new HttpClientUtil("http://127.0.0.1:8080/quartzClient/jobDispatcher").sendReq(map);
//        System.out.println(responseMap);
//    }
    public static void main(String[] args) throws UnsupportedEncodingException {

        Map map = new HashMap();
        map.put("fireInstanceId","123");
        map.put("brokerClassName","com.suning.framework.uts.client.broker.SpringBeanJobBroker");

        map.put("beanId","springBeanTest");
        map.put("methodName","test");
//        Class[] obsTypes = new Class[]{String.class,String.class};
//        Object[] obsValues = new Object[]{"许","欢"};
//        map.put("argumentTypes",obsTypes);
//        map.put("arguments",obsValues);

//        Map responseMap = new HttpClientUtil("http://127.0.0.1:8080/quartzClient/jobDispatcher").sendReq(map);
//        System.out.println(responseMap);
    }
}