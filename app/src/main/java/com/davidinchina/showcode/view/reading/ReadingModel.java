package com.davidinchina.showcode.view.reading;


import com.davidinchina.showcode.net.ApiServices;
import com.davidinchina.showcode.utils.APiUtil;

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
}
