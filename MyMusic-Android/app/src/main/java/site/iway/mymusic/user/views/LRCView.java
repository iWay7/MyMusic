package site.iway.mymusic.user.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Xfermode;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import site.iway.androidhelpers.UITimer;
import site.iway.androidhelpers.UnitHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.utils.LyricManager;
import site.iway.mymusic.utils.LyricManager.LyricLine;
import site.iway.mymusic.utils.LyricTask;
import site.iway.mymusic.utils.LyricTask.LyricStateListener;
import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;

/**
 * Created by iWay on 2017/12/25.
 */

public class LRCView extends View implements LyricStateListener {

    public LRCView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
    }

    public LRCView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
    }

    public LRCView(Context context) {
        super(context);
        resolveAttrs(context, null);
    }

    private Paint mPaint;

    private float mTextSize;
    private int mTextLineSpacing;
    private int mLineSpacing;
    private int mNormalColor;
    private int mHighLightColor;
    private String mEmptyText;
    private float mEdgeFadingPercent;

    private void resolveAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LRCView);
        mTextSize = a.getDimension(R.styleable.LRCView_lrcTextSize, UnitHelper.spToPx(16));
        mTextLineSpacing = a.getDimensionPixelSize(R.styleable.LRCView_lrcTextLineSpacing, UnitHelper.dipToPxInt(2));
        mLineSpacing = a.getDimensionPixelSize(R.styleable.LRCView_lrcLineSpacing, UnitHelper.dipToPxInt(20));
        mNormalColor = a.getColor(R.styleable.LRCView_lrcColorNormal, 0xff999999);
        mHighLightColor = a.getColor(R.styleable.LRCView_lrcColorHighLight, 0xffffffff);
        mEmptyText = a.getString(R.styleable.LRCView_lrcEmptyText);
        mEdgeFadingPercent = a.getFloat(R.styleable.LRCView_lrcEdgeFadingPercent, 16f);
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
        a.recycle();
    }

    private volatile LyricTask mLyricTask;
    private volatile LyricManager mLyricManager;

    private void setLyricManager(LyricManager lyricManager) {
        if (mLyricManager != lyricManager) {
            mLyricManager = lyricManager;
            if (mLyricManager != null) {
                mLyricManager.setPaint(mPaint);
                mLyricManager.setTextLineSpacing(mTextLineSpacing);
                mLyricManager.setLineSpacing(mLineSpacing);
                mLyricManager.setNormalColor(mNormalColor);
                mLyricManager.setHighLightColor(mHighLightColor);
                mLyricManager.setDisplayWidth(getWidth());
                mLyricManager.setDisplayHeight(getHeight());
                mLyricManager.computePositions();
            }
            mCurrentLine = null;
            invalidate();
            scrollLyric();
        }
    }

    @Override
    public void onLyricStateChanged(LyricTask lyricTask) {
        if (mLyricTask == lyricTask) {
            switch (lyricTask.getTaskState()) {
                case LyricTask.STATE_SUCCESS:
                    final LyricManager lyricManager = lyricTask.getLyricManager();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setLyricManager(lyricManager);
                        }
                    });
                    break;
            }
        }
    }

    public void load(String url) {
        mLyricManager = null;
        mLyricTask = null;
        if (url != null) {
            mLyricTask = new LyricTask(url, this);
            mLyricTask.start();
        }
    }

    private float mEdgeFadingHeight;
    private LinearGradient mEdgeFadingTopShader;
    private LinearGradient mEdgeFadingBottomShader;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mLyricManager != null) {
            mLyricManager.setDisplayWidth(w);
            mLyricManager.setDisplayHeight(h);
            mLyricManager.computePositions();
            invalidate();
        }
        if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP && w > 0 && h > 0) {
            mEdgeFadingHeight = h * mEdgeFadingPercent / 100;
            mEdgeFadingTopShader = new LinearGradient(0, 0, 0, mEdgeFadingHeight, mNormalColor & 0x00ffffff, mNormalColor, TileMode.CLAMP);
            mEdgeFadingBottomShader = new LinearGradient(0, 0, 0, mEdgeFadingHeight, mNormalColor, mNormalColor & 0x00ffffff, TileMode.CLAMP);
        }
    }

    private LyricLine mCurrentLine;

    private void setCurrentLine(LyricLine currentLine) {
        if (mCurrentLine != currentLine) {
            mCurrentLine = currentLine;
            mLyricManager.setCurrentLine(currentLine);
            invalidate();
            scrollLyric();
        }
    }

    private UITimer mUITimer = new UITimer(200) {

        @Override
        public void doOnUIThread() {
            if (mLyricManager == null) {
                setCurrentLine(null);
            } else {
                Player player = Player.getInstance();
                PlayTask playTask = player.getPlayTask();
                if (playTask == null) {
                    setCurrentLine(null);
                } else {
                    int position = playTask.getPosition();
                    LyricLine currentLine = mLyricManager.getCurrentLine(position);
                    setCurrentLine(currentLine);
                }
            }
        }

    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUITimer.start(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        mUITimer.stop();
        super.onDetachedFromWindow();
    }

    private ObjectAnimator mObjectAnimator;

    protected void scrollLyric() {
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
        }
        if (mCurrentLine != null) {
            int viewHeight = getHeight();
            int targetScroll = mCurrentLine.middleY - viewHeight / 2;
            int currentScroll = getScrollY();
            int visibility = getVisibility();
            if (visibility == View.VISIBLE) {
                mObjectAnimator = ObjectAnimator.ofInt(this, "scrollY", currentScroll, targetScroll);
                mObjectAnimator.setDuration(300);
                mObjectAnimator.start();
            } else {
                setScrollY(targetScroll);
            }
        } else {
            setScrollY(0);
        }
    }

    private Xfermode mXfermode = new PorterDuffXfermode(Mode.SRC_IN);
    private RectF mTempRectF = new RectF();
    private Rect mTempRect = new Rect();
    private Matrix mTempMatrix = new Matrix();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = 0;
        float top = getScrollY();
        float right = left + getWidth();
        float bottom = top + getHeight();
        if (mLyricManager != null) {
            if (VERSION.SDK_INT > VERSION_CODES.LOLLIPOP) {
                mTempRectF.set(left, top, right, bottom);
                int layer = canvas.saveLayer(mTempRectF, mPaint);
                mLyricManager.drawNormal(canvas);
                mPaint.setXfermode(mXfermode);
                mEdgeFadingTopShader.getLocalMatrix(mTempMatrix);
                mTempMatrix.setTranslate(left, top);
                mEdgeFadingTopShader.setLocalMatrix(mTempMatrix);
                mPaint.setShader(mEdgeFadingTopShader);
                canvas.drawRect(left, top, right, top + mEdgeFadingHeight, mPaint);
                mEdgeFadingBottomShader.getLocalMatrix(mTempMatrix);
                mTempMatrix.setTranslate(left, bottom - mEdgeFadingHeight);
                mEdgeFadingBottomShader.setLocalMatrix(mTempMatrix);
                mPaint.setShader(mEdgeFadingBottomShader);
                canvas.drawRect(left, bottom - mEdgeFadingHeight, right, bottom, mPaint);
                mPaint.setShader(null);
                mPaint.setXfermode(null);
                canvas.restoreToCount(layer);
                mLyricManager.drawHighLight(canvas);
            } else {
                mLyricManager.drawNormal(canvas);
                mLyricManager.drawHighLight(canvas);
            }
        } else {
            if (!TextUtils.isEmpty(mEmptyText)) {
                mPaint.getTextBounds(mEmptyText, 0, mEmptyText.length(), mTempRect);
                mPaint.setColor(mNormalColor);
                int textWidth = mTempRect.width();
                int textHeight = mTempRect.height();
                float textLeft = left + ((right - left) - textWidth) / 2;
                float textTop = top + ((bottom - top) - textHeight) / 2;
                canvas.drawText(mEmptyText, textLeft, textTop, mPaint);
            }
        }
    }
}
