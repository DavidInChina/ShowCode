package com.davidinchina.showcode.readingview.utils;


import com.davidinchina.showcode.readingview.net.HttpEmptyResultFunc;
import com.davidinchina.showcode.readingview.net.HttpResultFunc;
import com.davidinchina.showcode.readingview.net.JsonEntity;

import rx.Observable;
import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */

public class APiUtil {

    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
     */
    public static <T> Transformer<T, T> rxSchedulerHelper() {    //compose简化线程
        return new Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        };
    }

    /**
     * 统一返回数据请求结果处理
     *
     * @param <T>
     * @return
     */
    public static <T> Transformer<JsonEntity<T>, T> handleResult() {   //compose判断结果
        return new Transformer<JsonEntity<T>, T>() {
            @Override
            public Observable<T> call(Observable<JsonEntity<T>> jsonEntityObservable) {
                return jsonEntityObservable.map(new HttpResultFunc<T>());
            }
        };
    }
    /**
     * 统一返回数据请求结果处理
     *
     * @return
     */
    public static Transformer<JsonEntity, JsonEntity> handleEmptyResult() {   //compose判断结果
        return new Transformer<JsonEntity, JsonEntity>() {
            @Override
            public Observable<JsonEntity> call(Observable<JsonEntity> jsonEntityObservable) {
                return jsonEntityObservable.map(new HttpEmptyResultFunc());
            }
        };
    }



}
