package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import site.iway.androidhelpers.ExtendedEditText;
import site.iway.androidhelpers.ExtendedListView;
import site.iway.mymusic.R;

public class InputLyricTextActivity extends BaseActivity implements OnClickListener {

    public static final String DEFAULT_TEXT = "DEFAULT_TEXT";
    public static final String LYRIC_LINES = "LYRIC_LINES";
    public static final String RESULT_LINES = "RESULT_LINES";


    private ExtendedEditText mEditor;
    private ExtendedListView mQuickSelectList;

    private void setViews() {
        mEditor = (ExtendedEditText) findViewById(R.id.editor);
        mQuickSelectList = (ExtendedListView) findViewById(R.id.quickSelectList);

        mEditor.setText(mIntent.getStringExtra(DEFAULT_TEXT));

        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText("编辑");
        mTitleBarButton.setText("完成");
        mTitleBarButton.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_lyric_text);
        setViews();
    }

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mTitleBarButton) {
            String[] lines = mEditor.getText().toString().split("\n");
            Intent data = new Intent();
            data.putExtra(RESULT_LINES, lines);
            finish(RESULT_OK, data);
        }
    }

    @Override
    public void onBackPressed() {
        finish(RESULT_CANCELED);
    }

}
