package site.iway.mymusic.user.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import site.iway.mymusic.user.dialogs.ActionDialog;
import site.iway.mymusic.user.dialogs.BaseDialog.OnUserActionListener;

public class PermissionActivity extends BaseActivity {

    private static final int REQUEST_PERMISSIONS = 0;
    private static final int REQUEST_SETTINGS = 1;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE
    };

    private void setPermissionRequiredComponents() {
        finish(RESULT_OK);
    }

    private long mPermissionRequestTime = Long.MIN_VALUE;
    private long mPermissionResponseTime = Long.MAX_VALUE;

    private boolean isUserDenied() {
        return mPermissionResponseTime - mPermissionRequestTime < 200000000;
    }

    private void checkPermissions(boolean showDialog) {
        mPermissionRequestTime = System.nanoTime();
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            List<String> toRequestPermissions = new ArrayList<>();
            for (String permission : PERMISSIONS) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    toRequestPermissions.add(permission);
                }
            }
            if (toRequestPermissions.isEmpty()) {
                setPermissionRequiredComponents();
            } else {
                if (showDialog) {
                    showPermissionRequestedDialog();
                } else {
                    String[] array = new String[0];
                    array = toRequestPermissions.toArray(array);
                    requestPermissions(array, REQUEST_PERMISSIONS);
                }
            }
        } else {
            setPermissionRequiredComponents();
        }
    }

    private void showPermissionRequestedDialog() {
        if (isUserDenied()) {
            ActionDialog dialog = new ActionDialog(this);
            dialog.setType(ActionDialog.TYPE_DOUBLE);
            dialog.setMessageText("需要一些基本的权限以使用");
            dialog.setDescBottomText("请进入应用详情->权限进行设置");
            dialog.setActionLeftText("退出应用");
            dialog.setActionRightText("进入应用详情");
            dialog.setActionRightTextTypefaceStyle(Typeface.BOLD);
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (action == ActionDialog.ACTION_RIGHT) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        startActivityForResult(intent, REQUEST_SETTINGS);
                    } else {
                        finish();
                    }
                }
            });
            dialog.show();
        } else {
            ActionDialog dialog = new ActionDialog(this);
            dialog.setType(ActionDialog.TYPE_DOUBLE);
            dialog.setMessageText("需要一些基本的权限以使用");
            dialog.setActionLeftText("退出应用");
            dialog.setActionRightText("尝试开启");
            dialog.setActionRightTextTypefaceStyle(Typeface.BOLD);
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (action == ActionDialog.ACTION_RIGHT) {
                        checkPermissions(false);
                    } else {
                        finish();
                    }
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                mPermissionResponseTime = System.nanoTime();
                boolean allGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    allGranted &= grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
                if (allGranted) {
                    setPermissionRequiredComponents();
                } else {
                    showPermissionRequestedDialog();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("mPermissionRequestTime", mPermissionRequestTime);
        outState.putLong("mPermissionResponseTime", mPermissionResponseTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            checkPermissions(false);
        } else {
            mPermissionRequestTime = savedInstanceState.getLong("mPermissionRequestTime");
            mPermissionResponseTime = savedInstanceState.getLong("mPermissionResponseTime");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SETTINGS:
                checkPermissions(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // nothing
    }

}
