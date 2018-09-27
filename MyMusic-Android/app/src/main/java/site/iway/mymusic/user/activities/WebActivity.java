package site.iway.mymusic.user.activities;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;

import java.io.FileOutputStream;
import java.nio.charset.Charset;

import site.iway.androidhelpers.ExtendedLinearLayout;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.javahelpers.StringHelper;
import site.iway.javahelpers.TextRW;
import site.iway.mymusic.R;
import site.iway.mymusic.user.fragments.WebFragment;
import site.iway.mymusic.utils.Constants;

public class WebActivity extends BaseActivity implements OnClickListener {

    public static final String DEFAULT_TITLE = "DEFAULT_TITLE";
    public static final String URL = "URL";
    public static final String HTML = "HTML";

    protected WebFragment mWebFragment;

    protected ExtendedTextView mTitleBarClose;
    protected ExtendedLinearLayout mTitleBarActions;

    protected void addTitleBarAction(String action, OnClickListener onClickListener) {
        ExtendedTextView extendedTextView = new ExtendedTextView(this);
        extendedTextView.setText(action);
        extendedTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mResources.getDimensionPixelSize(R.dimen.titleBarButtonTextSize));
        extendedTextView.setTextColor(mResources.getColor(R.color.titleBarButtonTextColor));
        extendedTextView.setGravity(Gravity.CENTER);
        int padding = mResources.getDimensionPixelSize(R.dimen.titleBarButtonPadding);
        extendedTextView.setPadding(padding, 0, padding, 0);
        extendedTextView.setTextPressAlpha(mResources.getInteger(R.integer.titleBarTextPressAlpha));
        extendedTextView.setOnClickListener(onClickListener);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mTitleBarActions.addView(extendedTextView, layoutParams);
    }

    protected void loadUrl(String url) {
        mWebFragment.loadUrl(url);
    }

    protected void loadHtml(String html) {
        try {
            FileOutputStream fileOutputStream = openFileOutput(Constants.FILE_NAME_TEMP_HTML, MODE_PRIVATE);
            TextRW.writeAllText(fileOutputStream, Charset.forName("utf-8"), html);
            fileOutputStream.close();
            mWebFragment.loadUrl("file://" + getFilesDir() + "/" + Constants.FILE_NAME_TEMP_HTML);
        } catch (Exception e) {
            // nothing
        }
    }

    public void setViews() {
        mTitleBarClose = findViewById(R.id.titleBarClose);
        mTitleBarActions = findViewById(R.id.titleBarActions);
        mWebFragment = (WebFragment) mFragmentManager.findFragmentById(R.id.webFragment);

        mTitleBarBack.setOnClickListener(this);
        mTitleBarClose.setOnClickListener(this);
        mTitleBarText.setText(mIntent.getStringExtra(DEFAULT_TITLE));
        mTitleBarSplitter.setVisibility(View.GONE);

        mWebFragment.setTitleBarRoot(mTitleBarRoot);
        mWebFragment.setTitleBarBack(mTitleBarBack);
        mWebFragment.setTitleBarClose(mTitleBarClose);
        mWebFragment.setTitleBarActions(mTitleBarActions);
        mWebFragment.setTitleBarSplitter(mTitleBarSplitter);
        mWebFragment.setTitleBarText(mTitleBarText);

        if (!StringHelper.nullOrBlank(mIntent.getStringExtra(URL))) {
            loadUrl(mIntent.getStringExtra(URL));
        }
        if (!StringHelper.nullOrBlank(mIntent.getStringExtra(HTML))) {
            loadHtml(mIntent.getStringExtra(HTML));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setViews();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.titleBarBack) {
            onBackPressed();
        } else if (viewId == R.id.titleBarClose) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebFragment.willHandleOnBackPressed()) {
            mWebFragment.handleOnBackPressed();
        } else {
            finish();
        }
    }

}
