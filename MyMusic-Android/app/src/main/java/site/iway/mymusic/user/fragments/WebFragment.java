package site.iway.mymusic.user.fragments;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedLinearLayout;
import site.iway.androidhelpers.ExtendedRelativeLayout;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ExtendedView;
import site.iway.androidhelpers.ExtendedWebView;
import site.iway.androidhelpers.LoadingView;
import site.iway.mymusic.BuildConfig;
import site.iway.mymusic.R;
import site.iway.mymusic.user.dialogs.BaseDialog.OnUserActionListener;
import site.iway.mymusic.user.dialogs.DoubleActionDialog;
import site.iway.mymusic.user.dialogs.JsPromptDialog;
import site.iway.mymusic.user.dialogs.SingleActionDialog;
import site.iway.mymusic.utils.JavascriptCallable;

public class WebFragment extends BaseFragment implements OnClickListener {

    public interface OnJavascriptCallListener {
        public String onJavascriptCall(String param);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    private View mTitleBarRoot;
    private ExtendedTextView mTitleBarText;
    private ExtendedImageView mTitleBarBack;
    private ExtendedTextView mTitleBarClose;
    private ExtendedLinearLayout mTitleBarActions;
    private ExtendedView mTitleBarSplitter;
    private ExtendedWebView mWebView;
    private LoadingView mLoadingView;
    private ExtendedRelativeLayout mErrorContainer;
    private ExtendedImageView mErrorImage;
    private ExtendedTextView mErrorText;
    private ExtendedTextView mErrorDesc;
    private ExtendedTextView mRefreshButton;

    public void setTitleBarRoot(View titleBarRoot) {
        mTitleBarRoot = titleBarRoot;
    }

    public void setTitleBarText(ExtendedTextView titleBarText) {
        mTitleBarText = titleBarText;
    }

    public void setTitleBarBack(ExtendedImageView titleBarBack) {
        mTitleBarBack = titleBarBack;
    }

    public void setTitleBarClose(ExtendedTextView titleBarClose) {
        mTitleBarClose = titleBarClose;
    }

    public void setTitleBarActions(ExtendedLinearLayout titleBarActions) {
        mTitleBarActions = titleBarActions;
    }

    public void setTitleBarSplitter(ExtendedView titleBarSplitter) {
        mTitleBarSplitter = titleBarSplitter;
    }

    protected void doBeforeLoadUrl(WebView webView) {
        // nothing
    }

    protected void doAfterPageStarted(WebView webView) {
        // nothing
    }

    protected void doAfterPageFinished(WebView webView) {
        // nothing
    }

    private OnJavascriptCallListener mOnJavascriptCallListener;

    public void setOnJavascriptCallListener(OnJavascriptCallListener onJavascriptCallListener) {
        mOnJavascriptCallListener = onJavascriptCallListener;
    }

    public void runJavascript(String javascript) {
        mWebView.loadUrl("javascript:" + javascript);
    }

    private class JavascriptBridge implements JavascriptCallable {

        @JavascriptInterface
        public String run(String param) {
            if (BuildConfig.DEBUG) {
                Log.d("WebCall", param);
            }
            if (mOnJavascriptCallListener != null) {
                mOnJavascriptCallListener.onJavascriptCall(param);
            }
            return null;
        }

    }

    private boolean mErrorOccurred;
    private String mUrl;
    private String mData;
    private String mOnBackPressedEvent;

    public void extendWebViewTop(int height) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mWebView.getLayoutParams();
        if (mTitleBarRoot == null || mTitleBarRoot.getVisibility() == View.GONE) {
            layoutParams.topMargin = -height;
        } else {
            layoutParams.topMargin = 0;
        }
        mWebView.setLayoutParams(layoutParams);
    }

