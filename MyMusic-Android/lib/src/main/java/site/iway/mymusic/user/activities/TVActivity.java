package site.iway.mymusic.user.activities;

import android.os.Bundle;

import site.iway.mymusic.R;

/**
 * Created by iWay on 2017/12/28.
 */

public class TVActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
