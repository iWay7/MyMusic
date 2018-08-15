package site.iway.mymusic.user.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import site.iway.androidhelpers.EmptyDialog;
import site.iway.androidhelpers.UnitHelper;

/**
 * Created by iWay on 8/8/15.
 */
public class BaseDialog extends EmptyDialog {

    public interface OnUserActionListener {
        public void onUserAction(int action, Object data);
    }

    protected Context mContext;
    protected Handler mHandler;
    protected LayoutInflater mLayoutInflater;

    private float mWindowXOffsetDp;
    private float mWindowYOffsetDp;

    public float getWindowXOffsetDp() {
        return mWindowXOffsetDp;
    }

    public void setWindowXOffsetDp(float windowXOffsetDp) {
        mWindowXOffsetDp = windowXOffsetDp;
    }

    public float getWindowYOffsetDp() {
        return mWindowYOffsetDp;
    }

    public void setWindowYOffsetDp(float windowYOffsetDp) {
        mWindowYOffsetDp = windowYOffsetDp;
    }

    private void setWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.x = UnitHelper.dipToPxInt(mWindowXOffsetDp);
        layoutParams.y = UnitHelper.dipToPxInt(mWindowYOffsetDp);
    }

    public BaseDialog(Context context) {
        super(context);
        mContext = context;
        mHandler = new Handler();
        mLayoutInflater = getLayoutInflater();
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    protected OnUserActionListener mListener;

    public void setOnUserActionListener(OnUserActionListener l) {
        mListener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
    }

    protected void onUserAction(int action, Object data) {
        if (mListener != null) {
            mListener.onUserAction(action, data);
        }
    }

    protected void beforeShow() {
        // nothing
    }

    protected void afterShow() {
        // nothing
    }

    @Override
    public void show() {
        beforeShow();
        super.show();
        afterShow();
    }

    protected void beforeDismiss() {
        // nothing
    }

    protected void afterDismiss() {
        // nothing
    }

    @Override
    public void dismiss() {
        beforeDismiss();
        super.dismiss();
        afterDismiss();
    }

    public void dismiss(int action, Object data) {
        beforeDismiss();
        onUserAction(action, data);
        super.dismiss();
        afterDismiss();
    }

}
