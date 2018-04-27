package site.iway.mymusic.user.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;

import site.iway.androidhelpers.ExtendedLinearLayout;

/**
 * Created by iWay on 2017/12/31.
 */

public class FitSysWindowsLinearLayout extends ExtendedLinearLayout {

    public FitSysWindowsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFitsSystemWindows(true);
    }

    public FitSysWindowsLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFitsSystemWindows(true);
    }

    public FitSysWindowsLinearLayout(Context context) {
        super(context);
        setFitsSystemWindows(true);
    }

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()));
        } else {
            return insets;
        }
    }

}
