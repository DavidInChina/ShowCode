package com.davidinchina.showcode.readingview.readview;


import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.davidinchina.showcode.readingview.utils.LogUtil;

/**
 * author:davidinchina on 2017/10/1 20:20
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class ClickableMovementMethod extends BaseMovementMethod {

    private static ClickableMovementMethod sInstance;
    private int lastbegin = 0;
    private int lastEnd = 0;

    public static ClickableMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ClickableMovementMethod();
        }
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getActionMasked();
//        switch (action) {
//            case MotionEvent.ACTION_UP:
//                break;
//            case MotionEvent.ACTION_DOWN:
//                break;
//        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();
            x += widget.getScrollX();
            y += widget.getScrollY();
            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);
            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    LogUtil.debug("up:" + lastbegin + "///" + lastEnd + buffer.toString());
                    if (lastEnd != 0) {
//                        LocalSelection.setSelection(buffer, lastbegin,
//                                lastEnd);//弹起才会触发Selection背景色改变
                        lastbegin = 0;
                        lastEnd = 0;
                    }
                    link[0].onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    LogUtil.debug("Action:" + action + "///");
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                    LogUtil.debug("down:" + lastbegin + "///" + lastEnd);
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }

}
