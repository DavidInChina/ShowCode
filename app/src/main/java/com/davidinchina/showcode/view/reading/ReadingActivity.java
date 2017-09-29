package com.davidinchina.showcode.view.reading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.readingview.readview.ReadingView;
import com.davidinchina.showcode.readingview.view.SearchWordPopupWindow;

public class ReadingActivity extends Activity {
    private Context mContext;
    private TextView tvTitle;
    private TextView tvBack;
    private ReadingView atv;

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
        atv = findViewById(R.id.readView);
        atv.setText(R.string.str_reading_content);
        atv.setChooseListener(new ReadingView.WordChooseListener() {
            @Override
            public void chooseWord(String word) {
                word = word.replaceAll("[^a-zA-Z]", "");
                //这里要开始查询
                Toast.makeText(mContext, word, Toast.LENGTH_SHORT).show();
                new SearchWordPopupWindow(mContext).showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            }
        });
    }

}
