package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.PackageHelper;
import site.iway.mymusic.R;

public class AboutActivity extends BaseActivity implements OnClickListener {

    private ExtendedTextView mVisitWebSite;
    private ExtendedTextView mVersionName;

    private void setViews() {
        mVisitWebSite = (ExtendedTextView) findViewById(R.id.visitWebSite);
        mVersionName = (ExtendedTextView) findViewById(R.id.versionName);

        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText("关于");
        mVisitWebSite.setOnClickListener(this);
        mVersionName.setOnClickListener(this);
        mVersionName.setText("版本号：" + PackageHelper.getPackageVersionName(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setViews();
    }

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mVisitWebSite) {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra(WebActivity.DEFAULT_TITLE, "iWay7 · GitHub");
            intent.putExtra(WebActivity.URL, "https://github.com/iWay7/MyMusic");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
