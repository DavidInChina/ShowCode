package com.davidinchina.showcode.lightload.load;

import android.content.Context;

import com.davidinchina.showcode.lightload.loadtype.DiskCache;
import com.davidinchina.showcode.lightload.loadtype.LoadType;

/**
 * author:davidinchina on 2017/9/27 00:43
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:初始化加载工具设置类
 */
public class LoadingSetting {
    public static int NO_CACHE = 100;//不缓存
    public static int DISK_CACHE = 101;//磁盘缓存
    public static int MEMORY_CACHE = 102;//内存缓存
    public static int DOUBLE_CACHE = 103;//内存磁盘双缓存
    private int loadWay;
    private String cacheRoot = "";
    private LoadType loadType;
    private long cacheSize = 0;
    private Context app;
    private int defaultRes = 0;//失败加载图片

    public LoadingSetting setCacheRoot(String cacheRoot) {
        this.cacheRoot = cacheRoot;
        return this;
    }

    public LoadingSetting setDefaultRes(int defaultRes) {
        this.defaultRes = defaultRes;
        return this;
    }

    public int getDefaultRes() {
        return defaultRes;
    }

    public LoadingSetting setLoadWay(int loadWay) {
        this.loadWay = loadWay;
        if (loadWay == DISK_CACHE) {
            if (null == app || "".equals(cacheRoot) || 0 == cacheSize) {
                throw new IllegalArgumentException("params must have been seted");
            }
            this.loadType = DiskCache.getInstance(app, cacheRoot, cacheSize);
        }
        return this;
    }

    public int getLoadWay() {
        return loadWay;
    }

    public LoadingSetting setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }

    public LoadingSetting setApp(Context app) {
        this.app = app;
        return this;
    }

    public String getCacheRoot() {
        return cacheRoot;
    }

    public LoadType getLoadType() {
        return loadType;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public Context getApp() {
        return app;
    }
}
