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
import com.pgyersdk.crash.PgyCrashManager;

public class ReadingActivity extends Activity {
    private Context mContext;
    private TextView tvTitle;
    private TextView tvBack;
    private TextView tvNext;
    private ReadingView readingView;
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
        tvNext = findViewById(R.id.tvActionRight);
        tvNext.setVisibility(View.VISIBLE);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        readingView = findViewById(R.id.readView);
        readingView.setChooseListener(new ReadingView.WordChooseListener() {
            @Override
            public void chooseWord(String word) {
                word = word.replaceAll("[^a-zA-Z]", "");
                //这里要开始查询
                if (!"".equals(word))
                    querySingleWord(word);
            }
        });
        readingView.setPageChangeListener(new ReadingView.PageChangeListener() {
            @Override
            public void choosePage(int page, int maxPage) {
                tvNext.setText(page + "/" + maxPage + getString(R.string.str_page));
            }
        });
        try {
            readingView.setText(R.string.str_reading_content);
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(ReadingActivity.this, e);
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 下午12:21
     * description 查词框查询单个单词
     */
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
