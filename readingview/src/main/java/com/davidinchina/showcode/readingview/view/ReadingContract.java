package com.davidinchina.showcode.readingview.view;


import com.davidinchina.showcode.readingview.base.BaseModel;
import com.davidinchina.showcode.readingview.base.BasePresenter;
import com.davidinchina.showcode.readingview.base.BaseView;

import rx.Observable;

/**
 * author:davidinchina
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public interface ReadingContract {

    /**
     * @author davidinchina
     * @description 数据交互接口声明
     */
    interface ReadingModel extends BaseModel {
        /**
         * 数据交互方法示例
         *
         * @return
         */
        Observable<WordResult> queryWord(String url);

        Observable<SentenceResult> querySentence(String url);
    }

    /**
     * @author davidinchina
     * @description 页面交互接口声明
     */
    interface ReadingView extends BaseView {

        /**
         * 请求用户授权成功返回web鉴权页面
         *
         * @param result
         */
        void queryWord(WordResult result);

        void querySentence(SentenceResult result);
    }

    /**
     * @author davidinchina
     * @description 交互控制接口声明
     */
    abstract class ReadingPresenter extends BasePresenter<ReadingModel, ReadingView> {
        /**
         * 控制交互方法示例
         */
        public abstract void queryWord(String word, String token);

        public abstract void querySentence(String vocabulary_id, String type, String access_token);
    }
}