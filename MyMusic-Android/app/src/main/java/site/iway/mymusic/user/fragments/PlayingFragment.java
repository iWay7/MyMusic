package site.iway.mymusic.user.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import site.iway.androidhelpers.BitmapFilter;
import site.iway.androidhelpers.BitmapHelper;
import site.iway.androidhelpers.BitmapView;
import site.iway.androidhelpers.ExtendedFrameLayout;
import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ExtendedView;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UITimer;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.androidhelpers.WindowHelper;
import site.iway.javahelpers.Scale;
import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.mymusic.GetSongInfoReq;
import site.iway.mymusic.net.mymusic.GetSongInfoRes;
import site.iway.mymusic.net.mymusic.internal.SongInfo;
import site.iway.mymusic.user.activities.AlbumListActivity;
import site.iway.mymusic.user.activities.LyricListActivity;
import site.iway.mymusic.user.activities.SettingsActivity;
import site.iway.mymusic.user.activities.ViewSongsActivity;
import site.iway.mymusic.user.views.LRCView;
import site.iway.mymusic.user.views.LineLRCView;
import site.iway.mymusic.user.views.PlayProgressView;
import site.iway.mymusic.user.views.PlayProgressView.OnRequestPlayProgressListener;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.PlayList;
import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;
import site.iway.mymusic.utils.Song;

/**
 * Created by iWay on 2017/12/25.
 */

