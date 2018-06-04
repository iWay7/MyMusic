package site.iway.mymusic.user.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.javahelpers.HttpTextReader;
import site.iway.mymusic.R;

public class ViewLyricActivity extends BaseActivity implements OnClickListener {

    public static final String URL = "URL";

    private static final int INDEX_LOADING = 0;
    private static final int INDEX_LIST = 1;
    private static final int INDEX_ERROR = 2;

    private ViewSwapper mViewSwapper;
    private ExtendedTextView mTextView;

    private void setViews() {
        mViewSwapper = (ViewSwapper) findViewById(R.id.viewSwapper);
        mTextView = (ExtendedTextView) findViewById(R.id.textView);

        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText("歌词");
        mTitleBarButton.setText("编辑");
        mTitleBarButton.setOnClickListener(this);

        HttpTextReader httpTextReader = new HttpTextReader(mIntent.getStringExtra(URL)) {
            @Override
            public void onGetText(final String s) throws Exception {
                sleep(300);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewSwapper.setDisplayedChild(INDEX_LIST);
                        mTextView.setText(s);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                try {
                    sleep(300);
                } catch (InterruptedException e1) {
                    // nothing
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewSwapper.setDisplayedChild(INDEX_ERROR);
                    }
                });
            }
        };
        httpTextReader.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lyric);
        setViews();
    }

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mTitleBarButton) {
            // TODO
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
