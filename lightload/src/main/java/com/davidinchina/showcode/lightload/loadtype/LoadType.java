package com.davidinchina.showcode.lightload.loadtype;

import android.graphics.Bitmap;

/**
 * author:davidinchina on 2017/9/27 00:42
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:加载方式的抽象类
 */
public abstract class LoadType {

    /**
     * 从当前加载方式中获取图片
     *
     * @return
     */
    public abstract Bitmap getImg(String key);

    /**
     * 根据加载方式缓存图片
     *
     * @param url
     * @param img
     */
    public abstract void cache(String url, Bitmap img, int width, int height);

    /**
     * @author davidinchina
     * cerate at 2017/9/27 上午10:44
     * description 获取已缓存大小
     */
    public abstract long getCacheSize();

    /**
     * @author davidinchina
     * cerate at 2017/9/27 上午10:44
     * description 清空已缓存数据
     */
    public abstract void clearCache();

    /**
     * @author davidinchina
     * cerate at 2017/9/27 上午11:05
     * description 停止lruCache
     */
    public abstract void stopCache();
}
