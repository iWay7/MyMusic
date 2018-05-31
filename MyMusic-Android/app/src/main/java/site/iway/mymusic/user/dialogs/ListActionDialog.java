package site.iway.mymusic.user.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import site.iway.androidhelpers.ExtendedLinearLayout;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.mymusic.R;

/**
 * Created by iWay on 8/20/15.
 */
public class ListActionDialog extends BaseDialog implements OnClickListener {

    public static final int ACTION_USER = 0;
    public static final int ACTION_CANCEL = 1;

    public ListActionDialog(Context context) {
        super(context);
    }

    private void setWindow() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogAnimationSlideBottom);
    }

    private ExtendedLinearLayout mActionContainer;
    private ExtendedTextView mCancelView;
    private String[] mActions;

    public void setActions(String... actions) {
        mActions = actions;
    }

    private void setViews() {
        mActionContainer = (ExtendedLinearLayout) findViewById(R.id.actionContainer);
        mCancelView = (ExtendedTextView) findViewById(R.id.cancel);
        if (mActions != null) {
            for (int i = 0; i < mActions.length; i++) {
                View view = mLayoutInflater.inflate(R.layout.group_action_item, mActionContainer, false);
                ExtendedTextView actionText = (ExtendedTextView) view.findViewById(R.id.actionText);
                actionText.setText(mActions[i]);
                actionText.setTag(mActions[i]);
                actionText.setOnClickListener(this);
                mActionContainer.addView(view);
            }
        }
        mCancelView.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        setContentView(R.layout.dialog_list_action);
        setViews();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.actionText) {
            Object data = v.getTag();
            dismiss(ACTION_USER, data);
        } else if (viewId == R.id.cancel) {
            dismiss(ACTION_CANCEL, null);
        }
    }

}
