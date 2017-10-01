package com.davidinchina.showcode.readingview.view;


import com.davidinchina.showcode.readingview.net.SimpleSubscriber;

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
    public void queryWord(String word, String token) {
        String url = "https://api.shanbay.com/bdc/search/?word=" + word + "&access_token=" + token;
        Subscription subscription = mModel.queryWord(url).
                subscribe(new SimpleSubscriber<WordResult>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
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

    @Override
    public void querySentence(String vocabulary_id, String type, String access_token) {
        String url = "https://api.shanbay.com/bdc/example/?vocabulary_id=" + vocabulary_id + "&type=" + type + "&access_token=" + access_token;
        Subscription subscription = mModel.querySentence(url).
                subscribe(new SimpleSubscriber<SentenceResult>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mView.showError(e);
                    }

                    @Override
                    public void onNext(SentenceResult result) {
                        super.onNext(result);
                        mView.querySentence(result);
                    }
                });
        mRxManager.add(subscription);
    }
}
