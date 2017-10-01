package com.davidinchina.showcode.readingview.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.davidinchina.showcode.readingview.R;
import com.davidinchina.showcode.readingview.net.ApiException;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewClickEvent;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;


/**
 * @author davidinchina
 *         cerate at 2017/9/30 上午1:22
 *         description 查词框
 */
public class SearchWordPopupWindow extends PopupWindow implements ReadingContract.ReadingView {
    private Context mContext;
    private View contentView;
    private TextView tvClose;
    private TextView tvWord;//单词
    private TextView tvRead;//音标
    private ImageView ivReadWord;//阅读按钮
    private TextView tvMean;//单词释义
    private TextView tvSentence1;//例句1
    private TextView tvCNSentence1;//翻译1
    private TextView tvSentence2;//例句2
    private TextView tvCNSentence2;//翻译2
    private ReadingPresenter presenter;
    private String token = "";
    private MediaPlayer player;//播放器


    public SearchWordPopupWindow(Context context) {
        super(context);
        this.mContext = context;
        contentView = LayoutInflater.from(context).inflate(R.layout.search_word_window_view, null, false);
        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.setFocusable(false);//弹出后页面可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) mContext, 1.0f);//0.0-1.0
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (isPlaying()) {//退出查词框则停止播放
                    stopPlaying();
                }
            }
        });
        initView();
        initNet();
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午4:52
     * description 释放播放器
     */
    public void releasePlayer() {
        if (null != player) {
            player.release();
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午2:34
     * description 初始化页面控件
     */
    private void initView() {
        tvClose = contentView.findViewById(R.id.tvClose);
        tvWord = contentView.findViewById(R.id.tvWord);
        tvRead = contentView.findViewById(R.id.tvRead);
        ivReadWord = contentView.findViewById(R.id.ivReadWord);
        tvMean = contentView.findViewById(R.id.tvMean);
        tvSentence1 = contentView.findViewById(R.id.tvSentence1);
        tvCNSentence1 = contentView.findViewById(R.id.tvCNSentence1);
        tvSentence2 = contentView.findViewById(R.id.tvSentence2);
        tvCNSentence2 = contentView.findViewById(R.id.tvCNSentence2);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initAudioPlay();
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午4:38
     * description 点击播放音频事件处理
     */
    public void initAudioPlay() {
        Action1<ViewClickEvent> clickAction = new Action1<ViewClickEvent>() {
            @Override
            public void call(ViewClickEvent event) {
                int i = event.view().getId();
                if (i == R.id.ivReadWord) {
                    String audio = (String) event.view().getTag();
                    if (null != audio) {
                        if (isPlaying()) {
                            stopPlaying();
                        }
                        startPlaying(audio);
                    }
                }
            }
        };
        RxView.clickEvents(ivReadWord)//防止快速多次点击
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(clickAction);
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午2:34
     * description 清空页面已有数据
     */
    private void resetView() {
        tvWord.setText("");
        tvRead.setText("");
        tvMean.setText("");
        tvSentence1.setText("");
        tvCNSentence1.setText("");
        tvSentence2.setText("");
        tvCNSentence2.setText("");
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午2:24
     * description 初始化网络请求
     */
    private void initNet() {
        presenter = new ReadingPresenter();
        presenter.attachV(mContext, this);
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午2:25
     * description 根据传入的单词和token进行查询
     */
    public void queryWord(String word, String token) {
        this.token = token;
        resetView();
        //传入一个单词，查词框进行查询并展示
        presenter.queryWord(word, token);
    }

    @Override
    public void showError(Throwable msg) {//交互失败
        if (msg instanceof ApiException) {
            ApiException exception = (ApiException) msg;
            Toast.makeText(mContext, exception.getErrorMsg(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.str_netword_failed, Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    @Override
    public void queryWord(WordResult result) {//获取搭单词成功
//        Toast.makeText(mContext, "查询成功" + result.getCn_definition().getDefn(), Toast.LENGTH_SHORT).show();
        tvWord.setText(result.getContent());
        tvRead.setText("/" + result.getPronunciation() + "/");
        tvMean.setText(result.getDefinition().substring(1));
        ivReadWord.setTag(result.getAudio());//设置阅读到页面
        presenter.querySentence(result.getId() + "", "sys", token);
    }

    @Override
    public void querySentence(SentenceResult result) {//获取例句成功
        if (result.getList().size() > 1) {//硬编码显示前两个例句
            String sentence1 = result.getList().get(0).getAnnotation();
            String word = sentence1.substring(sentence1.indexOf(">") + 1, sentence1.indexOf("</"));
            sentence1 = sentence1.substring(0, sentence1.indexOf("<")) +
                    "<strong><font color=\"#209e85\">" + word + "</font></strong>" +
                    sentence1.substring(sentence1.lastIndexOf(">") + 1, sentence1.length());
            tvSentence1.setText(Html.fromHtml(sentence1));
            tvCNSentence1.setText(result.getList().get(0).getTranslation());

            String sentence2 = result.getList().get(1).getAnnotation();
            String word2 = sentence2.substring(sentence2.indexOf(">") + 1, sentence2.indexOf("</"));
            sentence2 = sentence2.substring(0, sentence2.indexOf("<")) +
                    "<strong><font color=\"#209e85\">" + word2 + "</font></strong>" +
                    sentence2.substring(sentence2.lastIndexOf(">") + 1, sentence2.length());
            tvSentence2.setText(Html.fromHtml(sentence2));
            tvCNSentence2.setText(result.getList().get(1).getTranslation());
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午4:19
     * description 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午4:18
     * description 判断当前是否在播放音频
     */
    private boolean isPlaying() {
        try {
            return player != null && player.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午4:18
     * description 开始流媒体播放
     */
    private void startPlaying(String audioPath) {
        try {
            if (null == player) {
                player = new MediaPlayer();
            } else {
                player.reset();
            }
            player.setDataSource(audioPath);
            player.prepare();
            player.start();
            ivReadWord.setSelected(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ivReadWord.setSelected(false);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午4:18
     * description 关闭音频播放
     */
    private void stopPlaying() {
        if (player != null) {
            try {
                player.stop();
                ivReadWord.setSelected(false);
            } catch (Exception e) {
            }
        }

    }
}
