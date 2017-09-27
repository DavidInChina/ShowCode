package com.davidinchina.showcode.lightload.loadtype;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.davidinchina.showcode.lightload.R;
import com.davidinchina.showcode.lightload.utils.BitmapUtil;
import com.davidinchina.showcode.lightload.utils.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author:davidinchina on 2017/9/27 09:30
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:磁盘缓存加载方式实现
 */
public class DiskCache extends LoadType {
    private DiskLruCache lruCache;
    public static DiskCache cache;
    //硬盘缓存
    private File cacheDir;
    private Context mContext;
    private long cacheSize;

    private DiskCache(Context mContext, String cacheRoot, long cacheSize) {
        this.mContext = mContext;
        this.cacheSize = cacheSize;
        try {
            //获取文件缓存路径
            cacheDir = getDiskCacheDir(mContext, cacheRoot);
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            //创建DiskLruCache实例，初始化硬盘缓存
            lruCache = DiskLruCache.open(cacheDir, getAppVersion(mContext), 1, cacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * DCL获取单例
     *
     * @param mContext
     * @param cacheRoot
     * @return
     */
    public static DiskCache getInstance(Context mContext, String cacheRoot, long cacheSize) {
        if (null == cache) {
            synchronized (DiskCache.class) {
                if (null == cache)
                    cache = new DiskCache(mContext, cacheRoot, cacheSize);
            }
        }
        return cache;
    }

    /**
     * 根据传入的unique返回硬盘缓存地址
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //当有SD卡时
            cachePath = context.getExternalCacheDir().getParent();
        } else {
            //当没有SD卡或SD卡被移除
            cachePath = context.getCacheDir().getParent();
        }
        Log.e("TAG", "getDiskCacheDir: " + cachePath + File.separator + uniqueName);
        return new File(cachePath + File.separator + uniqueName);
    }

    @Override
    public Bitmap getImg(String key) {
        try {
            DiskLruCache.Snapshot snapshot = lruCache.get(hashKeyForDisk(key));
            if (null != snapshot && null != snapshot.getInputStream(0)) {
                return BitmapFactory.decodeStream(snapshot.getInputStream(0));//获取唯一的缓存数据
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void cache(String url, Bitmap img, int width, int height) {
        try {
            String key = hashKeyForDisk(url);
            DiskLruCache.Editor editor = lruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                boolean result = false;
                String fileExt = url.substring(url.lastIndexOf("."));
                img = BitmapUtil.compressScale(img, width, height);//宽高压缩
                if (fileExt.equals(".jpg") || fileExt.equals(".jpeg")) {
                    result = img.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else if (fileExt.equals(".png")) {
                    result = img.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } else if (fileExt.equals(".webp")) {
                    result = img.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
                } else {
                    throw new IllegalArgumentException(mContext.getString(R.string.str_illegal_img));
                }
                if (result) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public long getCacheSize() {
        if (lruCache != null) {
            return lruCache.size();
        }
        return 0;
    }

    @Override
    public void clearCache() {
        if (lruCache != null) {
            try {
                lruCache.delete();
                lruCache = DiskLruCache.open(cacheDir, getAppVersion(mContext), 1, cacheSize);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void stopCache() {
        try {
            lruCache.flush();
            lruCache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用MD5算法对传入的key进行加密，以免出现url不合法
     *
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
            e.printStackTrace();
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                builder.append("0");
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    /**
     * 获取当前应用程序版本号
     *
     * @param context
     * @return
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
