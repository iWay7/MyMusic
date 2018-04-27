package site.iway.mymusic.utils;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.widget.Toast;

import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UnitHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.user.activities.BaseActivity;

/**
 * Created by iWay on 2017/7/13.
 */

public class Toaster {

    private static Context sContext;

    public static void initialize(Context context) {
        sContext = context;
    }

    public static void show(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (NotificationManagerCompat.from(sContext).areNotificationsEnabled()) {
                    ExtendedTextView toastView = new ExtendedTextView(sContext);
                    toastView.setBackgroundResource(R.drawable.bg_com_toast);
                    toastView.setGravity(Gravity.CENTER);
                    toastView.setMinWidth(UnitHelper.dipToPxInt(180));
                    int paddingLeft = UnitHelper.dipToPxInt(20);
                    int paddingRight = paddingLeft;
                    int paddingTop = UnitHelper.dipToPxInt(16);
                    int paddingBottom = paddingTop;
                    toastView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                    toastView.setTextColor(0xffffffff);
                    toastView.setTextSize(14);
                    toastView.setText(message);
                    Toast toast = new Toast(sContext);
                    toast.setView(toastView);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (BaseActivity.sRunningInstance != null) {
                        BaseActivity.sRunningInstance.showWeakHint(message);
                    }
                }
            }
        };
        UIThread.run(runnable);
    }

    public static void show(int stringResId) {
        String message = sContext.getString(stringResId);
        show(message);
    }

}
