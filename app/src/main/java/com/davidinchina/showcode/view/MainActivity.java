package com.davidinchina.showcode.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.view.reading.ReadingActivity;
import com.davidinchina.showcode.view.reading.token.RequestTokenActivity;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewClickEvent;

import java.util.concurrent.TimeUnit;

import easypermission.davidinchina.com.easylibrary.EasyPermission;
import easypermission.davidinchina.com.easylibrary.annotation.OnEasyPermissionFailed;
import easypermission.davidinchina.com.easylibrary.annotation.OnEasyPermissionSuccess;
import rx.functions.Action1;

public class MainActivity extends Activity {
    private Button btnCustomizeView;
    private Button btnLoadImg;
    private Context mContext;
    private TextView tvTitle;
    private static final int REQUEST_CODE = 100;//请求权限code
    private static final int REQUEST_TOKEN_CODE = 100;//请求token code
    private String token = "";//扇贝会话token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/27 上午12:11
     * description 初始化页面控件,设置点击事件
     */
    public void initView() {
        btnCustomizeView = findViewById(R.id.btnCustomizeView);
        btnLoadImg = findViewById(R.id.btnLoadImg);
        tvTitle = findViewById(R.id.tvActionTitle);
        tvTitle.setText(R.string.app_name);
        Action1<ViewClickEvent> clickAction = new Action1<ViewClickEvent>() {
            @Override
            public void call(ViewClickEvent event) {
                switch (event.view().getId()) {

                    case R.id.btnCustomizeView:
                        if (!"".equals(token)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("token", token);
                            startActivity(ReadingActivity.createIntent(mContext, bundle));
                        } else {
                            Bundle bundle = new Bundle();
                            String url = "https://api.shanbay.com/oauth2/authorize/?client_id=ad1c89740bd028ee2a96&response_type=token&redirect_uri=https://api.shanbay.com/oauth2/auth/success/";
                            bundle.putString("url", url);
                            startActivityForResult(RequestTokenActivity.createIntent(mContext, bundle), REQUEST_TOKEN_CODE);
                        }
                        break;
                    case R.id.btnLoadImg:
                        startActivity(ImgListActivity.createIntent(mContext));
                        break;
                }
            }
        };
        RxView.clickEvents(btnCustomizeView) // 以 Observable 形式来反馈点击事件
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(clickAction);
        RxView.clickEvents(btnLoadImg) // 以 Observable 形式来反馈点击事件
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(clickAction);
        requestPermission();
    }

    public void requestPermission() {
        EasyPermission.with(this).code(REQUEST_CODE).request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermission.handleResult(this, requestCode, permissions, grantResults);//处理权限申请回调结果
    }

    @OnEasyPermissionSuccess(REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        Toast.makeText(this, R.string.str_permission_success, Toast.LENGTH_SHORT).show();
    }

    @OnEasyPermissionFailed(REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, R.string.str_permission_failed, Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_TOKEN_CODE:
                    token = data.getExtras().getString("token");
                    Toast.makeText(this, R.string.str_get_token_success, Toast.LENGTH_SHORT).show();
                    break;
            }
    }
}
