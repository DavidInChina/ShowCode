package com.davidinchina.showcode.readingview.readview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.davidinchina.showcode.readingview.R;

import java.util.HashMap;
import java.util.Map;

/**
 * author:davidinchina on 2017/9/27 23:57
 * email:davicdinchina@gmail.com
 * version:1.0.0
 * des: 继承Linearlayout的文本阅读视图
 */
public class ReadingView extends LinearLayout {
    private int marginAndPaddingOfLeftRight = dip2px(20);//文本域左右已使用的间距值
    private int marginAndPaddingOfTopDown = dip2px(66);//文本域上下已使用间距
    private int lineSpacingExtra = dip2px(2);//文本行距
    private int offset = 2;//偏差值,用作于误差调整
    private int textSize = sp2px(16);//字体大小
    private int textColor = Color.BLACK;// 字体颜色
    private LayoutParams layoutParamsMW;
    private LocalTextView lastView;
    private WordChooseListener listener;
    private PageChangeListener pageChangeListener;
    private int remainWidth = 0;//宽度
    private int remainHeight = 0;//高度
    private int lineWordNum = 0;//行数
    private int columnWordNum = 0;//列数
    private int mLastXIntercept = 0;//拦截x坐标
    private boolean intercepted = false;
    private int mLastXTouch = 0;//触摸x坐标
    private int page = 1;//当前页，默认第一页
    private int maxPage = 0;//最大页
    private Map<Integer, Integer> pageCount = new HashMap<>();//键值对记录每一页的起始下标
    private String content = "";

    public interface WordChooseListener {
        public void chooseWord(String word);
    }

    public interface PageChangeListener {
        public void choosePage(int page, int maxPage);
    }

