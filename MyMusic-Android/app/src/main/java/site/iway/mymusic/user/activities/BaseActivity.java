package site.iway.mymusic.user.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ExtendedView;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UIThread.UIEventHandler;
import site.iway.androidhelpers.UnitHelper;
import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;

/**
 * Created by iWay on 8/3/15.
 */
public abstract class BaseActivity extends FragmentActivity implements UIEventHandler {

    public static final String CLOSE_ANIMATION_ENTER = "CLOSE_ANIMATION_ENTER";
    public static final String CLOSE_ANIMATION_EXIT = "CLOSE_ANIMATION_EXIT";

    protected ViewGroup mContentViewContainer;
    protected View mTitleBarPad;
    protected View mTitleBarRoot;
    protected ExtendedTextView mTitleBarText;
    protected ExtendedView mTitleBarBg;
    protected ExtendedImageView mTitleBarBack;
    protected ExtendedTextView mTitleBarButton;
    protected ExtendedImageView mTitleBarImage;
    protected ExtendedView mTitleBarSplitter;
    protected Handler mHandler;
    protected Intent mIntent;
    protected FragmentManager mFragmentManager;
    protected LayoutInflater mLayoutInflater;
    protected ContentResolver mContentResolver;
    protected Resources mResources;
    protected AssetManager mAssetManager;
    protected boolean mCloseAnimEnabled;
    protected boolean mUserInteractEnabled;
    protected boolean mWillInitTitleBarViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mIntent = getIntent();
        mFragmentManager = getSupportFragmentManager();
        mLayoutInflater = getLayoutInflater();
        mContentResolver = getContentResolver();
        mResources = getResources();
        mAssetManager = getAssets();
        mCloseAnimEnabled = true;
        mUserInteractEnabled = true;
        mWillInitTitleBarViews = true;
        mContentViewContainer = (ViewGroup) findViewById(android.R.id.content);
        mContentViewContainer.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (parent == mContentViewContainer) {
                    Integer priority = (Integer) child.getTag(R.id.contentViewPriority);
                    Long addTime = (Long) child.getTag(R.id.contentViewAddTime);
                    if (priority == null || addTime == null) {
                        child.setTag(R.id.contentViewPriority, CONTENT_VIEW_PRIORITY_NORMAL);
                        child.setTag(R.id.contentViewAddTime, System.nanoTime());
                        adjustContentViewOrders();
                    }
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                // nothing
            }
        });
        UIThread.register(this);
    }

    public static final int CONTENT_VIEW_PRIORITY_BASE = 0;
    public static final int CONTENT_VIEW_PRIORITY_NORMAL = 1;
    public static final int CONTENT_VIEW_PRIORITY_FLOATING_OBJECT = 125;
    public static final int CONTENT_VIEW_PRIORITY_DIALOG = 126;
    public static final int CONTENT_VIEW_PRIORITY_LOADING_VIEW = 127;
    public static final int CONTENT_VIEW_PRIORITY_SIMULATED_TOAST = 128;

    private void adjustContentViewOrders() {
        List<View> childViews = new ArrayList<>();
        int childCount = mContentViewContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            childViews.add(mContentViewContainer.getChildAt(i));
        }
        Collections.sort(childViews, new Comparator<View>() {
            @Override
            public int compare(View view1, View view2) {
                int p1 = (int) view1.getTag(R.id.contentViewPriority);
                int p2 = (int) view2.getTag(R.id.contentViewPriority);
                long t1 = (long) view1.getTag(R.id.contentViewAddTime);
                long t2 = (long) view2.getTag(R.id.contentViewAddTime);
                if (p1 < p2) {
                    return -1;
                } else if (p1 > p2) {
                    return 1;
                } else {
                    if (t1 < t2) {
                        return -1;
                    } else if (t1 > t2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        for (int i = 0; i < childCount; i++) {
            childViews.get(i).bringToFront();
        }
    }

    private void addContentViewInternal(View view, int priority) {
        view.setTag(R.id.contentViewPriority, priority);
        view.setTag(R.id.contentViewAddTime, System.nanoTime());
        if (priority == CONTENT_VIEW_PRIORITY_BASE) {
            int childCount = mContentViewContainer.getChildCount();
            if (childCount > 0) {
                View firstContentView = mContentViewContainer.getChildAt(0);
                int firstContentViewPriority = (int) firstContentView.getTag(R.id.contentViewPriority);
                if (firstContentViewPriority == CONTENT_VIEW_PRIORITY_BASE) {
                    mContentViewContainer.removeViewAt(0);
                }
            }
            mContentViewContainer.addView(view, 0);
        } else {
            mContentViewContainer.addView(view);
            adjustContentViewOrders();
        }
    }

    private void setTitleBarViews() {
        if (mWillInitTitleBarViews) {
            mTitleBarPad = findViewById(R.id.titleBarPad);
            if (mTitleBarPad != null && WindowHelper.makeTranslucent(this, true, false)) {
                mTitleBarPad.setVisibility(View.VISIBLE);
                int statusBarHeight = WindowHelper.getStatusBarHeight(this);
                LayoutParams layoutParams = mTitleBarPad.getLayoutParams();
                layoutParams.height = statusBarHeight;
            }
            mTitleBarRoot = findViewById(R.id.titleBarRoot);
            mTitleBarText = (ExtendedTextView) findViewById(R.id.titleBarText);
            mTitleBarBg = (ExtendedView) findViewById(R.id.titleBarBg);
            mTitleBarBack = (ExtendedImageView) findViewById(R.id.titleBarBack);
            mTitleBarButton = (ExtendedTextView) findViewById(R.id.titleBarButton);
            mTitleBarImage = (ExtendedImageView) findViewById(R.id.titleBarImage);
            mTitleBarSplitter = (ExtendedView) findViewById(R.id.titleBarSplitter);
            mTitleBarSplitter.setVisibility(View.GONE);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = mLayoutInflater.inflate(layoutResID, mContentViewContainer, false);
        addContentViewInternal(view, CONTENT_VIEW_PRIORITY_BASE);
        setTitleBarViews();
    }

    @Override
    public void setContentView(View view) {
        addContentViewInternal(view, CONTENT_VIEW_PRIORITY_BASE);
        setTitleBarViews();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        view.setLayoutParams(params);
        addContentViewInternal(view, CONTENT_VIEW_PRIORITY_BASE);
        setTitleBarViews();
    }

    public void addContentView(int layoutResID) {
        View view = mLayoutInflater.inflate(layoutResID, mContentViewContainer, false);
        addContentViewInternal(view, CONTENT_VIEW_PRIORITY_NORMAL);
    }

    public void addContentView(View view) {
        addContentViewInternal(view, CONTENT_VIEW_PRIORITY_NORMAL);
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        view.setLayoutParams(params);
        addContentViewInternal(view, CONTENT_VIEW_PRIORITY_NORMAL);
    }

    public void removeContentView(View view) {
        mContentViewContainer.removeView(view);
    }

    private View mLoadingView;
    private int mShowCount;

    public void showLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = mLayoutInflater.inflate(R.layout.dialog_loading, mContentViewContainer, false);
            addContentViewInternal(mLoadingView, CONTENT_VIEW_PRIORITY_LOADING_VIEW);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(100);
            mLoadingView.startAnimation(alphaAnimation);
        }
        mShowCount++;
    }

    public void hideLoadingView() {
        if (mShowCount > 0) {
            mShowCount--;
        }
        if (mShowCount == 0 && mLoadingView != null) {
            final View animationView = mLoadingView;
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(100);
            alphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // nothing
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeContentView(animationView);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // nothing
                }
            });
            mLoadingView.startAnimation(alphaAnimation);
            mLoadingView = null;
        }
    }

    public void showToastView(String message, final long timeout) {
        if (isFinishing()) {
            return;
        }
        final ExtendedTextView toastView = new ExtendedTextView(this);
        toastView.setBackgroundResource(R.drawable.bg_com_toast);
        toastView.setGravity(Gravity.CENTER);
        toastView.setMinWidth(UnitHelper.dipToPxInt(180));
        int paddingLeft = UnitHelper.dipToPxInt(20);
        int paddingRight = paddingLeft;
        int paddingTop = UnitHelper.dipToPxInt(16);
        int paddingBottom = paddingTop;
        toastView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        toastView.setTextColor(0xffffffff);
        toastView.setTextSize(14);
        toastView.setText(message);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        flp.gravity = Gravity.CENTER;
        toastView.setLayoutParams(flp);
        addContentViewInternal(toastView, CONTENT_VIEW_PRIORITY_SIMULATED_TOAST);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                removeContentView(toastView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setStartOffset(timeout);
            }
        });
        toastView.startAnimation(animation);
    }

    public void showToastView(String message) {
        showToastView(message, 2000);
    }

    public void setCloseAnimEnabled(boolean closeAnimEnabled) {
        mCloseAnimEnabled = closeAnimEnabled;
    }

    public void setWillInitTitleBarViews(boolean willInitTitleBarViews) {
        mWillInitTitleBarViews = willInitTitleBarViews;
    }

    public void disableUserInteract() {
        mUserInteractEnabled = false;
    }

    public void enableUserInteract() {
        mUserInteractEnabled = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mUserInteractEnabled) {
            return super.dispatchTouchEvent(ev);
        } else {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    return false;
                default:
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    boolean superResult = super.dispatchTouchEvent(ev);
                    ev.setAction(action);
                    return false;
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {
        if (mUserInteractEnabled) {
            return super.dispatchKeyEvent(ev);
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        int closeAnimEnter = mIntent.getIntExtra(CLOSE_ANIMATION_ENTER, 0);
        int closeAnimExit = mIntent.getIntExtra(CLOSE_ANIMATION_EXIT, 0);
        if (mCloseAnimEnabled && closeAnimEnter != 0 && closeAnimExit != 0) {
            overridePendingTransition(closeAnimEnter, closeAnimExit);
        }
    }

    public void finish(int result) {
        setResult(result);
        finish();
    }

    public void finish(int result, Intent data) {
        setResult(result, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        UIThread.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onEvent(String event, Object data) {
        // nothing
    }

    @Override
    public abstract void onBackPressed();

}
