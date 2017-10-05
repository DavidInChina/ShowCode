package com.davidinchina.showcode.lightload.loadtype;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * author:davidinchina on 2017/10/5 23:57
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class MemoryCache extends LoadType {
    private LruCache<String, Bitmap> imgCache;
    public static MemoryCache cache;

    private MemoryCache(long cacheSize) {
        initCache(cacheSize);
    }

    public static MemoryCache getInstance(long cacheSize) {
        if (null == cache) {
            synchronized (MemoryCache.class) {
                if (null == cache)
                    cache = new MemoryCache(cacheSize);
            }
        }
        return cache;
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/5 下午11:59
     * description 初始化内存缓存
     */
    public void initCache(long cacheSize) {
        long maxMemory = Runtime.getRuntime().maxMemory() / 4;//最大可用值为内存的当前可用内存的四分之一,单位b
        Log.e("TAG", Runtime.getRuntime().maxMemory() + "///" + maxMemory + "///");
        maxMemory = cacheSize > maxMemory ? maxMemory : cacheSize;//取较小值
        imgCache = new LruCache<String, Bitmap>((int) maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };

    }

    @Override
    public Bitmap getImg(String key) {
        if (null != imgCache && imgCache.size() > 0) {
            return imgCache.get(key);
        }
        return null;
    }

    @Override
    public void cache(String url, Bitmap img, int width, int height) {
        if (null != imgCache && null == imgCache.get(url)) {
            imgCache.put(url, img);
        }
    }

    @Override
    public long getCacheSize() {
        long size = 0;//暂不做计算
        return size;
    }

    @Override
    public void clearCache() {
        imgCache.evictAll();
    }

    @Override
    public void stopCache() {
        imgCache = null;
    }
}
