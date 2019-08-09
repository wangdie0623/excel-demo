package cn.wang.excel.utils;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

class HttpPoolFactory {
    //从链接池获取链接超时时间5秒
    private static final int GET_POOL_CLIENT_TIME = 5000;
    //链接到服务器超时时间5秒
    private static final int MAX_CONNECT_SERVER_TIME = 5000;
    //响应获取数据超时时间
    private static final int MAX_READ_TIME_OUT = 10 * 1000;
    //默认http客户端配置
    private static final RequestConfig DEFAULT_CONF = RequestConfig.custom()
            .setConnectionRequestTimeout(GET_POOL_CLIENT_TIME)
            .setConnectTimeout(MAX_CONNECT_SERVER_TIME)
            .setSocketTimeout(MAX_READ_TIME_OUT)
            .build();

    private HttpPoolFactory() {
    }

    //http连接池
    private static final PoolingHttpClientConnectionManager POLL_MANGER = getPollManger();

    //获取http连接池对象
    private static PoolingHttpClientConnectionManager getPollManger() {
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager pollManger = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        pollManger.setMaxTotal(200);
        pollManger.setDefaultMaxPerRoute(20);
        return pollManger;
    }


    /**
     * 获取http客户端对象
     *
     * @param cookieStore cookie存储对象
     * @return http客户端对象
     */
    static CloseableHttpClient getHttpClient(CookieStore cookieStore) {
        return HttpClients.custom()
                .setConnectionManager(POLL_MANGER)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(DEFAULT_CONF)
                .build();
    }

}
