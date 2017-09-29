package com.davidinchina.showcode.readingview.readview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davidinchina.showcode.readingview.R;

/**
 * author:davidinchina on 2017/9/27 23:57
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des: 继承Linearlayout的文本阅读视图
 */
public class ReadingView extends LinearLayout {
    private int marginAndPaddingOfLeftRight = dip2px(20);//文本域左右已使用的间距值
    private int lineSpacingExtra = dip2px(5);//文本行距
    private int offset = dip2px(0);//偏差值,用作于误差调整
    private int textSize = dip2px(13);//字体大小
    private int textColor = Color.BLACK;// 字体颜色
    private LayoutParams layoutParamsMW;
    private TextView lastView;
    private WordChooseListener listener;

    public interface WordChooseListener {
        public void chooseWord(String word);
    }

    public ReadingView(Context context) {
        this(context, null);
    }

    public ReadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AlignedTextView);
            marginAndPaddingOfLeftRight = ta.getDimensionPixelSize(R.styleable.AlignedTextView_atv_marginAndPaddingOfLeftRight, marginAndPaddingOfLeftRight);
            lineSpacingExtra = ta.getDimensionPixelSize(R.styleable.AlignedTextView_atv_lineSpacingExtra, lineSpacingExtra);
            offset = ta.getDimensionPixelSize(R.styleable.AlignedTextView_atv_offset, offset);
            textSize = ta.getDimensionPixelSize(R.styleable.AlignedTextView_atv_textSize, textSize);
            textColor = ta.getColor(R.styleable.AlignedTextView_atv_textColor, textColor);
            ta.recycle();
        }
        init();
    }

    private void init() {
        layoutParamsMW = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        layoutParamsMW.bottomMargin = lineSpacingExtra;
    }

    public void setText(int resId) {
        setText(getResources().getString(resId));
    }

    public void setChooseListener(WordChooseListener listener) {
        this.listener = listener;
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/29 上午12:00
     * description 设置所有数据到页面
     */
    public void setText(String content) {
        int usedWidth = marginAndPaddingOfLeftRight + offset;//已使用宽度
        int remainWidth = getContext().getResources().getDisplayMetrics().widthPixels - usedWidth;//可放置宽度
        int lineWordNum = remainWidth / (textSize / 2);//大致的行数，还有空格造成的空隙，暂时用空格填充解决
        if (lineWordNum <= 0) {
            return;
        }
        String[] paraArray = content.split("\\n+");//段落分开
        for (String para : paraArray) {
            setPara(para, lineWordNum, remainWidth);
            addEmptyLine();
        }

    }

    /**
     * @author davidinchina
     * cerate at 2017/9/29 上午12:00
     * description 处理每一个段落
     */
    public void setPara(String content, int lineWordNum, int remainWidth) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        content = content.trim();
        int contentLength = content.length();
        int index = 0;//计算下标
        while (index < contentLength) {
            if (contentLength - index <= lineWordNum) {
                String single = content.substring(index, contentLength);
                addOneLineContent(single, remainWidth, false);//单行文本,最后一样不作对齐操作
                return;
            }
            String lineContent = content.substring(index, index + lineWordNum);
            String next = content.substring(index + lineWordNum, index + lineWordNum + 1);
            if (!next.equals(" ") && !lineContent.endsWith(" ")) {//下一行非空格开始,此一行非空格结束，则单词中断
                lineContent = lineContent.substring(0, lineContent.lastIndexOf(" ") + 1);//截取最后一个空格之前
            }
            index = index + lineContent.length();//更新下标
            addOneLineContent(lineContent, remainWidth, true);//添加一行数据到页面
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/28 下午11:57
     * description 添加一行数据
     */
    private void addOneLineContent(String content, int remainWidth, boolean justify) {
        if (content.startsWith(" ")) {
            content = content.substring(1);
        }
        if (content.endsWith(" ")) {
            content = content.substring(0, content.length() - 1);
        }
        TextView lineText = new TextView(getContext());
        lineText.setMovementMethod(LinkMovementMethod.getInstance());
        lineText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        lineText.setTextColor(textColor);
        if (justify) {
            justify(lineText, content, remainWidth);
        } else {//最后一行同样处理点击事件
            lineText.setText(addClickablePart(content, lineText));
        }
        lineText.setLayoutParams(layoutParamsMW);
        avoidHintColor(lineText);
        addView(lineText);
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/28 下午11:56
     * description  添加段落间空串
     */
    private void addEmptyLine() {
        TextView lineText = new TextView(getContext());
        lineText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        lineText.setTextColor(textColor);
        lineText.setText(" ");
        lineText.setLayoutParams(layoutParamsMW);
        addView(lineText);
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/29 上午12:14
     * description 单行文本左右对齐
     */
    public void justify(TextView textView, String content, float contentWidth) {
        Paint paint = textView.getPaint();
        //需要插入的空格个数
        int totalSpacesToInsert = (int) ((contentWidth - paint.measureText(content)) / paint.measureText(" "));
        content = justifyLine(content, totalSpacesToInsert);
        textView.setText(addClickablePart(content, textView));
    }

    //已填入最多单词数的一行，插入对应的空格数直到该行满
    private String justifyLine(String text, int totalSpacesToInsert) {
        String[] wordArray = text.split("\\s");
        String toAppend = " ";
        while ((totalSpacesToInsert) >= (wordArray.length - 1)) {
            toAppend = toAppend + " ";//根据需要插入的空格个数来拼出最终插入的空字符串
            totalSpacesToInsert = totalSpacesToInsert - (wordArray.length - 1);
        }
        int i = 0;
        String justifiedText = "";
        for (String word : wordArray) {
            if (i < totalSpacesToInsert)//剩余空格数量不足每一间距添加，添加到前若干个间隔
                justifiedText = justifiedText + word + " " + toAppend;
            else
                justifiedText = justifiedText + word + toAppend;
            i++;
        }
        return justifiedText;
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/29 上午12:36
     * description 单词点击
     */
    private SpannableStringBuilder addClickablePart(String str, final TextView current) {
        current.setTag(str);//保存数据
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        String[] words = str.split("\\s");
        if (words.length > 0) {
            for (int i = 0; i < words.length; i++) {
                final String word = words[i];
                final int start = str.indexOf(word);
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        //清除旧数据状态，添加到新数据
                        if (null != lastView && lastView != current) {
                            String content = (String) lastView.getTag();
                            lastView.setText(addClickablePart(content, lastView));

                        }
                        lastView = current;
                        if (null == listener) {
                            throw new IllegalArgumentException("选择监听器没有初始化");
                        } else {
                            listener.chooseWord(word);
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }

                }, start, start + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ssb;
    }

    private void avoidHintColor(View view) {
        if (view instanceof TextView)
            ((TextView) view).setHighlightColor(getResources().getColor(R.color.chosed));
    }

    private int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
