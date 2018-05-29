package site.iway.mymusic.user.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;

public class SettingsActivity extends BaseActivity implements OnClickListener {

    private void setViews() {
        mTitleBarText.setText("设置");
        mTitleBarBack.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setViews();
    }

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
