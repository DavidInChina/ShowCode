package com.davidinchina.showcode.lightload.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * author:davidinchina on 2017/9/27 12:47
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des: 请求接口类
 */
public interface HttpServerApi {
    @Streaming
    @GET
    Call<ResponseBody> downloadLatestFeature(@Url String fileUrl);

    @GET
    Observable<ResponseBody> downloadPicFromNet(@Url String fileUrl);
}
