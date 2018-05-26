package site.iway.mymusic.user.activities;

import android.app.Dialog;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ExtendedView;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UIThread.UIEventHandler;
import site.iway.androidhelpers.UnitHelper;
import site.iway.mymusic.R;

/**
 * Created by iWay on 8/3/15.
 */
public abstract class BaseActivity extends FragmentActivity implements UIEventHandler {

    public static final String CLOSE_ANIMATION_ENTER = "CLOSE_ANIMATION_ENTER";
    public static final String CLOSE_ANIMATION_EXIT = "CLOSE_ANIMATION_EXIT";

    public static BaseActivity sRunningInstance;

    protected ViewGroup mContentViewContainer;
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
    protected List<Dialog> mDialogs;
    protected boolean mUserInteractEnabled;

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
        mDialogs = new LinkedList<>();
        mUserInteractEnabled = true;
        mContentViewContainer = findViewById(android.R.id.content);
        UIThread.register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sRunningInstance = this;
    }

    private static final int CONTENT_VIEW_PRIORITY_BASE = 0;
    private static final int CONTENT_VIEW_PRIORITY_NORMAL = 1;
    private static final int CONTENT_VIEW_PRIORITY_FLOATING_OBJECT = 126;
    private static final int CONTENT_VIEW_PRIORITY_SIMULATED_DIALOG = 127;
    private static final int CONTENT_VIEW_PRIORITY_SIMULATED_TOAST = 128;

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
                    long t1 = (int) view1.getTag(R.id.contentViewAddTime);
                    long t2 = (int) view2.getTag(R.id.contentViewAddTime);
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
    }

    private void setTitleBarViews() {
        mTitleBarRoot = findViewById(R.id.titleBarRoot);
        mTitleBarText = (ExtendedTextView) findViewById(R.id.titleBarText);
        mTitleBarBg = (ExtendedView) findViewById(R.id.titleBarBg);
        mTitleBarBack = (ExtendedImageView) findViewById(R.id.titleBarBack);
        mTitleBarButton = (ExtendedTextView) findViewById(R.id.titleBarButton);
        mTitleBarImage = (ExtendedImageView) findViewById(R.id.titleBarImage);
        mTitleBarSplitter = (ExtendedView) findViewById(R.id.titleBarSplitter);
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

    public void simulateToast(String message, final long timeout) {
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

    public void simulateToast(String message) {
        simulateToast(message, 2000);
    }

    public void setCloseAnimEnabled(boolean closeAnimEnabled) {
        mCloseAnimEnabled = closeAnimEnabled;
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

    public boolean registerDialog(Dialog dialog) {
        return mDialogs.add(dialog);
    }

    public boolean unregisterDialog(Dialog dialog) {
        return mDialogs.remove(dialog);
    }

    public boolean hasShowingDialog() {
        for (Dialog dialog : mDialogs) {
            if (dialog.isShowing()) {
                return true;
            }
        }
        return false;
    }

    protected void dismissAllDialogs() {
        List<Dialog> dialogs = new LinkedList<>();
        dialogs.addAll(mDialogs);
        for (Dialog dialog : dialogs) {
            dialog.dismiss();
        }
        mDialogs.clear();
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
    protected final void onDestroy() {
        UIThread.unregister(this);
        dismissAllDialogs();
        super.onDestroy();
    }

    @Override
    public void onEvent(String event, Object data) {
        // nothing
    }

    @Override
    public abstract void onBackPressed();

}
