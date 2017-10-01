package com.davidinchina.showcode.readingview.view;


import com.davidinchina.showcode.readingview.net.ApiServices;
import com.davidinchina.showcode.readingview.utils.APiUtil;

import rx.Observable;

/**
 * author:davidinchina
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class ReadingModel implements ReadingContract.ReadingModel {
    /**
     * @author davidinchina
     * @description 单词查询
     */
    @Override
    public Observable<WordResult> queryWord(String url) {
        return ApiServices.createApi()
                .queryWord(url)
                .compose((Observable.Transformer)
                        APiUtil.<WordResult>handleResult())
                .compose(APiUtil.<WordResult>rxSchedulerHelper());
    }

    @Override
    public Observable<SentenceResult> querySentence(String url) {
        return ApiServices.createApi()
                .queryWordSentence(url)
                .compose((Observable.Transformer)
                        APiUtil.<SentenceResult>handleResult())
                .compose(APiUtil.<SentenceResult>rxSchedulerHelper());
    }
}
