package com.sncfc.scheduler.client.util;

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;


public abstract class HttpClient {

    private static final int SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE = 1024;

    public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-java-serialized-object";

    private static final String HTTP_METHOD_POST = "POST";

    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    private static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";

    private static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding";

    private static final String ENCODING_GZIP = "gzip";

    public static Object sendByPost(List<String> urlList, Object data, int timeout) throws Exception {
        return sendByPost(urlList, data, timeout, 1, 0, null, null, 8080);
    }

    public static Object sendByPost(List<String> urlList, Object data,
                                    int timeout, int retry, int interval) throws Exception {
        return sendByPost(urlList, data, timeout, retry, interval, null, null, 8080);
    }

    public static Object sendByPost(List<String> urlList, Object data,
                                    int timeout, int retry) throws Exception {
        return sendByPost(urlList, data, timeout, retry, 0, null, null, 8080);
    }
    
    public static Object sendByPost(List<String> urlList, Object data, int timeout, String proxyHost, int proxyPort) throws Exception {
        return sendByPost(urlList, data, timeout, 1, 0, null, proxyHost, proxyPort);
    }

    public static Object sendByPost(List<String> urlList, Object data,
                                    int timeout, int retry, int interval, String proxyHost, int proxyPort) throws Exception {
        return sendByPost(urlList, data, timeout, retry, interval, null, proxyHost, proxyPort);
    }

    public static Object sendByPost(List<String> urlList, Object data,
                                    int timeout, int retry, String proxyHost, int proxyPort) throws Exception {
        return sendByPost(urlList, data, timeout, retry, 0, null, proxyHost, proxyPort);
    }

    public static Object sendByPost(List<String> urlList, Object data, int timeout,
                                    int retry, int interval, RetryCallBack callback, String proxyHost, int proxyPort) throws Exception {
        Collections.shuffle(urlList);
        Exception exception = null;
        for (int i = 0; i < retry; i++) {
            try {
                Object newData = data;
                if (i != 0 && callback != null) {
                    newData = callback.onRetry(data);
                }
                String url = urlList.get(i % urlList.size());
                ByteArrayOutputStream baos = getByteArrayOutputStream(newData);
                // Not use proxy to prevent: Software caused connection abort: socket write error
                // proxy 可选
                Proxy proxy = proxyHost == null ? Proxy.NO_PROXY : new Proxy(
                        Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                URLConnection connection = new URL(url).openConnection(proxy);
                if (!(connection instanceof HttpURLConnection)) {
                    exception = new Exception("Service URL [" + url + "] is not an HTTP URL");
                    break;
                }
                HttpURLConnection con = (HttpURLConnection) connection;
                prepareConnection(con, baos.size(), timeout);
                writeRequestBody(con, baos);
                validateResponse(con);
                InputStream responseBody = readResponseBody(con);
                return readResult(responseBody);
            } catch (Exception ex) {
                exception = ex;
                // If IOException, retry
                if (!(ex instanceof IOException)) break;
                if (interval > 0) {
                    Thread.sleep(interval);
                }
            }
        }
        throw exception;
    }

    public interface RetryCallBack {

        // can modify data on each retry.
        public Object onRetry(Object data);

    }

    private static ByteArrayOutputStream getByteArrayOutputStream(Object data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(SERIALIZED_INVOCATION_BYTE_ARRAY_INITIAL_SIZE);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        try {
            oos.writeObject(data);
        } finally {
            oos.close();
        }
        return baos;
    }

    private static void prepareConnection(HttpURLConnection connection, int contentLength,
                                          int timeout) throws IOException {
        if (timeout >= 0) {
            connection.setConnectTimeout(timeout);
        }
        if (timeout >= 0) {
            connection.setReadTimeout(timeout);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod(HTTP_METHOD_POST);
        connection.setRequestProperty(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_SERIALIZED_OBJECT);
        connection.setRequestProperty(HTTP_HEADER_CONTENT_LENGTH, Integer.toString(contentLength));
    }

    private static void writeRequestBody(HttpURLConnection con,
                                         ByteArrayOutputStream baos) throws IOException {
        baos.writeTo(con.getOutputStream());
    }

    private static void validateResponse(HttpURLConnection con)
            throws IOException {

        if (con.getResponseCode() >= 300) {
            throw new IOException(
                    "Did not receive successful HTTP response: status code = " + con.getResponseCode() +
                            ", status message = [" + con.getResponseMessage() + "]");
        }
    }

    private static InputStream readResponseBody(HttpURLConnection con) throws IOException {
        if (isGzipResponse(con)) {
            // GZIP response found - need to unzip.
            return new GZIPInputStream(con.getInputStream());
        } else {
            // Plain response found.
            return con.getInputStream();
        }
    }

    private static boolean isGzipResponse(HttpURLConnection con) {
        String encodingHeader = con.getHeaderField(HTTP_HEADER_CONTENT_ENCODING);
        return (encodingHeader != null && encodingHeader.toLowerCase().contains(ENCODING_GZIP));
    }

    private static Object readResult(InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
    }
}
