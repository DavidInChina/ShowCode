package com.davidinchina.showcode.lightload.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.davidinchina.showcode.lightload.R;
import com.davidinchina.showcode.lightload.api.ApiService;
import com.davidinchina.showcode.lightload.api.HttpServerApi;
import com.davidinchina.showcode.lightload.loadtype.LoadType;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * author:davidinchina on 2017/9/27 00:43
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des: 轻量加载工具
 */
public class LightLoad {
    private static final String TAG = "LightLoad";
    //当前待加载的view
    private ImageView currentImage;
    //要加载的图片地址
    private String url;
    //上下文
    private Context mContext;
    //图片缓存方式
    private int loadWay;
    //图片缓存工具
    private LoadType loadType;
    //默认加载图片资源id
    private int defaultView = 0;
    //默认使用单例方式
    public static LightLoad instance;

    private static final int LOAD_SUCCESS = 0;
    private static final int LOAD_FAILED = 1;

    public void setCurrentImage(ImageView currentImage) {
        this.currentImage = currentImage;
        if (null == url || "".equals(url)) {
            throw new IllegalArgumentException("img url must have been setted");
        }
        this.currentImage.setTag(R.id.image_url, url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }


    public void setDefaultView(int defaultView) {
        this.defaultView = defaultView;
    }

    private LightLoad() {
    }

    public void setLoadWay(int loadWay) {
        this.loadWay = loadWay;
    }

    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }

    public static LightLoad initInstance(LoadingSetting setting) {
        if (null == instance) {
            synchronized (LightLoad.class) {
                if (null == instance)
                    instance = new LightLoad();
                instance.setLoadWay(setting.getLoadWay());
                instance.setLoadType(setting.getLoadType());
                instance.setDefaultView(setting.getDefaultRes());
            }
        }
        return instance;
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/27 下午12:06
     * description 设置上下文
     */
    public LightLoad with(Context mContext) {
        if (null == instance) {
            throw new RuntimeException("LightLoad must be initialize");
        }
        instance.setmContext(mContext);
        return instance;
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/27 下午12:06
     * description 设置图片地址
     */
    public LightLoad load(String url) {
        if (null == instance) {
            throw new RuntimeException("LightLoad must be initialize");
        }
        instance.setUrl(url);
        return instance;
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/27 下午12:27
     * description 设置目标view
     */
    public void into(ImageView img) {
        if (null == instance) {
            throw new RuntimeException("LightLoad must be initialize");
        }
        instance.setCurrentImage(img);
        instance.beginLoad(currentImage, url, defaultView);
        currentImage.setTag(R.id.image_type, LOAD_FAILED);
        //设置点击重新加载
        currentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = (int) view.getTag(R.id.image_type);
                String tagUrl = (String) view.getTag(R.id.image_url);
                if (type == LOAD_FAILED) {//加载失败的点击可以重新加载
                    view.setTag(R.id.image_type, LOAD_SUCCESS);
                    instance.beginLoad((ImageView) view, tagUrl, defaultView);
                }
            }
        });
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/27 下午12:30
     * description 开始加载
     */
    public void beginLoad(final ImageView imageView, final String url, final int defaultView) {
        final String tagUrl = (String) imageView.getTag(R.id.image_url);//避免加载错乱
        if (null == tagUrl || !tagUrl.equals(url)) {
            imageView.setTag(url);
        }
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                final Bitmap bitmap = loadType.getImg(url);//获取缓存
                if (null != bitmap) {
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Throwable());
                }
            }
        }).subscribeOn(Schedulers.io()) // 切换图片加载至IO线程
                .observeOn(AndroidSchedulers.mainThread()) //切换图片显示在主线程
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Bitmap drawable) {
                        imageView.setImageBitmap(drawable);
                        imageView.setTag(R.id.image_type, LOAD_SUCCESS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        HttpServerApi serviceApi = ApiService.createApi();
                        serviceApi.downloadPicFromNet(url)
                                .subscribeOn(Schedulers.newThread())//在新线程中实现该方法
                                .map(new Func1<ResponseBody, Bitmap>() {
                                    @Override
                                    public Bitmap call(ResponseBody arg0) {
                                        if (null != arg0.byteStream()) {//保存图片成功
                                            Bitmap bitmap = BitmapFactory.decodeStream(arg0.byteStream());
                                            if (null != bitmap) {
                                                //根据控件宽高来压缩缓存图片
                                                loadType.cache(url, bitmap, imageView.getWidth(), imageView.getHeight());//缓存图片
                                            }
                                            return bitmap;//返回一个bitmap对象
                                        }
                                        return null;
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())//在主线程中展示
                                .subscribe(new Subscriber<Bitmap>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable arg0) {
                                        if (0 != defaultView) {
                                            imageView.setImageDrawable(mContext.getDrawable(defaultView));
                                            imageView.setTag(R.id.image_type, LOAD_FAILED);
                                        }
                                    }

                                    @Override
                                    public void onNext(Bitmap arg0) {
                                        if (null != arg0) {
                                            imageView.setImageBitmap(arg0);
                                            imageView.setTag(R.id.image_type, LOAD_SUCCESS);
                                        } else if (0 != defaultView) {
                                            imageView.setImageDrawable(mContext.getDrawable(defaultView));
                                            imageView.setTag(R.id.image_type, LOAD_FAILED);
                                        }
                                    }
                                });
                    }
                });
    }

}
