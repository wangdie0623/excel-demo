package cn.wang.excel.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 王叠  2019-06-10 10:23
 */
@Slf4j
public class HttpUtils {

    /**
     * 忽略content-type get
     *
     * @param url  请求地址
     * @param data 入参
     * @return 响应结果
     */
    public static String get(String url, Map<String, String> data) {
        CloseableHttpClient client = HttpPoolFactory.getHttpClient(new BasicCookieStore());
        try {
            StringBuilder builder = new StringBuilder();
            if (data != null && !data.isEmpty()) {
                builder.append("?");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    builder.append(entry.getKey() + "=" + entry.getValue() + "&");
                }
                builder.deleteCharAt(builder.length() - 1);
            }
            HttpGet httpGet = new HttpGet(url + builder.toString());
            ResponseHandler<String> handler = getStringHandler();
            return client.execute(httpGet, handler);
        } catch (Exception e) {
            log.error("Get请求异常：" + url + ",params:" + JSON.toJSONString(data), e);
        }
        return null;
    }


    /**
     * 忽略content-type postFrom
     *
     * @param url  请求地址
     * @param data 入参
     * @return 响应结果
     */
    public static String postFrom(String url, Map<String, String> data) {
        try {
            CloseableHttpClient client = HttpPoolFactory.getHttpClient(new BasicCookieStore());
            HttpPost httpPost = new HttpPost(url);
            List<BasicNameValuePair> list = new ArrayList<>();
            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            if (!list.isEmpty()) {
                httpPost.setEntity(new UrlEncodedFormEntity(list, Charset.forName("UTF-8")));
            }
            ResponseHandler<String> handler = getStringHandler();
            return client.execute(httpPost, handler);
        } catch (Exception e) {
            log.error("POST FROM请求异常：" + url + ",params:" + JSON.toJSONString(data), e);
        }
        return null;
    }


    /**
     * post JSON
     * @param url 请求地址
     * @param json  json格式请求入参
     * @return 响应结果
     */
    public static String postJSON(String url, String json) {
        CloseableHttpClient client = HttpPoolFactory.getHttpClient(new BasicCookieStore());
        try {
            HttpPost httpPost = new HttpPost(url);
            if (json != null && !json.isEmpty()) {
                ContentType type = ContentType.APPLICATION_JSON;
                type.withCharset("UTF-8");
                httpPost.setEntity(new StringEntity(json, type));
            }
            ResponseHandler<String> handler = getStringHandler();
            return client.execute(httpPost, handler);
        } catch (Exception e) {
            log.error("POST JSON请求异常：" + url + ",params:" + json, e);
        }
        return null;
    }
    /**
     * post文件请求
     *
     * @param url   请求地址
     * @param data  文本入参
     * @param files 文件入参
     * @return 响应结果
     */
    public static String postFiles(String url, Map<String, String> data, Map<String, byte[]> files) {
        CloseableHttpClient client = HttpPoolFactory.getHttpClient(new BasicCookieStore());
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(RequestConfig
                    .custom()
                    .setSocketTimeout(10 * 1000)
                    .setConnectTimeout(3 * 1000)
                    .build());
            httpPost.setEntity(getEntity(data, files));
            ResponseHandler<String> handler = getStringHandler();
            return client.execute(httpPost, handler);
        } catch (Exception e) {
            log.error("上传文件请求异常:" + url + ",data:" + data + ",files:" + files, e);
        }
        return null;
    }

    /**
     * 得到文件入参对象
     *
     * @param data  文本入参
     * @param files 文件入参
     * @return 响应结果
     */
    private static HttpEntity getEntity(Map<String, String> data, Map<String, byte[]> files) {
        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentType contentType = ContentType.TEXT_PLAIN.withCharset("UTF-8");
        if (data != null && !data.isEmpty()) {
            for (Map.Entry<String, String> item : data.entrySet()) {
                entity.addTextBody(item.getKey(), item.getValue(), contentType);
            }
        }
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, byte[]> item : files.entrySet()) {
                entity.addBinaryBody(item.getKey(), item.getValue());
            }
        }
        return entity.build();
    }

    /**
     * 获取自定义字符串响应handler
     *
     * @return 响应结果Handler对象
     */
    private static ResponseHandler<String> getStringHandler() {
        return response -> {
            StatusLine statusLine = response.getStatusLine();
            log.info("响应状态:" + statusLine.getStatusCode());
            HttpEntity entity = response.getEntity();
            return entity == null ? null : EntityUtils.toString(entity);
        };
    }
}

