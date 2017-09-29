package com.davidinchina.showcode.readingview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.davidinchina.showcode.readingview.R;


/**
 * @author davidinchina
 *         cerate at 2017/9/30 上午1:22
 *         description 查词框
 */
public class SearchWordPopupWindow extends PopupWindow {


    private Context mContext;
    private View contentView;

    public SearchWordPopupWindow(Context context) {
        super(context);
        this.mContext = context;
        contentView = LayoutInflater.from(context).inflate(R.layout.search_word_window_view, null, false);
        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(false);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
//        contentView.findViewById(R.id.tvClose).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//        contentView.findViewById(R.id.tvSure).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.getChooseArea(areaPicker.getData());
//                dismiss();
//            }
//        });
        backgroundAlpha((Activity) mContext, 1.0f);//0.0-1.0
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) mContext, 1f);
            }
        });

    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
}
