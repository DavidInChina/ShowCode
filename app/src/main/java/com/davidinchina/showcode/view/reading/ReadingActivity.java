package com.davidinchina.showcode.view.reading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.readingview.readview.ReadingView;
import com.davidinchina.showcode.readingview.view.SearchWordPopupWindow;

public class ReadingActivity extends Activity {
    private Context mContext;
    private TextView tvTitle;
    private TextView tvBack;
    private ReadingView atv;
    private String token = "";
    SearchWordPopupWindow popupWindow;

    public static Intent createIntent(Context begin, Bundle bundle) {
        Intent intent = new Intent(begin, ReadingActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = getIntent().getExtras().getString("token");
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
                if (!"".equals(word))
                    querySingleWord(word);
            }
        });
    }


    public void querySingleWord(String word) {
        if (null == popupWindow) {
            popupWindow = new SearchWordPopupWindow(mContext);
        }
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        }
        popupWindow.queryWord(word, token);
    }

    @Override
    public void onBackPressed() {
        if (null != popupWindow && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != popupWindow) {//释放播放器
            popupWindow.releasePlayer();
        }
    }
}
