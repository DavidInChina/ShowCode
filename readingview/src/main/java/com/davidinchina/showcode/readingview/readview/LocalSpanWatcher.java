package com.davidinchina.showcode.readingview.readview;

import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.widget.TextView;

/**
 * author:davidinchina on 2017/10/7 00:29
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class LocalSpanWatcher implements SpanWatcher {
    private TextView tv;
    private ClickableSpan selectedSpan = null;

    public LocalSpanWatcher(TextView tv) {
        this.tv = tv;
    }

    private void changeColor(Spannable text, Object what, int start, int end) {
        if (what == Selection.SELECTION_END) {
            ClickableSpan[] spans = text.getSpans(start, end, ClickableSpan.class);
            if (spans != null) {
                if (selectedSpan != null) {
                }
                selectedSpan = spans[0];
            }
        }
    }

    @Override
    public void onSpanAdded(Spannable text, Object what, int start, int end) {
        changeColor(text, what, start, end);
    }

    @Override
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        changeColor(text, what, nstart, nend);
    }

    @Override
    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
    }
}