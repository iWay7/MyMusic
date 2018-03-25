package site.iway.mymusic.user.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.mymusic.R;

/**
 * Created by iWay on 2016/4/20.
 */
public class ActionDialog extends BaseDialog implements OnClickListener {

    public static final int TYPE_SINGLE = 0;
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_TRIPLE = 2;

    public static final int ACTION_DEFAULT = -1;
    public static final int ACTION_LEFT = 0;
    public static final int ACTION_MIDDLE = 1;
    public static final int ACTION_RIGHT = 2;

    public ActionDialog(Context context) {
        super(context);
    }

    private boolean mAutoDismiss = true;
    private String mDescTopText;
    private Integer mDescTopTextTypefaceStyle;
    private String mMessageText;
    private Float mMessageTextSize;
    private Integer mMessageTextColor;
    private Integer mMessageTextTypefaceStyle;
    private String mDescBottomText;
    private Integer mDescBottomTextColor;
    private Float mDescBottomTextSize;
    private Integer mDescBottomTextTypefaceStyle;
    private String mActionText;
    private Integer mActionTextTypefaceStyle;
    private String mActionLeftText;
    private Integer mActionLeftTextTypefaceStyle;
    private String mActionMiddleText;
    private Integer mActionMiddleTextTypefaceStyle;
    private String mActionRightText;
    private Integer mActionRightTextTypefaceStyle;
    private int mType;

    public boolean isAutoDismiss() {
        return mAutoDismiss;
    }

    public void setAutoDismiss(boolean autoDismiss) {
        mAutoDismiss = autoDismiss;
    }

    public void setDescTopText(String descTopText) {
        mDescTopText = descTopText;
    }

    public void setDescBottomTextTypefaceStyle(Integer descBottomTextTypefaceStyle) {
        mDescBottomTextTypefaceStyle = descBottomTextTypefaceStyle;
    }

    public void setMessageText(String messageText) {
        mMessageText = messageText;
    }

    public void setMessageTextSize(Float messageTextSize) {
        mMessageTextSize = messageTextSize;
    }

    public void setMessageTextColor(Integer messageTextColor) {
        mMessageTextColor = messageTextColor;
    }

    public void setMessageTextTypefaceStyle(Integer messageTextTypefaceStyle) {
        mMessageTextTypefaceStyle = messageTextTypefaceStyle;
    }

    public void setDescBottomText(String descBottomText) {
        mDescBottomText = descBottomText;
    }

    public void setDescBottomTextColor(Integer descBottomTextColor) {
        mDescBottomTextColor = descBottomTextColor;
    }

    public void setDescBottomTextSize(Float descBottomTextSize) {
        mDescBottomTextSize = descBottomTextSize;
    }

    public void setDescTopTextTypefaceStyle(Integer descTopTextTypefaceStyle) {
        mDescTopTextTypefaceStyle = descTopTextTypefaceStyle;
    }

    public void setActionText(String actionText) {
        mActionText = actionText;
    }

    public void setActionTextTypefaceStyle(Integer actionTextTypefaceStyle) {
        mActionTextTypefaceStyle = actionTextTypefaceStyle;
    }

    public void setActionLeftText(String actionLeftText) {
        mActionLeftText = actionLeftText;
    }

    public void setActionLeftTextTypefaceStyle(Integer actionLeftTextTypefaceStyle) {
        mActionLeftTextTypefaceStyle = actionLeftTextTypefaceStyle;
    }

    public void setActionMiddleText(String actionMiddleText) {
        mActionMiddleText = actionMiddleText;
    }

    public void setActionMiddleTextTypefaceStyle(Integer actionMiddleTextTypefaceStyle) {
        mActionMiddleTextTypefaceStyle = actionMiddleTextTypefaceStyle;
    }

    public void setActionRightText(String actionRightText) {
        mActionRightText = actionRightText;
    }

    public void setActionRightTextTypefaceStyle(Integer actionRightTextTypefaceStyle) {
        mActionRightTextTypefaceStyle = actionRightTextTypefaceStyle;
    }

    public void setType(int type) {
        switch (type) {
            case TYPE_SINGLE:
            case TYPE_DOUBLE:
            case TYPE_TRIPLE:
                mType = type;
                break;
        }
    }

