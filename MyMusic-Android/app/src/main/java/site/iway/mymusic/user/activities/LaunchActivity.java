package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;

import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.utils.Player;

/**
 * Created by iWay on 2017/12/28.
 */

public class LaunchActivity extends BaseActivity {

    private Runnable mRedirector = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in_300, R.anim.none_300);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowHelper.makeTranslucent(this, true, false);
        Player player = Player.getInstance();
        if (player.isPlaying()) {
            mHandler.postDelayed(mRedirector, 300);
        } else {
            mHandler.postDelayed(mRedirector, 1500);
        }
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

}