public class PlayingFragment extends BaseFragment implements RPCCallback, OnClickListener, OnRequestPlayProgressListener, OnLongClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    private Player mPlayer = Player.getInstance();

    private BitmapView mBackground;
    private ExtendedView mTitleBarPad;
    private ExtendedImageView mTitleBarBack;
    private ExtendedTextView mTitleBarText;
    private ExtendedImageView mTitleBarImage;
    private ExtendedTextView mArtist;
    private ViewSwapper mViewSwapper;
    private ExtendedFrameLayout mDiskContainer;
    private BitmapView mSongArt;
    private LineLRCView mLrc;
    private ExtendedImageView mDiskHandle;
    private LRCView mLrcView;
    private ExtendedTextView mPosition;
    private PlayProgressView mPlayProgress;
    private ExtendedTextView mDuration;
    private ViewSwapper mSwitchMode;
    private ExtendedImageView mPrevious;
    private ViewSwapper mPlayPause;
    private ExtendedImageView mNext;
    private ExtendedImageView mSettings;

    private void refreshPlayMode() {
        PlayList playList = mPlayer.getPlayList();
        switch (playList.getMode()) {
            case PlayList.MODE_LOOP_LIST:
                mSwitchMode.setDisplayedChild(0);
                break;
            case PlayList.MODE_LOOP_SINGLE:
                mSwitchMode.setDisplayedChild(1);
                break;
            case PlayList.MODE_RANDOM:
                mSwitchMode.setDisplayedChild(2);
                break;
        }
    }

    private String getTime(int millis) {
        int minute = millis / 1000 / 60;
        int second = millis / 1000 % 60;
        return (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
    }

    private void refreshViews() {
        PlayTask playTask = mPlayer.getPlayTask();
        if (playTask != null) {
            String fileName = playTask.getFileName();
            Song song = new Song(fileName);
            mTitleBarText.setText(song.name);
            mArtist.setText(song.artist);
            loadImageAndLyric(fileName);
        } else {
            mTitleBarText.setText(R.string.app_name);
            mArtist.setText(null);
            mBackground.loadFromURLSource(null);
            mSongArt.loadFromURLSource(null);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBackground = (BitmapView) mRootView.findViewById(R.id.background);
        mTitleBarPad = (ExtendedView) mRootView.findViewById(R.id.titleBarPad);
        mTitleBarBack = (ExtendedImageView) mRootView.findViewById(R.id.titleBarBack);
        mTitleBarText = (ExtendedTextView) mRootView.findViewById(R.id.titleBarText);
        mTitleBarImage = (ExtendedImageView) mRootView.findViewById(R.id.titleBarImage);
        mArtist = (ExtendedTextView) mRootView.findViewById(R.id.artist);
        mViewSwapper = (ViewSwapper) mRootView.findViewById(R.id.viewSwapper);
        mDiskContainer = (ExtendedFrameLayout) mRootView.findViewById(R.id.diskContainer);
        mSongArt = (BitmapView) mRootView.findViewById(R.id.songArt);
        mLrc = (LineLRCView) mRootView.findViewById(R.id.lrc);
        mDiskHandle = (ExtendedImageView) mRootView.findViewById(R.id.diskHandle);
        mLrcView = (LRCView) mRootView.findViewById(R.id.lrcView);
        mPosition = (ExtendedTextView) mRootView.findViewById(R.id.position);
        mPlayProgress = (PlayProgressView) mRootView.findViewById(R.id.playProgress);
        mDuration = (ExtendedTextView) mRootView.findViewById(R.id.duration);
        mSwitchMode = (ViewSwapper) mRootView.findViewById(R.id.switchMode);
        mPrevious = (ExtendedImageView) mRootView.findViewById(R.id.previous);
        mPlayPause = (ViewSwapper) mRootView.findViewById(R.id.playPause);
        mNext = (ExtendedImageView) mRootView.findViewById(R.id.next);
        mSettings = (ExtendedImageView) mRootView.findViewById(R.id.settings);

        if (WindowHelper.makeTranslucent(mActivity, true, false)) {
            mTitleBarPad.setVisibility(View.VISIBLE);
            int statusBarHeight = WindowHelper.getStatusBarHeight(mActivity);
            LayoutParams layoutParams = mTitleBarPad.getLayoutParams();
            layoutParams.height = statusBarHeight;
        }
        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText(R.string.app_name);
        mTitleBarText.setTypeface(Typeface.DEFAULT_BOLD);
        mTitleBarImage.setImageResource(R.drawable.icon_menu);
        mTitleBarImage.setOnClickListener(this);
        mPlayProgress.setListener(this);
        mSwitchMode.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mSettings.setOnClickListener(this);
        mDiskContainer.setOnClickListener(this);
        mDiskContainer.setOnLongClickListener(this);
        mLrcView.setOnClickListener(this);
        mLrcView.setOnLongClickListener(this);

        refreshViews();
        refreshPlayMode();

        mUITimer.start(false);
    }

    private GetSongInfoReq mLastFetchSongInfo;

    private void loadImageAndLyric(String fileName) {
        if (mLastFetchSongInfo != null) {
            mLastFetchSongInfo.cancel();
            mLastFetchSongInfo = null;
        }
        mLastFetchSongInfo = new GetSongInfoReq();
        mLastFetchSongInfo.minDelayTime = 300;
        Song song = new Song(fileName);
        mLastFetchSongInfo.query = song.artist + " " + song.name;
        mLastFetchSongInfo.fileName = fileName;
        mLastFetchSongInfo.tag = song;
        mLastFetchSongInfo.cacheEnabled = true;
        mLastFetchSongInfo.start(this);

    }

    private BitmapFilter mBitmapFilter = new BitmapFilter() {
        private String uuid = StringHelper.uuid();

        @Override
        public String id() {
            return uuid;
        }

        @Override
        public Bitmap filter(Bitmap bitmap) {
            float viewWidth = mBackground.getWidth();
            float viewHeight = mBackground.getHeight();
            int width = (int) (viewWidth / 20);
            int height = (int) (viewHeight / 20);
            bitmap = BitmapHelper.clip(bitmap, Scale.CenterCrop, width, height, 0);
            bitmap = BitmapHelper.blur(bitmap, 10);
            return bitmap;
        }
    };

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mLastFetchSongInfo) {
            GetSongInfoRes getSongInfoRes = (GetSongInfoRes) req.response;
            String imageLink = null;
            String lrcLink = null;
            if (getSongInfoRes.list != null) {
                for (SongInfo songInfo : getSongInfoRes.list) {
                    if (TextUtils.isEmpty(imageLink) && !TextUtils.isEmpty(songInfo.imgLink)) {
                        imageLink = songInfo.imgLink;
                    }
                    if (TextUtils.isEmpty(lrcLink) && !TextUtils.isEmpty(songInfo.lrcLink)) {
                        lrcLink = songInfo.lrcLink;
                    }
                    if (!TextUtils.isEmpty(imageLink) && !TextUtils.isEmpty(lrcLink)) {
                        break;
                    }
                }
            }
            mBackground.loadFromURLSource(imageLink, mBitmapFilter);
            if (imageLink != null) {
                int lastIndexOfAt = imageLink.lastIndexOf('@');
                if (lastIndexOfAt > -1) {
                    imageLink = imageLink.substring(0, lastIndexOfAt);
                }
            }
            mSongArt.loadFromURLSource(imageLink);
            mLrc.load(lrcLink);
            mLrcView.load(lrcLink);
        }
    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        if (req == mLastFetchSongInfo) {
            mBackground.loadFromURLSource(null);
            mSongArt.loadFromURLSource(null);
            mLrc.load(null);
            mLrcView.load(null);
        }
    }

    private UITimer mUITimer = new UITimer(16) {

        private int mTicks = 0;

        private boolean isPlayerPlaying() {
            PlayTask playTask = mPlayer.getPlayTask();
            if (playTask != null) {
                int playState = playTask.getTaskState();
                return playState == PlayTask.STATE_PLAYING;
            }
            return false;
        }

        private void setDisk() {
            if (isPlayerPlaying()) {
                float current = mDiskContainer.getRotation();
                current += 0.5;
                mDiskContainer.setRotation(current);
            }
        }

        private float mPausedDegrees = -30;
        private float mPlayingDegrees = 0;

        private void setDiskHandle() {
            if (isPlayerPlaying()) {
                float current = mDiskHandle.getRotation();
                if (current < 0) {
                    float target = current + 1;
                    if (target > mPlayingDegrees) {
                        target = mPausedDegrees;
                    }
                    mDiskHandle.setRotation(target);
                }
            } else {
                float current = mDiskHandle.getRotation();
                if (current > -30) {
                    float target = current - 1;
                    if (target < mPausedDegrees) {
                        target = mPausedDegrees;
                    }
                    mDiskHandle.setRotation(target);
                }
            }
        }

        private void setProgress() {
            if (mTicks % 8 == 0) {
                PlayTask playTask = mPlayer.getPlayTask();
                if (playTask == null) {
                    mPlayProgress.setProgress(0);
                    mPosition.setText(getTime(0));
                    mDuration.setText(getTime(0));
                } else {
                    switch (playTask.getTaskState()) {
                        case PlayTask.STATE_READY:
                        case PlayTask.STATE_TASK_START:
                        case PlayTask.STATE_DOWNLOADING:
                        case PlayTask.STATE_DATA_READY:
                        case PlayTask.STATE_ERROR:
                        case PlayTask.STATE_TASK_CANCELED:
                            mPlayProgress.setProgress(0);
                            mPosition.setText(getTime(0));
                            mDuration.setText(getTime(0));
                            break;
                        case PlayTask.STATE_PLAYING:
                        case PlayTask.STATE_PAUSED:
                        case PlayTask.STATE_COMPLETED:
                            float duration = playTask.getDuration();
                            float position = playTask.getPosition();
                            float percent = position * 100 / duration;
                            mPlayProgress.setProgress(percent);
                            if (mTicks % 40 == 0) {
                                mPosition.setText(getTime(playTask.getPosition()));
                                mDuration.setText(getTime(playTask.getDuration()));
                            }
                            break;
                    }
                }
            }
        }

        private static final int INDEX_PLAY = 0;
        private static final int INDEX_PAUSE = 1;
        private static final int INDEX_LOADING = 2;

        private void setPlayPause() {
            if (mTicks % 8 == 0) {
                PlayTask playTask = mPlayer.getPlayTask();
                if (playTask == null) {
                    mPlayPause.setDisplayedChild(INDEX_PLAY);
                } else {
                    switch (playTask.getTaskState()) {
                        case PlayTask.STATE_READY:
                        case PlayTask.STATE_TASK_START:
                        case PlayTask.STATE_DOWNLOADING:
                        case PlayTask.STATE_DATA_READY:
                            mPlayPause.setDisplayedChild(INDEX_LOADING);
                            break;
                        case PlayTask.STATE_PLAYING:
                            mPlayPause.setDisplayedChild(INDEX_PAUSE);
                            break;
                        default:
                            mPlayPause.setDisplayedChild(INDEX_PLAY);
                            break;
                    }
                }
            }
        }

        @Override
        public void doOnUIThread() {
            mTicks++;
            setDisk();
            setDiskHandle();
            setProgress();
            setPlayPause();
        }
    };

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_PLAYER_TASK_CHANGED:
                refreshViews();
                break;
            case Constants.EV_PLAY_LIST_MODE_CHANGED:
                refreshPlayMode();
                break;
            case Constants.EV_LYRIC_CHANGED:
            case Constants.EV_ALBUM_CHANGED:
                PlayTask playTask = mPlayer.getPlayTask();
                if (playTask != null) {
                    loadImageAndLyric(playTask.getFileName());
                }
                break;
        }
    }

    private static final int REQUEST_CODE_VIEW_SONGS = 0;

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            UIThread.event(Constants.EV_REQUEST_VIEW_PLAY_LIST);
        } else if (v == mTitleBarImage) {
            Intent intent = new Intent(mActivity, ViewSongsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_VIEW_SONGS);
        } else if (v == mSwitchMode) {
            PlayList playList = mPlayer.getPlayList();
            switch (playList.getMode()) {
                case PlayList.MODE_LOOP_LIST:
                    playList.setMode(PlayList.MODE_LOOP_SINGLE);
                    break;
                case PlayList.MODE_LOOP_SINGLE:
                    playList.setMode(PlayList.MODE_RANDOM);
                    break;
                case PlayList.MODE_RANDOM:
                    playList.setMode(PlayList.MODE_LOOP_LIST);
                    break;
            }
        } else if (v == mPlayPause) {
            PlayTask playTask = mPlayer.getPlayTask();
            if (playTask == null) {
                mPlayer.play();
            } else {
                switch (playTask.getTaskState()) {
                    case PlayTask.STATE_PLAYING:
                        playTask.pause();
                        break;
                    case PlayTask.STATE_PAUSED:
                        playTask.play();
                        break;
                    default:
                        // nothing
                        break;
                }
            }
        } else if (v == mPrevious) {
            mPlayer.previous();
        } else if (v == mNext) {
            mPlayer.next();
        } else if (v == mDiskContainer) {
            mViewSwapper.setDisplayedChild(1);
        } else if (v == mLrcView) {
            mViewSwapper.setDisplayedChild(0);
        } else if (v == mSettings) {
            Intent intent = new Intent(mActivity, SettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == mLrcView) {
            PlayTask playTask = mPlayer.getPlayTask();
            if (playTask == null) {
                return false;
            } else {
                Intent intent = new Intent(mActivity, LyricListActivity.class);
                intent.putExtra(LyricListActivity.FILE_NAME, playTask.getFileName());
                startActivity(intent);
                return true;
            }
        } else if (v == mDiskContainer) {
            PlayTask playTask = mPlayer.getPlayTask();
            if (playTask == null) {
                return false;
            } else {
                Intent intent = new Intent(mActivity, AlbumListActivity.class);
                intent.putExtra(AlbumListActivity.FILE_NAME, playTask.getFileName());
                startActivity(intent);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPlayProgress(float progress) {
        PlayTask playTask = mPlayer.getPlayTask();
        if (playTask != null) {
            playTask.seekTo((int) (playTask.getDuration() * progress / 100));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_VIEW_SONGS:
                if (resultCode == ViewSongsActivity.RESULT_OK) {
                    UIThread.event(Constants.EV_REQUEST_REFRESH_PLAY_LIST);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        mUITimer.stop();
        super.onDestroyView();
    }

}
