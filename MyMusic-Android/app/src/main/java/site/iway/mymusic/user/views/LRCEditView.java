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
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ScrollView;

import java.util.List;

import site.iway.androidhelpers.ExtendedFrameLayout;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.UnitHelper;
import site.iway.androidhelpers.ViewHelper;
import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.utils.LyricManager.LyricLine;

public class LRCEditView extends ExtendedFrameLayout {

    public interface OnEditActionListener {

        public void onRequestAdjustPosition(int position);
    }

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

    private OnEditActionListener mListener;

    public void setListener(OnEditActionListener listener) {
        mListener = listener;
    }

    private Resources mResources;
    private LayoutInflater mLayoutInflater;
    private Paint mPaint;

    private Drawable mTimeLineDrawable;
    private Drawable mPositionDrawable;

    private void init(Context context) {
        setWillNotDraw(false);
        mResources = context.getResources();
        mLayoutInflater = LayoutInflater.from(context);
        mPaint = new Paint();
        mPaint.setColor(0xff666666);
        mPaint.setTextSize(UnitHelper.dipToPx(14));
        mPaint.setAntiAlias(true);
        mTimeLineDrawable = mResources.getDrawable(R.drawable.bg_time_line);
        mPositionDrawable = mResources.getDrawable(R.drawable.ic_lyric_indicator);
    }

    private int mDuration;

    public void setDuration(int duration) {
        mDuration = duration;
        requestLayout();
    }

    private int mPosition;

    public void mPosition(int position) {
        if (mPosition != position) {
            mPosition = position;
            invalidate();
        }
    }

    public void addLyricLines(List<LyricLine> lines) {
        for (LyricLine lyricLine : lines) {
            if (lyricLine.millis > mDuration)
                return;
            ViewGroup viewGroup = (ViewGroup) mLayoutInflater.inflate(R.layout.group_lyric_line, this, false);
            ExtendedTextView timeTag = viewGroup.findViewById(R.id.timeTag);
            ExtendedTextView text = viewGroup.findViewById(R.id.text);
            timeTag.setText(timeToString(lyricLine.millis));
            String combinedText = lyricLine.combineLineTexts();
            if (StringHelper.nullOrWhiteSpace(combinedText))
                combinedText = "     ";
            text.setText(combinedText);
            viewGroup.setTag(lyricLine);
            addView(viewGroup);
        }
    }

