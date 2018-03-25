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

    protected ViewGroup mContainer;
    protected View mContentView;
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
    protected Thread mMainThread;
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
        mMainThread = Thread.currentThread();
        mUserInteractEnabled = true;
        UIThread.register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sRunningInstance = this;
    }

    private void setViews() {
        mContainer = findViewById(android.R.id.content);
        int rootViewCount = mContainer.getChildCount();
        if (rootViewCount > 0) {
            mContentView = mContainer.getChildAt(0);
        }
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
        super.setContentView(layoutResID);
        setViews();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setViews();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        setViews();
    }

    public void showWeakHint(String message, final long timeout) {
        if (isFinishing()) {
            return;
        }
        final ViewGroup contentViewContainer = mContainer;
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
        contentViewContainer.addView(toastView);
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
                contentViewContainer.removeView(toastView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.setStartOffset(timeout);
            }
        });
        toastView.startAnimation(animation);
    }

    public void showWeakHint(String message) {
        showWeakHint(message, 2000);
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

    private boolean mOnCloseCalled;

    protected void onClose() {
        dismissAllDialogs();
        UIThread.unregister(this);
    }

    @Override
    public final void finish() {
        if (!mOnCloseCalled) {
            onClose();
            mOnCloseCalled = true;
        }
        super.finish();
        int closeAnimEnter = mIntent.getIntExtra(CLOSE_ANIMATION_ENTER, 0);
        int closeAnimExit = mIntent.getIntExtra(CLOSE_ANIMATION_EXIT, 0);
        if (mCloseAnimEnabled && closeAnimEnter != 0 && closeAnimExit != 0) {
            overridePendingTransition(closeAnimEnter, closeAnimExit);
        }
    }

    public final void finish(int result) {
        setResult(result);
        finish();
    }

    public final void finish(int result, Intent data) {
        setResult(result, data);
        finish();
    }

    @Override
    protected final void onDestroy() {
        if (!mOnCloseCalled) {
            onClose();
            mOnCloseCalled = true;
        }
        super.onDestroy();
    }

    @Override
    public void onEvent(String event, Object data) {
        // nothing
    }

    @Override
    public abstract void onBackPressed();

}
