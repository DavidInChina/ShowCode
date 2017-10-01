package com.davidinchina.showcode.view.reading.token;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.readingview.utils.LogUtil;

public class RequestTokenActivity extends Activity {
    private WebView webView;
    String url = "";
    private Context mContext;
    private TextView tvTitle;
    private TextView tvBack;

    public static Intent createIntent(Context begin, Bundle bundle) {
        Intent intent = new Intent(begin, RequestTokenActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getExtras().getString("url");
        setContentView(R.layout.activity_web_view);
        this.mContext = this;
        tvTitle = findViewById(R.id.tvActionTitle);
        tvTitle.setText(R.string.str_get_token);
        tvBack = findViewById(R.id.tvActionLeft);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(url);//加载页面
    }

    @Override
    public void onBackPressed() {
        if (null != webView) {
            String redirectUrl = webView.getUrl();
            LogUtil.e("TAG", redirectUrl);
            if (!url.equals(redirectUrl) && redirectUrl.contains("access_token")) {
                //已经重定向，并且有token 可以返回code
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                String token = redirectUrl.substring(redirectUrl.indexOf("=") + 1, redirectUrl.indexOf("&"));//截取token值
                bundle.putString("token", token);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
            } else {
                //没有授权成功，返回失败
                setResult(RESULT_CANCELED, null);
            }
            finish();
        }
    }
}
