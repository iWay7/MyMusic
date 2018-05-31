package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;

import site.iway.androidhelpers.UIThread;
import site.iway.javahelpers.FolderScanner;
import site.iway.mymusic.R;
import site.iway.mymusic.user.dialogs.BaseDialog.OnUserActionListener;
import site.iway.mymusic.user.dialogs.ListActionDialog;
import site.iway.mymusic.user.views.ListActionItem;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.Settings;

public class SettingsActivity extends BaseActivity implements OnClickListener {

    private ListActionItem mGoSongCache;
    private ListActionItem mGoLyricCache;
    private ListActionItem mGoPlayListSortType;
    private ListActionItem mGoSearchSortType;
    private ListActionItem mGoAbout;

    private void setPlayListSortType() {
        switch (Settings.getPlayListSortType()) {
            case Settings.SORT_BY_ADD_TIME:
                mGoPlayListSortType.setDesc(SORT_BY_ADD_TIME);
                break;
            case Settings.SORT_BY_ARTIST_NAME:
                mGoPlayListSortType.setDesc(SORT_BY_ARTIST_NAME);
                break;
            case Settings.SORT_BY_SONG_NAME:
                mGoPlayListSortType.setDesc(SORT_BY_SONG_NAME);
                break;
        }
    }

    private void setSearchSortType() {
        switch (Settings.getSearchSortType()) {
            case Settings.SORT_BY_ADD_TIME:
                mGoSearchSortType.setDesc(SORT_BY_ADD_TIME);
                break;
            case Settings.SORT_BY_ARTIST_NAME:
                mGoSearchSortType.setDesc(SORT_BY_ARTIST_NAME);
                break;
            case Settings.SORT_BY_SONG_NAME:
                mGoSearchSortType.setDesc(SORT_BY_SONG_NAME);
                break;
        }
    }

    private FolderScanner mMP3Scanner = new FolderScanner() {
        private long mFileSize;

        @Override
        protected void onEnterFolder(File file) {
            // nothing
        }

        @Override
        protected void onDetectFile(File file) {
            mFileSize += file.length();
        }

        @Override
        protected void onSkipFile(File file) {
            // nothing
        }

        @Override
        protected void onCompleted() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        mGoSongCache.setDesc(mFileSize + " 字节");
                    }
                }
            });
        }
    };

    private FolderScanner mLyricScanner = new FolderScanner() {
        private long mFileSize;

        @Override
        protected void onEnterFolder(File file) {
            // nothing
        }

        @Override
        protected void onDetectFile(File file) {
            mFileSize += file.length();
        }

        @Override
        protected void onSkipFile(File file) {
            // nothing
        }

        @Override
        protected void onCompleted() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        mGoLyricCache.setDesc(mFileSize + " 字节");
                    }
                }
            });
        }
    };

    private void setViews() {
        mGoSongCache = (ListActionItem) findViewById(R.id.goSongCache);
        mGoLyricCache = (ListActionItem) findViewById(R.id.goLyricCache);
        mGoPlayListSortType = (ListActionItem) findViewById(R.id.goPlayListSortType);
        mGoSearchSortType = (ListActionItem) findViewById(R.id.goSearchSortType);
        mGoAbout = (ListActionItem) findViewById(R.id.goAbout);
        mMP3Scanner.addExtension(".mp3");
        File rootCacheDir = getCacheDir();
        File musicCacheDir = new File(rootCacheDir, Constants.DIR_NAME_MUSIC_CACHE);
        mMP3Scanner.addFolder(musicCacheDir);
        mMP3Scanner.start();
        File lyricCacheDir = new File(rootCacheDir, Constants.DIR_NAME_LYRIC_CACHE);
        mLyricScanner.addFolder(lyricCacheDir);
        mLyricScanner.start();
        mTitleBarText.setText("设置");
        mTitleBarBack.setOnClickListener(this);
        mGoSearchSortType.setOnClickListener(this);
        mGoPlayListSortType.setOnClickListener(this);
        setPlayListSortType();
        setSearchSortType();
        mGoAbout.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setViews();
    }

    private static final String SORT_BY_ADD_TIME = "按添加时间排序";
    private static final String SORT_BY_ARTIST_NAME = "按歌手名排序";
    private static final String SORT_BY_SONG_NAME = "按歌曲名排序";

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mGoPlayListSortType) {
            ListActionDialog dialog = new ListActionDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setActions(SORT_BY_ADD_TIME, SORT_BY_ARTIST_NAME, SORT_BY_SONG_NAME);
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (data == null) {
                        return;
                    }
                    switch ((String) data) {
                        case SORT_BY_ADD_TIME:
                            Settings.setPlayListSortType(Settings.SORT_BY_ADD_TIME);
                            break;
                        case SORT_BY_ARTIST_NAME:
                            Settings.setPlayListSortType(Settings.SORT_BY_ARTIST_NAME);
                            break;
                        case SORT_BY_SONG_NAME:
                            Settings.setPlayListSortType(Settings.SORT_BY_SONG_NAME);
                            break;
                    }
                    Settings.commit();
                    UIThread.event(Constants.EV_PLAY_LIST_SORT_TYPE_CHANGED);
                }
            });
            dialog.show();
        } else if (v == mGoSearchSortType) {
            ListActionDialog dialog = new ListActionDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setActions(SORT_BY_ADD_TIME, SORT_BY_ARTIST_NAME, SORT_BY_SONG_NAME);
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (data == null) {
                        return;
                    }
                    switch ((String) data) {
                        case SORT_BY_ADD_TIME:
                            Settings.setSearchSortType(Settings.SORT_BY_ADD_TIME);
                            break;
                        case SORT_BY_ARTIST_NAME:
                            Settings.setSearchSortType(Settings.SORT_BY_ARTIST_NAME);
                            break;
                        case SORT_BY_SONG_NAME:
                            Settings.setSearchSortType(Settings.SORT_BY_SONG_NAME);
                            break;
                    }
                    Settings.commit();
                    UIThread.event(Constants.EV_SEARCH_SORT_TYPE_CHANGED);
                }
            });
            dialog.show();
        } else if (v == mGoAbout) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_PLAY_LIST_SORT_TYPE_CHANGED:
                setPlayListSortType();
                break;
            case Constants.EV_SEARCH_SORT_TYPE_CHANGED:
                setSearchSortType();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
