package site.iway.mymusic.user.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;

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

    private static final int REQUEST_PERMISSIONS = 0;

    private void setPermissions() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, PermissionActivity.class);
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                intent.putExtra(PermissionActivity.PERMISSIONS, permissions);
                startActivityForResult(intent, REQUEST_PERMISSIONS);
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                switch (resultCode) {
                    case RESULT_OK:
                        mHandler.postDelayed(mRedirector, 500);
                        break;
                    default:
                        finish();
                        break;
                }
                break;
        }
    }

    private static boolean mInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWillInitTitleBarViews(false);
        WindowHelper.makeTranslucent(this, true, false);
        if (mInitialized) {
            mHandler.postDelayed(mRedirector, 300);
        } else {
            setPermissions();
        }
        mInitialized = true;
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

}
