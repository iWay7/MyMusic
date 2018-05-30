package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import site.iway.mymusic.R;
import site.iway.mymusic.user.views.ListActionItem;

public class SettingsActivity extends BaseActivity implements OnClickListener {

    private ListActionItem mGoAbout;

    private void setViews() {
        mGoAbout = findViewById(R.id.goAbout);
        mTitleBarText.setText("设置");
        mTitleBarBack.setOnClickListener(this);
        mGoAbout.setOnClickListener(this);
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
        } else if (v == mGoAbout) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
