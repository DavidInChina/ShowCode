package com.davidinchina.showcode.readingview.readview;


import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * author:davidinchina on 2017/10/1 20:20
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class ClickableMovementMethod extends BaseMovementMethod {

    private static ClickableMovementMethod sInstance;
    private int downX = 0;
    private int downY = 0;

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
                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();
                    x += widget.getScrollX();
                    y += widget.getScrollY();
                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);
                    ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                    if (link.length != 0) {
                        link[0].onClick(widget);
                        return true;
                    } else {
                        Selection.removeSelection(buffer);
                    }
                } else {
                    Selection.removeSelection(buffer);
                }
                break;
            case MotionEvent.ACTION_DOWN:
                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();
                x += widget.getScrollX();
                y += widget.getScrollY();
                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);
                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                if (link.length != 0) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                } else {
                    Selection.removeSelection(buffer);
                }
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(deltaX) > 20 || Math.abs(deltaY) > 20) {//距离横向或者纵向超过50，则视为滑动
                    Selection.removeSelection(buffer);
                }
                break;
        }
        return false;
    }

}
