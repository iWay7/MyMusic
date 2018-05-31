package site.iway.mymusic.user.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.FileOutputStream;
import java.nio.charset.Charset;

import site.iway.androidhelpers.ExtendedLinearLayout;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.javahelpers.StringHelper;
import site.iway.javahelpers.TextRW;
import site.iway.mymusic.R;
import site.iway.mymusic.user.fragments.WebFragment;
import site.iway.mymusic.user.fragments.WebFragment.OnJavascriptCallListener;
import site.iway.mymusic.utils.Constants;

public class WebActivity extends BaseActivity implements OnClickListener {

    public static final String DEFAULT_TITLE = "DEFAULT_TITLE";
    public static final String URL = "URL";
    public static final String HTML = "HTML";

    private WebFragment mWebFragment;

    public void setViews() {
        mWebFragment = (WebFragment) mFragmentManager.findFragmentById(R.id.webFragment);

        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText(mIntent.getStringExtra(DEFAULT_TITLE));
        mTitleBarSplitter.setVisibility(View.GONE);

        mWebFragment.setTitleBarRoot(mTitleBarRoot);
        mWebFragment.setTitleBarBack(mTitleBarBack);
        mWebFragment.setTitleBarClose((ExtendedTextView) findViewById(R.id.titleBarClose));
        mWebFragment.setTitleBarActions((ExtendedLinearLayout) findViewById(R.id.titleBarActions));
        mWebFragment.setTitleBarSplitter(mTitleBarSplitter);
        mWebFragment.setTitleBarText(mTitleBarText);
        if (!StringHelper.nullOrWhiteSpace(mIntent.getStringExtra(URL))) {
            mWebFragment.loadUrl(mIntent.getStringExtra(URL));
        }
        if (!StringHelper.nullOrWhiteSpace(mIntent.getStringExtra(HTML))) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = openFileOutput(Constants.FILE_NAME_TEMP_HTML, MODE_PRIVATE);
                Charset charset = Charset.forName("utf-8");
                String data = mIntent.getStringExtra(HTML);
                TextRW.writeAllText(fileOutputStream, charset, data);
                fileOutputStream.close();
                fileOutputStream = null;
                mWebFragment.loadUrl("file://" + getFilesDir() + "/" + Constants.FILE_NAME_TEMP_HTML);
            } catch (Exception e) {
                // nothing
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    // nothing
                }
            }
        }
        findViewById(R.id.titleBarClose).setOnClickListener(this);
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
