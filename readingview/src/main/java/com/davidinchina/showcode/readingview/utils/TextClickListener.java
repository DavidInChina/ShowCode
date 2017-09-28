package com.davidinchina.showcode.readingview.utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * author:davidinchina on 2017/9/28 19:35
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des:
 */
public class TextClickListener extends ClickableSpan {
    @Override
    public void onClick(View view) {

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.RED);
    }
}
