package com.davidinchina.showcode.readingview.readview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * author:davidinchina on 2017/10/6 22:52
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class LocalTextView extends TextView {
    public LocalTextView(Context context) {
        super(context);
    }

    public LocalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

}