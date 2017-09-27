package com.davidinchina.showcode.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.lightload.load.LightLoad;

import java.util.ArrayList;
import java.util.List;

/**
 * author:davidinchina on 2017/9/27 14:01
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des: 图片列表适配器
 */
public class ImgAdapter extends RecyclerView.Adapter {
    protected LayoutInflater mInflater;
    private List<String> imgs;
    private Context mContext;

    public ImgAdapter(Context mContext) {
        super();
        this.mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.imgs = new ArrayList<>();
    }

    public void addAll(List<String> imgs) {
        if (imgs != null) {
            this.imgs.addAll(imgs);
            notifyItemRangeInserted(this.imgs.size(), imgs.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AdapterHolder(mInflater.inflate(R.layout.item_img_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AdapterHolder adapterHolder = (AdapterHolder) holder;
        adapterHolder.initView(getItem(position));
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    public String getItem(int position) {
        if (position < 0 || position >= imgs.size())
            return "";
        return imgs.get(position);
    }

    class AdapterHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        FrameLayout flWrapper;

        public AdapterHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.ivImg);
            flWrapper = itemView.findViewById(R.id.flWrapper);
            ViewGroup.LayoutParams params = flWrapper.getLayoutParams();
            params.height = getScreenHeight(mContext)/3;
            flWrapper.setLayoutParams(params);
        }

        public int getScreenHeight(Context context) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return dm.heightPixels;
        }

        public void initView(String url) {
            LightLoad.instance.with(mContext).load(url).into(ivImg);
        }
    }
}
