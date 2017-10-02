package com.davidinchina.showcode.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.davidinchina.showcode.R;
import com.davidinchina.showcode.view.adapter.ImgAdapter;
import com.pgyersdk.crash.PgyCrashManager;

import java.util.ArrayList;
import java.util.List;

public class ImgListActivity extends AppCompatActivity {
    private RecyclerView recImgs;
    private TextView tvTitle;
    private TextView tvBack;
    private ImgAdapter adapter;

    public static Intent createIntent(Context begin) {
        Intent intent = new Intent(begin, ImgListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_list);
        initView();
        try  {
            initData();
        } catch (Exception e) {
            PgyCrashManager.reportCaughtException(ImgListActivity.this, e);
        }

    }


    /**
     * @author davidinchina
     * cerate at 2017/9/27 下午2:26
     * description 初始化页面控件
     */
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.tvActionTitle);
        tvTitle.setText(R.string.str_img_list_show);
        tvBack = (TextView) findViewById(R.id.tvActionLeft);
        tvBack.setVisibility(View.VISIBLE);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recImgs = (RecyclerView) findViewById(R.id.recImgs);
        recImgs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImgAdapter(this);
        recImgs.setAdapter(adapter);
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/27 下午2:26
     * description 数据生成
     */
    public void initData() {
        List<String> data = new ArrayList<>();
        data.add("https://static.baydn.com/media/media_store/image/f1672263006c6e28bb9dee7652fa4cf6.jpg");
        data.add("https://static.baydn.com/media/media_store/image/8c997fae9ebb2b22ecc098a379cc2ca3.jpg");
        data.add("https://static.baydn.com/media/media_store/image/2a4616f067285b4bd59fe5401cd7106b.jpeg");
        data.add("https://static.baydn.com/media/media_store/image/b0e3ab329c8d8218d2af5c8dfdc21125.jpg");
        data.add("https://static.baydn.com/media/media_store/image/670abb28408a9a0fc3dd9666e5ca1584.jpeg");
        data.add("https://static.baydn.com/media/media_store/image/1e8d675468ab61f4e5bdebd4bcb5f007.jpeg");
        data.add("https://static.baydn.com/media/media_store/image/9b2f93cbfa104dae1e67f540ff14a4c2.jpg");
        data.add("https://static.baydn.com/media/media_store/image/f5e0631e00a09edbbf2eb21eb71b4d3c.jpeg");
        adapter.addAll(data);
    }
}
