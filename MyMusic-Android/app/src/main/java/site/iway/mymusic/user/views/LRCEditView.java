package site.iway.mymusic.user.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.UnitHelper;
import site.iway.androidhelpers.ViewHelper;
import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.utils.LyricManager.LyricLine;

public class LRCEditView extends FrameLayout {

    public LRCEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LRCEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LRCEditView(Context context) {
        super(context);
        init(context);
    }

    private Resources mResources;
    private LayoutInflater mLayoutInflater;
    private Paint mPaint;

    private Drawable mTimeLineDrawable;
    private View mStartTag;
    private View mEndTag;

    private void init(Context context) {
        setWillNotDraw(false);

        mResources = context.getResources();
        mLayoutInflater = LayoutInflater.from(context);
        mPaint = new Paint();
        mPaint.setColor(0xff666666);
        mPaint.setTextSize(UnitHelper.dipToPx(14));
        mPaint.setAntiAlias(true);
        mTimeLineDrawable = mResources.getDrawable(R.drawable.bg_time_line);

        mStartTag = mLayoutInflater.inflate(R.layout.group_time_tag, this, false);
        addView(mStartTag);
        mEndTag = mLayoutInflater.inflate(R.layout.group_time_tag, this, false);
        addView(mEndTag);
    }

    private int mDuration;

    public void setDuration(int duration) {
        mDuration = duration;
        ExtendedTextView endTag = mEndTag.findViewById(R.id.timeTag);
        endTag.setText(timeToString(duration));
        requestLayout();
    }

    public void addLyricLine(LyricLine lines) {

    }

    private List<View> mLyricViews = new ArrayList<>();

    public void addLyricLines(List<LyricLine> lines) {
        for (LyricLine lyricLine : lines) {
            ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.group_lyric_line, this, false);
            ExtendedTextView timeTag = viewGroup.findViewById(R.id.timeTag);
            ExtendedTextView text = viewGroup.findViewById(R.id.text);
            timeTag.setText(timeToString(lyricLine.millis));
            String combinedText = lyricLine.combineLineTexts();
            if (StringHelper.nullOrWhiteSpace(combinedText))
                combinedText = "     ";
            text.setText(combinedText);
            viewGroup.setTag(lyricLine);
            mLyricViews.add(viewGroup);
            addView(viewGroup);
        }
    }

    private int mWidth;
    private int mHeight;

    private int mTimeLineLeft;
    private int mTimeLineRight;
    private int mTimeLineTop;
    private int mTimeLineBottom;

    private void drawTimeLine(Canvas canvas) {
        mTimeLineDrawable.setBounds(mTimeLineLeft, mTimeLineTop, mTimeLineRight, mTimeLineBottom);
        mTimeLineDrawable.draw(canvas);
    }

    private String timeToString(long millis) {
        long minutes = millis / 1000 / 60;
        long seconds = millis / 1000 % 60;
        long percent = millis / 10 % 100;
        String minutesString = minutes < 10 ? "0" + minutes : "" + minutes;
        String secondsString = seconds < 10 ? "0" + seconds : "" + seconds;
        String percentString = percent < 10 ? "0" + percent : "" + percent;
        return minutesString + ":" + secondsString + "." + percentString;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getWidth();
        mHeight = getHeight();
        drawTimeLine(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                boolean hasViewSelected = false;
                View selectedView = null;
                for (View view : mLyricViews) {
                    if (ViewHelper.isMotionEventInView(event, view)) {
                        view.setSelected(true);
                        view.bringToFront();
                        hasViewSelected = true;
                        selectedView = view;
                        break;
                    }
                }
                if (hasViewSelected) {
                    for (View view : mLyricViews) {
                        if (view != selectedView) {
                            view.setSelected(false);
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = right - left;
        mHeight = bottom - top;
        mTimeLineLeft = UnitHelper.dipToPxInt(20);
        mTimeLineRight = mTimeLineLeft + UnitHelper.dipToPxInt(2);
        mTimeLineTop = UnitHelper.dipToPxInt(20);
        mTimeLineBottom = mHeight - UnitHelper.dipToPxInt(20);
        mStartTag.setTranslationX(mTimeLineRight);
        mStartTag.setTranslationY(mTimeLineTop - mStartTag.getHeight() / 2);
        mEndTag.setTranslationX(mTimeLineRight);
        mEndTag.setTranslationY(mTimeLineBottom - mStartTag.getHeight() / 2);
        for (View view : mLyricViews) {
            view.setTranslationX(mTimeLineRight);
            LyricLine lyricLine = (LyricLine) view.getTag();
            float percent = lyricLine.millis * 1.0f / mDuration;
            float timeLineHeight = mTimeLineBottom - mTimeLineTop;
            float translationY = timeLineHeight * percent + mTimeLineTop - mStartTag.getHeight() / 2;
            view.setTranslationY(translationY);
        }
    }
}