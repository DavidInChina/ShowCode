package com.davidinchina.showcode.readingview.net;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
* @author davidinchina
* cerate at 2017/9/27 下午4:48
* description 网络请求api
*/
public class ApiServices {
    //读取新连接超时时间
    private static final int TIMEOUT_READ = 20;
    //请求超时时间超时时间
    private static final int TIMEOUT_CONNECTION = 10;

    //日志请求和响应信息拦截器
    private static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    /**
     * 默认构建器
     */
    private static OkHttpClient.Builder okHttpBuilder = newBuilder(new OkHttpClient().newBuilder());

    /**
     * 默认构建器
     */
    public static OkHttpClient.Builder newBuilder(OkHttpClient.Builder builder) {
        return builder
                //time out
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                //失败重连
                .retryOnConnectionFailure(true);
    }

    /**
     * 创建请求响应
     *
     * @return
     */
    public static HttpServerApis createApi() {
        okHttpBuilder.addInterceptor(interceptor);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.shanbay.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //添加Rxjava
                .addConverterFactory(LocalGsonConverterFactory.create())
                .client(okHttpBuilder.build())
                .build();
        return retrofit.create(HttpServerApis.class);
    }

}

