package site.iway.mymusic.user.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import site.iway.androidhelpers.ExtendedEditText;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;

public class JsPromptDialog extends DoubleActionDialog {

    public JsPromptDialog(Context context) {
        super(context);
    }

    private String mPromptMessage;
    private String mPromptDefaultValue;

    public void setPromptMessage(String promptMessage) {
        mPromptMessage = promptMessage;
    }

    public void setPromptDefaultValue(String promptDefaultValue) {
        mPromptDefaultValue = promptDefaultValue;
    }

    private ExtendedTextView mTitle;
    private ExtendedEditText mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        viewGroup = (ViewGroup) viewGroup.getChildAt(0);
        viewGroup.removeViewAt(0);
        View newContent = mLayoutInflater.inflate(R.layout.dialog_js_prompt, viewGroup, false);
        viewGroup.addView(newContent, 0);

        mTitle = (ExtendedTextView) findViewById(R.id.title);
        mContent = (ExtendedEditText) findViewById(R.id.content);

        mTitle.setText(mPromptMessage);
        mContent.setText(mPromptDefaultValue);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WindowHelper.showSoftInput(mContent);
            }
        }, 300);
    }

    @Override
    public void onUserAction(int action, Object data) {
        super.onUserAction(action, data);
    }

    @Override
    protected Object getResultData(int action) {
        if (action == ACTION_RIGHT) {
            return mContent.getText().toString();
        }
        return super.getResultData(action);
    }

}
