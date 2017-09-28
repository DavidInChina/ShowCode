package com.davidinchina.showcode.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.readingview.readview.ReadingView;

public class ReadingActivity extends Activity {
    private Context mContext;
    private TextView tvTitle;
    private TextView tvBack;

    public static Intent createIntent(Context begin) {
        Intent intent = new Intent(begin, ReadingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_reading);
        tvTitle = findViewById(R.id.tvActionTitle);
        tvTitle.setText(R.string.str_reading_view);
        tvBack = findViewById(R.id.tvActionLeft);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ReadingView atv = findViewById(R.id.readView);
        atv.setText(R.string.str_reading_content);
    }

//    public void testClick() {
//        SpannableStringBuilder spannable = new SpannableStringBuilder("测试 点击 变色");
//        spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        //这个一定要记得设置，不然点击不生效
//        tvTest.setMovementMethod(LinkMovementMethod.getInstance());
//        spannable.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(mContext, "点击", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                ds.setColor(Color.GREEN);
//            }
//        }, 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvTest.setText(spannable);
//
//    }
}
