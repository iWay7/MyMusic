package site.iway.mymusic.user.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import site.iway.helpers.MathHelper;
import site.iway.mymusic.R;

/**
 * Created by iWay on 2017/12/30.
 */

public class PlayProgressView extends View {

    public interface OnRequestPlayProgressListener {
        public void onRequestPlayProgress(float progress);
    }

    public PlayProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
    }

    public PlayProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
    }

    public PlayProgressView(Context context) {
        super(context);
        resolveAttrs(context, null);
    }

    private OnRequestPlayProgressListener mListener;

    public void setListener(OnRequestPlayProgressListener listener) {
        mListener = listener;
    }

    private Drawable mProgressBgDrawable;
    private Drawable mProgressFgDrawable;
    private Drawable mIndicatorDrawable;
    private int mProgressHeight;
    private int mIndicatorWidth;
    private int mIndicatorHeight;

    private float mProgress;

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        if (mHandleMyTouchEvent) {
            return;
        }
        if (progress < 0)
            progress = 0;
        if (progress > 100)
            progress = 100;
        if (mProgress != progress) {
            mProgress = progress;
            invalidate();
        }
    }

    private void resolveAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PlayProgressView);
        mProgressBgDrawable = a.getDrawable(R.styleable.PlayProgressView_progressBgDrawable);
        mProgressFgDrawable = a.getDrawable(R.styleable.PlayProgressView_progressFgDrawable);
        mIndicatorDrawable = a.getDrawable(R.styleable.PlayProgressView_progressIndicatorDrawable);
        mProgressHeight = a.getDimensionPixelSize(R.styleable.PlayProgressView_progressDrawableHeight, 0);
        mIndicatorWidth = a.getDimensionPixelSize(R.styleable.PlayProgressView_progressIndicatorWidth, 0);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.PlayProgressView_progressIndicatorHeight, 0);
        a.recycle();
    }

    private float mPaddingLeft;
    private float mPaddingTop;
    private float mPaddingRight;
    private float mPaddingBottom;
    private float mViewWidth;
    private float mViewHeight;
    private float mClientWidth;
    private float mClientHeight;

    private void setValues() {
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mClientWidth = mViewWidth - mPaddingLeft - mPaddingRight;
        mClientHeight = mViewHeight - mPaddingTop - mPaddingBottom;
    }

    private boolean checkAvailable() {
        return mProgressBgDrawable != null &&
                mProgressFgDrawable != null &&
                mIndicatorDrawable != null &&
                mProgressHeight > 0 &&
                mIndicatorWidth > 0 &&
                mIndicatorHeight > 0 &&
                mClientWidth > mIndicatorWidth &&
                mClientHeight > 0;
    }

    private void drawProgressBg(Canvas canvas) {
        float progressWidth = mClientWidth - mIndicatorWidth;
        float left = mPaddingLeft + mIndicatorWidth / 2;
        float top = mPaddingTop + (mClientHeight - mProgressHeight) / 2;
        float right = left + progressWidth;
        float bottom = top + mProgressHeight;
        mProgressBgDrawable.setBounds(MathHelper.pixel(left),
                MathHelper.pixel(top),
                MathHelper.pixel(right),
                MathHelper.pixel(bottom));
        mProgressBgDrawable.draw(canvas);
    }

    private void drawProgressFg(Canvas canvas) {
        float progressWidth = mClientWidth - mIndicatorWidth;
        float left = mPaddingLeft + mIndicatorWidth / 2;
        float top = mPaddingTop + (mClientHeight - mProgressHeight) / 2;
        float right = left + progressWidth * mProgress / 100;
        float bottom = top + mProgressHeight;
        mProgressFgDrawable.setBounds(MathHelper.pixel(left),
                MathHelper.pixel(top),
                MathHelper.pixel(right),
                MathHelper.pixel(bottom));
        mProgressFgDrawable.draw(canvas);
    }

    private void drawProgressIndicator(Canvas canvas) {
        float progressWidth = mClientWidth - mIndicatorWidth;
        float fgLeft = mPaddingLeft + mIndicatorWidth / 2;
        float fgRight = fgLeft + progressWidth * mProgress / 100;
        int left = (int) (fgRight - mIndicatorWidth / 2);
        int top = (int) (mPaddingTop + (mClientHeight - mIndicatorHeight) / 2);
        int right = left + mIndicatorWidth;
        int bottom = top + mIndicatorHeight;
        mIndicatorDrawable.setBounds((int) (left),
                MathHelper.pixel(top),
                (int) (right),
                MathHelper.pixel(bottom));
        mIndicatorDrawable.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setValues();
        if (checkAvailable()) {
            drawProgressBg(canvas);
            drawProgressFg(canvas);
            drawProgressIndicator(canvas);
        }
    }

    private boolean mSuperHandledTouchEvent;
    private boolean mHandleMyTouchEvent;

    private boolean onSuperTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mSuperHandledTouchEvent = super.onTouchEvent(event);
        } else {
            if (mSuperHandledTouchEvent) {
                mSuperHandledTouchEvent = super.onTouchEvent(event);
            }
        }
        return mSuperHandledTouchEvent;
    }

    private void onTouchEnd() {
        mHandleMyTouchEvent = false;
        ViewParent viewParent = getParent();
        while (viewParent != null && !(viewParent instanceof ViewPager)) {
            viewParent = viewParent.getParent();
        }
        if (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(false);
        }
    }

    private boolean onMyTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mHandleMyTouchEvent = true;
                ViewParent viewParent = getParent();
                while (viewParent != null && !(viewParent instanceof ViewPager)) {
                    viewParent = viewParent.getParent();
                }
                if (viewParent != null) {
                    viewParent.requestDisallowInterceptTouchEvent(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (mHandleMyTouchEvent) {
                    if (checkAvailable()) {
                        float progressWidth = mClientWidth - mIndicatorWidth;
                        float touchX = event.getX();
                        float touchProgressWidth = touchX - mPaddingLeft - mIndicatorWidth / 2;
                        if (touchProgressWidth < 0)
                            touchProgressWidth = 0;
                        if (touchProgressWidth > progressWidth)
                            touchProgressWidth = progressWidth;
                        float progress = touchProgressWidth * 100 / progressWidth;
                        if (mProgress != progress) {
                            mProgress = progress;
                            invalidate();
                        }
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                onTouchEnd();
                break;
            case MotionEvent.ACTION_UP:
                onTouchEnd();
                if (mListener != null) {
                    mListener.onRequestPlayProgress(mProgress);
                }
                break;
        }
        return mHandleMyTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mSuperHandledTouchEvent = false;
            mHandleMyTouchEvent = false;
        }
        return onSuperTouchEvent(event) || onMyTouchEvent(event);
    }
}
