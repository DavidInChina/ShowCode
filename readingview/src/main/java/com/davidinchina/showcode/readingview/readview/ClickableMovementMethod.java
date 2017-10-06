package com.davidinchina.showcode.readingview.readview;


import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

import com.davidinchina.showcode.readingview.R;

/**
 * author:davidinchina on 2017/10/1 20:20
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class ClickableMovementMethod extends BaseMovementMethod {

    private static ClickableMovementMethod sInstance;
    private TouchableSpan mPressedSpan;
    private int downX = 0;
    private int downY = 0;


    public ClickableMovementMethod() {
    }

    public static ClickableMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ClickableMovementMethod();
        }
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getActionMasked();
        int x = (int) event.getX();
        int y = (int) event.getY();
        int deltaX = x - downX;
        int deltaY = y - downY;
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (Math.abs(deltaX) < 20 && Math.abs(deltaY) < 20) {
                    //down和up点在一定范围内，判断是点击
                    if (mPressedSpan != null) {
                        int begin = buffer.getSpanStart(mPressedSpan);
                        int end = buffer.getSpanEnd(mPressedSpan);
                        widget.setTag(R.id.buffer_begin, begin);
                        mPressedSpan.onClick(widget);
                        mPressedSpan = null;
                        return true;
                    }
                } else {
                    Selection.removeSelection(buffer);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mPressedSpan = getPressedSpan(widget, buffer, event);
                if (null != mPressedSpan) {
                    mPressedSpan.setPressed(true);

                } else {
                    Selection.removeSelection(buffer);
                }
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(deltaX) > 20 || Math.abs(deltaY) > 20) {//距离横向或者纵向超过50，则视为滑动
                    TouchableSpan touchedSpan = getPressedSpan(widget, buffer, event);
                    if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                        mPressedSpan.setPressed(false);
                        mPressedSpan = null;
                        Selection.removeSelection(buffer);
                    }
                }
                break;
        }
        return false;
    }


    private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();
        x += textView.getScrollX();
        y += textView.getScrollY();
        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);
        TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
        TouchableSpan touchedSpan = null;
        if (link.length > 0) {
            touchedSpan = link[0];
        }
        return touchedSpan;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.removeSelection(text);
    }
}
