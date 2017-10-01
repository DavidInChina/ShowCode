package com.davidinchina.showcode.view.reading;


import com.davidinchina.showcode.net.ApiException;
import com.davidinchina.showcode.net.SimpleSubscriber;

import rx.Subscription;

/**
 * author:davidinchina
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class ReadingPresenter extends ReadingContract.ReadingPresenter {
    @Override
    public <M> M getmModel() {
        return (M) new ReadingModel();
    }


    @Override
    public void queryWord(String url) {
        Subscription subscription = mModel.queryWord(url).
                subscribe(new SimpleSubscriber<WordResult>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (e instanceof ApiException)
                            mView.showError(e);
                    }

                    @Override
                    public void onNext(WordResult result) {
                        super.onNext(result);
                        mView.queryWord(result);
                    }
                });
        mRxManager.add(subscription);
    }
}
