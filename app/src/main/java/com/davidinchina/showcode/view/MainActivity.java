package com.davidinchina.showcode.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.davidinchina.showcode.R;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button btnCustomizeView;
    private Button btnLoadImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        btnCustomizeView.setOnClickListener(this);
        btnLoadImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCustomizeView:
                Toast.makeText(this, "展示自定义阅读视图", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLoadImg:
                Toast.makeText(this, "展示图片加载", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
