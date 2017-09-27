package com.davidinchina.showcode.app;

import android.app.Application;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.lightload.load.LightLoad;
import com.davidinchina.showcode.lightload.load.LoadingSetting;

/**
 * author:davidinchina on 2017/9/27 00:00
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:应用上下文
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化某些数据
        LoadingSetting loadingSetting = new LoadingSetting();
        loadingSetting.setApp(this).setCacheRoot("Images").
                setCacheSize(20 * 1024 * 1024). setLoadWay(LoadingSetting.DISK_CACHE)
                .setDefaultRes(R.mipmap.icon_default);
        LightLoad.initInstance(loadingSetting);//初始化图片加载库
    }

}