    private ExtendedTextView mDescTopView;
    private ExtendedTextView mMessageView;
    private ExtendedTextView mDescBottomView;
    private ViewSwapper mTypeSwitcher;
    private ExtendedTextView mActionView;
    private ExtendedTextView mActionLeftView;
    private ExtendedTextView mActionRightView;
    private ExtendedTextView mActionMiddleView;

    private void setViews() {
        mDescTopView = (ExtendedTextView) findViewById(R.id.descTopView);
        mMessageView = (ExtendedTextView) findViewById(R.id.messageView);
        mDescBottomView = (ExtendedTextView) findViewById(R.id.descBottomView);
        mTypeSwitcher = (ViewSwapper) findViewById(R.id.typeSwitcher);
        mActionView = (ExtendedTextView) findViewById(R.id.actionView);
        mActionLeftView = (ExtendedTextView) findViewById(R.id.actionLeftView);
        mActionRightView = (ExtendedTextView) findViewById(R.id.actionRightView);
        mActionMiddleView = (ExtendedTextView) findViewById(R.id.actionMiddleView);

        mDescTopView.setText(mDescTopText);
        mDescTopView.setVisibility(TextUtils.isEmpty(mDescTopText) ? View.GONE : View.VISIBLE);
        if (mDescTopTextTypefaceStyle != null)
            mDescTopView.setTypeface(mDescTopView.getTypeface(), mDescTopTextTypefaceStyle);
        mMessageView.setText(mMessageText);
        mMessageView.setVisibility(TextUtils.isEmpty(mMessageText) ? View.GONE : View.VISIBLE);
        if (mMessageTextSize != null)
            mMessageView.setTextSize(mMessageTextSize);
        if (mMessageTextColor != null)
            mMessageView.setTextColor(mMessageTextColor);
        if (mMessageTextTypefaceStyle != null)
            mMessageView.setTypeface(mMessageView.getTypeface(), mMessageTextTypefaceStyle);
        mDescBottomView.setText(mDescBottomText);
        mDescBottomView.setVisibility(TextUtils.isEmpty(mDescBottomText) ? View.GONE : View.VISIBLE);
        if (mDescBottomTextColor != null)
            mDescBottomView.setTextColor(mDescBottomTextColor);
        if (mDescBottomTextSize != null)
            mDescBottomView.setTextSize(mDescBottomTextSize);
        if (mDescBottomTextTypefaceStyle != null)
            mDescBottomView.setTypeface(mDescBottomView.getTypeface(), mDescBottomTextTypefaceStyle);
        mTypeSwitcher.setDisplayedChild(mType);
        mActionView.setText(mActionText);
        mActionView.setOnClickListener(this);
        if (mActionTextTypefaceStyle != null)
            mActionView.setTypeface(mActionView.getTypeface(), mActionTextTypefaceStyle);
        mActionLeftView.setText(mActionLeftText);
        mActionLeftView.setOnClickListener(this);
        if (mActionLeftTextTypefaceStyle != null)
            mActionLeftView.setTypeface(mActionLeftView.getTypeface(), mActionLeftTextTypefaceStyle);
        mActionMiddleView.setText(mActionMiddleText);
        mActionMiddleView.setOnClickListener(this);
        if (mActionMiddleTextTypefaceStyle != null)
            mActionMiddleView.setTypeface(mActionMiddleView.getTypeface(), mActionMiddleTextTypefaceStyle);
        mActionRightView.setText(mActionRightText);
        mActionRightView.setOnClickListener(this);
        if (mActionRightTextTypefaceStyle != null)
            mActionRightView.setTypeface(mActionRightView.getTypeface(), mActionRightTextTypefaceStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_action);
        setViews();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.actionView) {
            if (mAutoDismiss)
                dismiss(ACTION_DEFAULT, null);
            else
                onUserAction(ACTION_DEFAULT, null);
        } else if (viewId == R.id.actionLeftView) {
            if (mAutoDismiss)
                dismiss(ACTION_LEFT, null);
            else
                onUserAction(ACTION_LEFT, null);
        } else if (viewId == R.id.actionMiddleView) {
            if (mAutoDismiss)
                dismiss(ACTION_MIDDLE, null);
            else
                onUserAction(ACTION_MIDDLE, null);
        } else if (viewId == R.id.actionRightView) {
            if (mAutoDismiss)
                dismiss(ACTION_RIGHT, null);
            else
                onUserAction(ACTION_RIGHT, null);
        }
    }

}