    public void setPageChangeListener(PageChangeListener pageChangeListener) {
        this.pageChangeListener = pageChangeListener;
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
            marginAndPaddingOfTopDown = ta.getDimensionPixelOffset(R.styleable.AlignedTextView_atv_marginAndPaddingOfTopDown, marginAndPaddingOfTopDown);
            lineSpacingExtra = ta.getDimensionPixelSize(R.styleable.AlignedTextView_atv_lineSpacingExtra, lineSpacingExtra);
            offset = ta.getDimensionPixelSize(R.styleable.AlignedTextView_atv_offset, offset);
            textSize = ta.getDimensionPixelSize(R.styleable.AlignedTextView_atv_textSize, textSize);
            textColor = ta.getColor(R.styleable.AlignedTextView_atv_textColor, textColor);
            ta.recycle();
        }
        init();
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午5:05
     * description 初始化参数计算
     */
    private void init() {
        layoutParamsMW = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setOrientation(VERTICAL);
        layoutParamsMW.bottomMargin = lineSpacingExtra;
        remainWidth = getContext().getResources().getDisplayMetrics().widthPixels - marginAndPaddingOfLeftRight;//可放置宽度
        remainHeight = getContext().getResources().getDisplayMetrics().heightPixels - marginAndPaddingOfTopDown;
        lineWordNum = remainWidth / (textSize / 2);//大致的列数，还有空格造成的空隙，暂时用空格填充解决
        columnWordNum = remainHeight / (getTextHeight() + 2 * lineSpacingExtra + offset);//每一页的行数((getTextHeight() + 3 * lineSpacingExtra))
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        intercepted = false;
        int x = (int) event.getX();
        int deltaX = x - mLastXIntercept;
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(deltaX) > 20) {//横向滑动超过20px，则拦截
                    intercepted = true;
                    setPage(deltaX);
                } else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_DOWN://down的时候就拦截
                mLastXIntercept = x;
                break;
        }
        return intercepted;
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 上午10:26
     * description 计算当前页数
     */
    public void setPage(int deltaX) {
        page = deltaX > 0 ? page - 1 : page + 1;
        if (page <= 0) {
            page = 1;//不再翻页
        } else if (page >= maxPage + 1) {
            page = maxPage;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastXTouch = x;
                break;
            case MotionEvent.ACTION_UP:
                int deltaX = x - mLastXTouch;
                if (Math.abs(deltaX) > 50) {//横向滑动超过50px，则拦截
                    if (!intercepted) {//没有拦截过，则进行翻页计算
                        setPage(deltaX);
                    }
                    if (null != lastView) {//恢复被touchDown事件改变状态的textview
                        String content = (String) lastView.getTag();
                        lastView.setText(addClickablePart(content, lastView, -1));
                    }
                    if (page <= 0) {
                        page = 1;//不再翻页
                    } else if (page >= maxPage + 1) {
                        page = maxPage;
                    } else {
                        clipPage();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 上午12:23
     * description 翻页操作
     */
    public void clipPage() {
        if (page > 0 && page <= maxPage) {
            showContent(page);
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/1 下午5:05
     * description 设置文本内容用来显示
     */
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
        this.content = content;
        if (lineWordNum <= 0) {
            return;
        }
        countText();
        showContent(page);
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 上午10:55
     * description 根据文字计算文本宽度
     */
    public int getTextWidth() {
        int result = 0;
        Rect bounds = new Rect();
        String text = "Hello";
        LocalTextView lineText = new LocalTextView(getContext());
        lineText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        lineText.setText(text);
        lineText.setLayoutParams(layoutParamsMW);
        TextPaint paint;
        paint = lineText.getPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        result = bounds.width() / text.length();
        return result;
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 上午10:55
     * description 根据文字计算文本高度
     */
    public int getTextHeight() {
        int result = 0;
        String text = "Hello";
        LocalTextView lineText = new LocalTextView(getContext());
        lineText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        lineText.setText(text);
        lineText.setLayoutParams(layoutParamsMW);
        result = lineText.getLineHeight();
        return result;
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 上午12:34
     * description 计算一共有多少页以及每一页的起始下标
     */
    public void countText() {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        int column = 0;
        String[] paraArray = content.split("\\n+");//段落分开
        for (String para : paraArray) {
            column = column + countPara(para);
            column++;//空行
        }
        maxPage = column / columnWordNum + (column % columnWordNum > 0 ? 1 : 0);//总行数除以每页行数，得到页数
        int currentIndex = 0;
        for (int i = 0; i < maxPage; i++) {
            pageCount.put(i + 1, currentIndex);
            currentIndex = getNextIndex(content, currentIndex);
            if (currentIndex == -1) {//已经到最后
                break;
            }
        }
        maxPage = pageCount.size();//更正总页数，防止误差产生
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 下午1:05
     * description 获取下一页的起始下标
     */
    public int getNextIndex(String content, int beginIndex) {
        if (TextUtils.isEmpty(content)) {
            return -1;
        }
        content = content.trim().substring(beginIndex);
        int contentLength = content.length();
        int index = 0;//计算下标
        int column = 0;//当前行数
        while (index < contentLength && column < columnWordNum) {
            if (contentLength - index <= lineWordNum) {//半行,没有下一页
                return -1;
            }
            String lineContent = content.substring(index, index + lineWordNum);
            String next = content.substring(index + lineWordNum, index + lineWordNum + 1);
            if (!next.equals(" ") && !lineContent.endsWith(" ")) {//下一行非空格开始,此一行非空格结束，则单词中断
                lineContent = lineContent.substring(0, lineContent.lastIndexOf(" ") + 1);//截取最后一个空格之前
            }
            if (lineContent.contains("\n")) {
                column++;//加一个空行


                index = index + lineContent.indexOf("\n") + 1;//半行
                column++;
            } else {
                index = index + lineContent.length();//更新下标
                column++;
            }
        }
        return index + beginIndex;//返回当前页之后下标+1
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 上午12:57
     * description 统计某段行数
     */
    public int countPara(String content) {
        int result = 0;
        if (TextUtils.isEmpty(content)) {
            return result;
        }
        content = content.trim();
        int contentLength = content.length();
        int index = 0;//计算下标
        while (index < contentLength) {
            if (contentLength - index <= lineWordNum) {//半行
                result++;
                return result;
            }
            String lineContent = content.substring(index, index + lineWordNum);
            String next = content.substring(index + lineWordNum, index + lineWordNum + 1);
            if (!next.equals(" ") && !lineContent.endsWith(" ")) {//下一行非空格开始,此一行非空格结束，则单词中断
                lineContent = lineContent.substring(0, lineContent.lastIndexOf(" ") + 1);//截取最后一个空格之前
            }
            index = index + lineContent.length();//更新下标
            result++;//一行
        }
        return result;
    }

    /**
     * @author davidinchina
     * cerate at 2017/10/2 上午12:54
     * description 显示某一页的内容到页面
     */
    public void showContent(int page) {
        if (null != pageChangeListener) {
            pageChangeListener.choosePage(page, maxPage);
        }
        removeAllViews();//清空已有数据
        int end = content.length();
        if (pageCount.containsKey(page + 1)) {
            end = pageCount.get(page + 1);
        }
        String currentPageContent = content.substring(pageCount.get(page), end);
        String[] paraArray = currentPageContent.split("\\n+");//段落分开
        for (String para : paraArray) {
            setPara(para);//设置每一行内容
            addEmptyLine();
        }
    }

    /**
     * @author davidinchina
     * cerate at 2017/9/29 上午12:00
     * description 处理每一个段落
     */
    public void setPara(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        content = content.trim();
        int contentLength = content.length();
        int index = 0;//计算下标
        while (index < contentLength) {
            if (contentLength - index <= lineWordNum) {
                String single = content.substring(index, contentLength);
                boolean flag = single.endsWith(".") || single.endsWith(".\"");
                addOneLineContent(single, remainWidth, !flag);//单行文本,最后一样不作对齐操作
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
        LocalTextView lineText = new LocalTextView(getContext());
        lineText.setMovementMethod(ClickableMovementMethod.getInstance());
        lineText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        lineText.setTextColor(textColor);
        if (justify) {
            justify(lineText, content, remainWidth);
        } else {//最后一行同样处理点击事件
            lineText.setText(addClickablePart(content, lineText, -1));
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
        LocalTextView lineText = new LocalTextView(getContext());
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
    public void justify(LocalTextView textView, String content, float contentWidth) {
        Paint paint = textView.getPaint();
        //需要插入的空格个数
        int totalSpacesToInsert = (int) ((contentWidth - paint.measureText(content)) / paint.measureText(" "));
        content = justifyLine(content, totalSpacesToInsert);
        textView.setText(addClickablePart(content, textView, -1));
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
    private SpannableStringBuilder addClickablePart(String str, final LocalTextView current, int begin) {
        current.setTag(str);//保存数据
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        String[] words = str.split("\\s");
        if (words.length > 0) {
            for (int i = 0; i < words.length; i++) {
                final String word = words[i];
                final int start = str.indexOf(word);
                ssb.setSpan(new TouchableSpan(R.color.textColor, R.color.white, R.color.chosed) {
                    @Override
                    public void onClick(View widget) {
                        if (null != widget) {//恢复被改变的数据
                            LocalTextView textView = (LocalTextView) widget;
                            int begin = (int) widget.getTag(R.id.buffer_begin);
                            String content = textView.getText().toString();
                            textView.setText(addClickablePart(content, textView, begin));
                        }
                        //清除旧数据状态，添加到新数据
                        if (null != lastView && lastView != current) {
                            String content = (String) lastView.getTag();
                            lastView.setText(addClickablePart(content, lastView, -1));
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
                if (start == begin) {
                    BackgroundColorSpan backgroundColorSpan =
                            new BackgroundColorSpan(getResources().getColor(R.color.chosed));
                    ssb.setSpan(backgroundColorSpan, start,
                            start + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.white));
                    ssb.setSpan(foregroundColorSpan, start, start + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return ssb;
    }


    private void avoidHintColor(View view) {
        if (view instanceof LocalTextView)
            ((LocalTextView) view).setHighlightColor(getResources().getColor(R.color.chosed));
    }

    private int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
