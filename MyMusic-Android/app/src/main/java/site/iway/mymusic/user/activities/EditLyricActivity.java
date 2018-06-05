package site.iway.mymusic.user.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.util.List;

import site.iway.androidhelpers.ViewSwapper;
import site.iway.javahelpers.TextRW;
import site.iway.javahelpers.URLCodec;
import site.iway.mymusic.R;
import site.iway.mymusic.user.views.LRCEditView;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.FileCache;
import site.iway.mymusic.utils.LyricManager;

public class EditLyricActivity extends BaseActivity implements OnClickListener {

    public static final String SONG_FILE_NAME = "SONG_FILE_NAME";
    public static final String SONG_LYRIC_URL = "SONG_LYRIC_URL";

    private ViewSwapper mViewSwapper;
    private LRCEditView mLrcEditView;

    private static final int INDEX_LOADING = 0;
    private static final int INDEX_CONTENT = 1;
    private static final int INDEX_ERROR = 2;

    private MediaPlayer mMediaPlayer;
    private LyricManager mLyricManager;

    private Thread mThread = new Thread() {

        private boolean mErrorOccured;

        private void configMediaPlayer(String url) {
            try {
                FileCache musicCache = FileCache.getMusic();
                mMediaPlayer = new MediaPlayer();
                String filePath = musicCache.getFilePath(url);
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
            } catch (Exception e) {
                mErrorOccured = true;
            }
        }

        private void configLyricLines(String url) {
            FileInputStream inputStream = null;
            try {
                FileCache lyricCache = FileCache.getLyric();
                String filePath = lyricCache.getFilePath(url);
                inputStream = new FileInputStream(filePath);
                List<String> lines = TextRW.readAllLines(inputStream);
                mLyricManager = new LyricManager(lines);
            } catch (Exception e) {
                mErrorOccured = true;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        // nothing
                    }
                }
            }
        }

        @Override
        public void run() {
            String fileName = mIntent.getStringExtra(SONG_FILE_NAME);
            String fileUrl = Constants.MUSIC_URL_BASE + URLCodec.encode(fileName);
            FileCache musicCache = FileCache.getMusic();
            if (musicCache.exists(fileUrl)) {
                configMediaPlayer(fileUrl);
            } else {
                musicCache.download(fileUrl);
                do {
                    try {
                        Thread.sleep(333);
                    } catch (Exception e) {
                        break;
                    }
                }
                while (musicCache.isDownloading(fileUrl));
                if (musicCache.exists(fileUrl)) {
                    configMediaPlayer(fileUrl);
                } else {
                    mErrorOccured = true;
                }
            }
            FileCache lyricCache = FileCache.getLyric();
            String lyricUrl = mIntent.getStringExtra(SONG_LYRIC_URL);
            if (lyricCache.exists(lyricUrl)) {
                configLyricLines(lyricUrl);
            } else {
                lyricCache.download(lyricUrl);
                do {
                    try {
                        Thread.sleep(333);
                    } catch (Exception e) {
                        break;
                    }
                }
                while (lyricCache.isDownloading(lyricUrl));
                if (lyricCache.exists(lyricUrl)) {
                    configLyricLines(lyricUrl);
                } else {
                    mErrorOccured = true;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing())
                        return;
                    if (mErrorOccured) {
                        mViewSwapper.setDisplayedChild(INDEX_ERROR);
                    } else {
                        int duration = mMediaPlayer.getDuration();
                        mLrcEditView.setDuration(duration);
                        mLrcEditView.addLyricLines(mLyricManager.getLyricLines());
                        ViewGroup.LayoutParams layoutParams = mLrcEditView.getLayoutParams();
                        layoutParams.height = duration / 1000 * 40;
                        mViewSwapper.setDisplayedChild(INDEX_CONTENT);
                    }
                }
            });
        }
    };

    private void setViews() {
        mViewSwapper = (ViewSwapper) findViewById(R.id.viewSwapper);
        mLrcEditView = (LRCEditView) findViewById(R.id.lrcEditView);

        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText("调整歌词");

        mThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lyric);
        setViews();
    }

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
