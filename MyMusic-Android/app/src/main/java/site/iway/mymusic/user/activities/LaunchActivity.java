package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by iWay on 2017/12/28.
 */

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // nothing
    }
}
