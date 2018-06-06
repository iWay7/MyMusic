package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.UITimer;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.javahelpers.TextRW;
import site.iway.javahelpers.URLCodec;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.mymusic.models.SaveLyricReq;
import site.iway.mymusic.user.views.LRCEditView;
import site.iway.mymusic.user.views.LRCEditView.OnEditActionListener;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.FileCache;
import site.iway.mymusic.utils.LyricManager;
import site.iway.mymusic.utils.LyricManager.LyricLine;
import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;
import site.iway.mymusic.utils.Toaster;

public class EditLyricActivity extends BaseActivity implements OnClickListener, OnEditActionListener, RPCCallback {

    public static final String SONG_FILE_NAME = "SONG_FILE_NAME";
    public static final String SONG_LYRIC_URL = "SONG_LYRIC_URL";

    private ViewSwapper mViewSwapper;
    private LRCEditView mLrcEditView;
    private ExtendedImageView mPlayPause;
    private ExtendedImageView mRemove;
    private ExtendedImageView mEdit;
    private ExtendedImageView mAdd;

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
            try {
                sleep(300);
            } catch (InterruptedException e) {
                // nothing
            }
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
                        mTitleBarButton.setText("保存");
                        mTitleBarButton.setOnClickListener(EditLyricActivity.this);
                        int duration = mMediaPlayer.getDuration();
                        mLrcEditView.setListener(EditLyricActivity.this);
                        mLrcEditView.setDuration(duration);
                        mLrcEditView.addLyricLines(mLyricManager.getLyricLines());
                        ViewGroup.LayoutParams layoutParams = mLrcEditView.getLayoutParams();
                        layoutParams.height = duration / 1000 * 40;
                        mViewSwapper.setDisplayedChild(INDEX_CONTENT);
                        mPlayPause.setOnClickListener(EditLyricActivity.this);
                        mRemove.setOnClickListener(EditLyricActivity.this);
                        mEdit.setOnClickListener(EditLyricActivity.this);
                        mAdd.setOnClickListener(EditLyricActivity.this);
                        mPositionUpdater.start(false);
                    }
                }
            });
        }
    };

    private UITimer mPositionUpdater = new UITimer(100) {
        @Override
        public void doOnUIThread() {
            mLrcEditView.mPosition(mMediaPlayer.getCurrentPosition());
            if (mMediaPlayer.isPlaying()) {
                mPlayPause.setImageResource(R.drawable.icon_pause);
            } else {
                mPlayPause.setImageResource(R.drawable.icon_play);
            }
        }
    };

    private void setViews() {
        mViewSwapper = (ViewSwapper) findViewById(R.id.viewSwapper);
        mLrcEditView = (LRCEditView) findViewById(R.id.lrcEditView);
        mPlayPause = (ExtendedImageView) findViewById(R.id.playPause);
        mRemove = (ExtendedImageView) findViewById(R.id.remove);
        mEdit = (ExtendedImageView) findViewById(R.id.edit);
        mAdd = (ExtendedImageView) findViewById(R.id.add);

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
    public void onRequestAdjustPosition(int position) {
        mMediaPlayer.seekTo(position);
    }

    private static final int REQUEST_ADD_TEXT = 0;
    private static final int REQUEST_EDIT_TEXT = 1;

    private SaveLyricReq mSaveLyricReq;

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mPlayPause) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
                Player player = Player.getInstance();
                PlayTask playTask = player.getPlayTask();
                if (playTask != null) {
                    playTask.pause();
                }
                mMediaPlayer.start();
            }
        } else if (v == mRemove) {
            mLrcEditView.removeSelectedLyricLine();
        } else if (v == mAdd) {
            Intent intent = new Intent(this, InputLyricTextActivity.class);
            intent.putExtra(InputLyricTextActivity.DEFAULT_TEXT, mLrcEditView.getSelectedLyricLineText());
            Set<String> set = new HashSet<>();
            for (LyricLine lyricLine : mLyricManager.getLyricLines()) {
                set.addAll(lyricLine.sourceTextLines);
            }
            intent.putExtra(InputLyricTextActivity.LYRIC_LINES, set.toArray(new String[0]));
            startActivityForResult(intent, REQUEST_ADD_TEXT);
        } else if (v == mTitleBarButton) {
            disableUserInteract();
            showLoadingView();
            mSaveLyricReq = new SaveLyricReq();
            mSaveLyricReq.fileName = mIntent.getStringExtra(SONG_FILE_NAME);
            mSaveLyricReq.lyric = mLrcEditView.generateLrcFile();
            mSaveLyricReq.minDelayTime = 300;
            mSaveLyricReq.start(this);
        } else if (v == mEdit) {
            if (mLrcEditView.hasSelectedView()) {
                Intent intent = new Intent(this, InputLyricTextActivity.class);
                intent.putExtra(InputLyricTextActivity.DEFAULT_TEXT, mLrcEditView.getSelectedLyricLineText());
                Set<String> set = new HashSet<>();
                for (LyricLine lyricLine : mLyricManager.getLyricLines()) {
                    set.addAll(lyricLine.sourceTextLines);
                }
                intent.putExtra(InputLyricTextActivity.LYRIC_LINES, set.toArray(new String[0]));
                startActivityForResult(intent, REQUEST_EDIT_TEXT);
            } else {
                Toaster.show("请选择要编辑的行~");
            }
        }
    }

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mSaveLyricReq) {
            hideLoadingView();
            finish(RESULT_OK);
            enableUserInteract();
        }

    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        if (req == mSaveLyricReq) {
            hideLoadingView();
            Toaster.show("网络错误，请重试！");
            enableUserInteract();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADD_TEXT:
                if (resultCode == InputLyricTextActivity.RESULT_OK) {
                    String[] lines = data.getStringArrayExtra(InputLyricTextActivity.RESULT_LINES);
                    mLrcEditView.addLyricLineText(Arrays.asList(lines));
                }
                break;
            case REQUEST_EDIT_TEXT:
                if (resultCode == InputLyricTextActivity.RESULT_OK) {
                    String[] lines = data.getStringArrayExtra(InputLyricTextActivity.RESULT_LINES);
                    mLrcEditView.setSelectedLyricLineText(Arrays.asList(lines));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        if (mPositionUpdater != null) {
            mPositionUpdater.stop();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        super.onDestroy();
    }

}
