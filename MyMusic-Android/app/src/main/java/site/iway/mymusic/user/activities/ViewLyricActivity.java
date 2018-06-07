package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.javahelpers.HttpTextReader;
import site.iway.javahelpers.URLCodec;
import site.iway.mymusic.R;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.FileCache;

public class ViewLyricActivity extends BaseActivity implements OnClickListener {

    public static final String SONG_FILE_NAME = "SONG_FILE_NAME";
    public static final String SONG_LYRIC_URL = "SONG_LYRIC_URL";
    public static final String RESULT_LYRIC_CHANGE = "RESULT_LYRIC_CHANGE";

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
        mTitleBarButton.setText("调整");
        mTitleBarButton.setOnClickListener(this);

        HttpTextReader httpTextReader = new HttpTextReader(mIntent.getStringExtra(SONG_LYRIC_URL)) {
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

    public static final int REQUEST_ADJUST_LYRIC = 0;

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mTitleBarButton) {
            Intent intent = new Intent(this, EditLyricActivity.class);
            intent.putExtra(EditLyricActivity.SONG_FILE_NAME, mIntent.getStringExtra(SONG_FILE_NAME));
            intent.putExtra(EditLyricActivity.SONG_LYRIC_URL, mIntent.getStringExtra(SONG_LYRIC_URL));
            startActivityForResult(intent, REQUEST_ADJUST_LYRIC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADJUST_LYRIC && resultCode == EditLyricActivity.RESULT_OK) {
            mTextView.setText(data.getStringExtra(EditLyricActivity.RESULT_TEXT));
            String fileName = mIntent.getStringExtra(SONG_FILE_NAME);
            String fileUrl = Constants.LYRIC_URL_BASE + URLCodec.encode(fileName);
            mIntent.putExtra(SONG_LYRIC_URL, fileUrl);
            FileCache fileCache = FileCache.getLyric();
            fileCache.delete(fileUrl);
            UIThread.event(Constants.EV_LYRIC_CHANGED);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