    public void setViews(Bundle savedInstanceState) {
        mWebView = (ExtendedWebView) mRootView.findViewById(R.id.webView);
        mLoadingView = (LoadingView) mRootView.findViewById(R.id.loadingView);
        mErrorContainer = (ExtendedRelativeLayout) mRootView.findViewById(R.id.errorContainer);
        mErrorImage = (ExtendedImageView) mRootView.findViewById(R.id.errorImage);
        mErrorText = (ExtendedTextView) mRootView.findViewById(R.id.errorText);
        mErrorDesc = (ExtendedTextView) mRootView.findViewById(R.id.errorDesc);
        mRefreshButton = (ExtendedTextView) mRootView.findViewById(R.id.refreshButton);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                doAfterPageStarted(view);
                mErrorOccurred = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mErrorOccurred) {
                    mLoadingView.setVisibility(View.GONE);
                    mErrorContainer.setVisibility(View.VISIBLE);
                } else {
                    mLoadingView.setVisibility(View.GONE);
                    mErrorContainer.setVisibility(View.GONE);
                }
                doAfterPageFinished(mWebView);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mErrorOccurred = true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                mErrorOccurred = true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (mTitleBarText != null) {
                    mTitleBarText.setText(title);
                }
            }

            @Override
            public boolean onJsAlert(final WebView view, final String url, final String message, final JsResult result) {
                if (mActivity.isFinishing()) {
                    result.cancel();
                    return true;
                }
                SingleActionDialog dialog = new SingleActionDialog(mActivity);
                dialog.setMessageText(message);
                dialog.setActionText("确定");
                dialog.setOnUserActionListener(new OnUserActionListener() {
                    @Override
                    public void onUserAction(int action, Object data) {
                        result.confirm();
                    }
                });
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(final WebView view, final String url, final String message, final JsResult result) {
                if (mActivity.isFinishing()) {
                    result.cancel();
                    return true;
                }
                DoubleActionDialog dialog = new DoubleActionDialog(mActivity);
                dialog.setMessageText(message);
                dialog.setActionLeftText("取消");
                dialog.setActionLeftTextTypefaceStyle(Typeface.BOLD);
                dialog.setActionRightText("确定");
                dialog.setOnUserActionListener(new OnUserActionListener() {
                    @Override
                    public void onUserAction(int action, Object data) {
                        if (action == DoubleActionDialog.ACTION_LEFT) {
                            result.cancel();
                        } else if (action == DoubleActionDialog.ACTION_RIGHT) {
                            result.confirm();
                        }
                    }
                });
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsPrompt(final WebView view, final String url, final String message, final String defaultValue, final JsPromptResult result) {
                if (mActivity.isFinishing()) {
                    result.cancel();
                    return true;
                }
                JsPromptDialog dialog = new JsPromptDialog(mActivity);
                dialog.setPromptMessage(message);
                dialog.setPromptDefaultValue(defaultValue);
                dialog.setActionLeftText("取消");
                dialog.setActionLeftTextTypefaceStyle(Typeface.BOLD);
                dialog.setActionRightText("确定");
                dialog.setOnUserActionListener(new OnUserActionListener() {
                    @Override
                    public void onUserAction(int action, Object data) {
                        if (action == DoubleActionDialog.ACTION_LEFT) {
                            result.cancel();
                        } else if (action == DoubleActionDialog.ACTION_RIGHT) {
                            result.confirm((String) data);
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (VERSION.SDK_INT <= VERSION_CODES.JELLY_BEAN) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
            mWebView.removeJavascriptInterface("accessibility");
            mWebView.removeJavascriptInterface("accessibilityTraversal");
        }
        mWebView.addJavascriptInterface(new JavascriptBridge(), "CJS");
        mRefreshButton.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(savedInstanceState);
    }

    private Runnable mLoadUrlRunnable;

    public void loadUrl(String url) {
        mUrl = url;
        if (mLoadUrlRunnable != null) {
            mHandler.removeCallbacks(mLoadUrlRunnable);
        }
        mLoadUrlRunnable = new Runnable() {
            @Override
            public void run() {
                doBeforeLoadUrl(mWebView);
                mLoadingView.setVisibility(View.VISIBLE);
                mErrorContainer.setVisibility(View.GONE);
                mWebView.loadUrl(mUrl);
                mLoadUrlRunnable = null;
            }
        };
        mHandler.postDelayed(mLoadUrlRunnable, 150);
    }

    public void loadData(String data) {
        mData = data;
        if (mLoadUrlRunnable != null) {
            mHandler.removeCallbacks(mLoadUrlRunnable);
        }
        mLoadUrlRunnable = new Runnable() {
            @Override
            public void run() {
                doBeforeLoadUrl(mWebView);
                mLoadingView.setVisibility(View.VISIBLE);
                mErrorContainer.setVisibility(View.GONE);
                mWebView.loadData(mData, "text/html", "UTF-8");
                mLoadUrlRunnable = null;
            }
        };
        mHandler.postDelayed(mLoadUrlRunnable, 150);
    }

    public boolean willHandleOnBackPressed() {
        if (!TextUtils.isEmpty(mOnBackPressedEvent)) {
            return true;
        } else {
            return mWebView.canGoBack();
        }
    }

    public void handleOnBackPressed() {
        if (!TextUtils.isEmpty(mOnBackPressedEvent)) {
            runJavascript(mOnBackPressedEvent);
        } else {
            mWebView.goBack();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mRefreshButton) {
            if (mUrl != null && !mUrl.equals("")) {
                loadUrl(mUrl);
            } else {
                loadData(mData);
            }
        }
    }

    @Override
    public void onDestroyView() {
        mWebView.destroy();
        super.onDestroyView();
    }

}