    public void removeSelectedLyricLine() {
        if (mSelectedView == null) {
            return;
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(300);
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removeView(mSelectedView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSelectedView.startAnimation(alphaAnimation);
    }

    public boolean hasSelectedView() {
        return mSelectedView != null;
    }

    public String getSelectedLyricLineText() {
        if (mSelectedView != null) {
            LyricLine lyricLine = (LyricLine) mSelectedView.getTag();
            return lyricLine.combineLineTexts();
        }
        return null;
    }

    public void setSelectedLyricLineText(List<String> lines) {
        if (mSelectedView != null) {
            LyricLine lyricLine = (LyricLine) mSelectedView.getTag();
            lyricLine.sourceTextLines = lines;
            String combinedText = lyricLine.combineLineTexts();
            if (StringHelper.nullOrWhiteSpace(combinedText))
                combinedText = "     ";
            ExtendedTextView text = mSelectedView.findViewById(R.id.text);
            text.setText(combinedText);
        }
    }

    public void addLyricLineText(List<String> lines) {
        LyricLine lyricLine = new LyricLine();
        lyricLine.millis = mPosition;
        lyricLine.sourceTextLines = lines;

        if (mSelectedView != null) {
            mSelectedView.setSelected(false);
        }

        mSelectedView = mLayoutInflater.inflate(R.layout.group_lyric_line, this, false);
        ExtendedTextView timeTag = mSelectedView.findViewById(R.id.timeTag);
        ExtendedTextView text = mSelectedView.findViewById(R.id.text);
        timeTag.setText(timeToString(lyricLine.millis));
        String combinedText = lyricLine.combineLineTexts();
        if (StringHelper.nullOrWhiteSpace(combinedText))
            combinedText = "     ";
        text.setText(combinedText);
        mSelectedView.setTag(lyricLine);
        addView(mSelectedView);
        mSelectedView.setSelected(true);
        mSelectedView.bringToFront();
    }

    public String generateLrcFile() {
        StringBuilder stringBuilder = new StringBuilder();
        int childCount = getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            View childView = getChildAt(childIndex);
            LyricLine lyricLine = (LyricLine) childView.getTag();
            for (String text : lyricLine.sourceTextLines) {
                stringBuilder.append("[")
                        .append(timeToString(lyricLine.millis))
                        .append("]")
                        .append(text)
                        .append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private static final int ACTION_NONE = -1;
    private static final int ACTION_ADJUST_POSITION = 0;
    private static final int ACTION_DRAG_LYRIC_LINE = 1;

    private int mAction;

    private float mDragStartY;
    private float mDragStartTranslationY;
    private View mSelectedView;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mAction = ACTION_NONE;
            float x = event.getX();
            float y = event.getY();
            if (x >= 0 && x <= mTimeLineLeft && y >= mTimeLineTop && y <= mTimeLineBottom) {
                mAction = ACTION_ADJUST_POSITION;
                mListener.onRequestAdjustPosition((int) ((y - mTimeLineTop) / mTimeLineHeight * mDuration));
            } else {
                mSelectedView = null;
                int childCount = getChildCount();
                for (int childIndex = childCount - 1; childIndex >= 0; childIndex--) {
                    View childView = getChildAt(childIndex);
                    childView.setSelected(false);
                }
                for (int childIndex = childCount - 1; childIndex >= 0; childIndex--) {
                    View childView = getChildAt(childIndex);
                    if (ViewHelper.isMotionEventInView(event, childView)) {
                        childView.setSelected(true);
                        childView.bringToFront();
                        mSelectedView = childView;
                        break;
                    }
                }
                if (mSelectedView != null) {
                    mAction = ACTION_DRAG_LYRIC_LINE;
                    ViewParent parent = getParent();
                    while (parent != null) {
                        if (parent instanceof ScrollView) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        parent = parent.getParent();
                    }
                    mDragStartY = y;
                    mDragStartTranslationY = mSelectedView.getTranslationY();
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mAction == ACTION_DRAG_LYRIC_LINE) {
                float offsetY = event.getY() - mDragStartY;
                float targetTranslationY = mDragStartTranslationY + offsetY;
                float minTranslationY = mTimeLineTop - UnitHelper.dipToPx(12.5f);
                float maxTranslationY = mTimeLineBottom - UnitHelper.dipToPx(12.5f);
                if (targetTranslationY < minTranslationY) {
                    targetTranslationY = minTranslationY;
                }
                if (targetTranslationY > maxTranslationY) {
                    targetTranslationY = maxTranslationY;
                }
                mSelectedView.setTranslationY(targetTranslationY);
                LyricLine lyricLine = (LyricLine) mSelectedView.getTag();
                lyricLine.millis = (long) ((targetTranslationY - minTranslationY) / mTimeLineHeight * mDuration);
                ExtendedTextView timeTag = mSelectedView.findViewById(R.id.timeTag);
                timeTag.setText(timeToString(lyricLine.millis));
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (mAction == ACTION_DRAG_LYRIC_LINE) {
                ViewParent parent = getParent();
                while (parent != null) {
                    if (parent instanceof ScrollView) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    parent = parent.getParent();
                }
            }
        }
        return true;
    }

    private int mWidth;
    private int mHeight;

    private int mTimeLineLeft;
    private int mTimeLineRight;
    private int mTimeLineTop;
    private int mTimeLineBottom;
    private int mTimeLineHeight;

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

    private void drawPosition(Canvas canvas) {
        int left = mTimeLineLeft - UnitHelper.dipToPxInt(12f);
        int timeLineHeight = mTimeLineBottom - mTimeLineTop;
        int top = (int) (1f * mPosition / mDuration * timeLineHeight + mTimeLineTop - 1f * mPositionDrawable.getIntrinsicWidth() / 2);
        int right = left + mPositionDrawable.getIntrinsicWidth();
        int bottom = top + mPositionDrawable.getIntrinsicHeight();
        mPositionDrawable.setBounds(left, top, right, bottom);
        mPositionDrawable.draw(canvas);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getWidth();
        mHeight = getHeight();
        drawTimeLine(canvas);
        drawPosition(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = right - left;
        mHeight = bottom - top;
        mTimeLineLeft = UnitHelper.dipToPxInt(24);
        mTimeLineRight = mTimeLineLeft + UnitHelper.dipToPxInt(1);
        mTimeLineTop = UnitHelper.dipToPxInt(20);
        mTimeLineBottom = mHeight - UnitHelper.dipToPxInt(20);
        mTimeLineHeight = mTimeLineBottom - mTimeLineTop;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            view.setTranslationX(mTimeLineRight);
            LyricLine lyricLine = (LyricLine) view.getTag();
            float percent = 1f * lyricLine.millis / mDuration;
            float translationY = 1f * mTimeLineHeight * percent + mTimeLineTop - UnitHelper.dipToPx(12.5f);
            view.setTranslationY(translationY);
        }
    }

}