package com.davidinchina.showcode.readingview.net;


import com.davidinchina.showcode.readingview.utils.LogUtil;

import rx.Subscriber;

/**
 * @author davidinchina
 *  cerate at 2017/9/30 下午6:18
 *  description  RxJava 基类
 */
public class SimpleSubscriber<T>
        extends Subscriber<T> {
    @Override
    public void onCompleted() {
        LogUtil.d("showcode----->>", "完成请求");
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.d("showcode----->>", "完成错误");
    }

    @Override
    public void onNext(T t) {
        LogUtil.d("showcode----->>", "请求完成");
    }
}
