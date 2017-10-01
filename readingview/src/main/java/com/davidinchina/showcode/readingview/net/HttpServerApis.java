package com.davidinchina.showcode.readingview.net;


import com.davidinchina.showcode.readingview.view.SentenceResult;
import com.davidinchina.showcode.readingview.view.WordResult;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * author:davidinchina on 2017/10/1 10:46
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des: 扇贝API请求接口
 */
public interface HttpServerApis {


    @GET
    Observable<JsonEntity<WordResult>> queryWord(@Url String url);

    @GET
    Observable<JsonEntity<SentenceResult>> queryWordSentence(@Url String url);
}
