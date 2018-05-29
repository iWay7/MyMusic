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
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.mymusic.models.GetSongInfoReq;
import site.iway.mymusic.net.mymusic.models.GetSongInfoRes;
import site.iway.mymusic.net.mymusic.models.internal.SongInfo;
import site.iway.mymusic.user.activities.SettingsActivity;
import site.iway.mymusic.user.activities.ViewSongsActivity;
import site.iway.mymusic.user.views.LRCView;
import site.iway.mymusic.user.views.PlayProgressView;
import site.iway.mymusic.user.views.PlayProgressView.OnRequestPlayProgressListener;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.LyricCache;
import site.iway.mymusic.utils.LyricManager;
import site.iway.mymusic.utils.LyricManager.LyricLine;
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
    private ExtendedTextView mLrc;
    private ExtendedImageView mDiskHandle;
    private LRCView mLrcView;
    private ExtendedTextView mPosition;
    private PlayProgressView mPlayProgress;
    private ExtendedTextView mDuration;
    private ViewSwapper mSwitchMode;
    private ExtendedImageView mPrevious;
    private ExtendedImageView mPlayStop;
    private ExtendedImageView mNext;
    private ExtendedImageView mSettings;

    private void refreshPlayMode() {
        int playMode = mPlayer.getPlayMode();
        switch (playMode) {
            case Player.MODE_LOOP_LIST:
                mSwitchMode.setDisplayedChild(0);
                break;
            case Player.MODE_LOOP_SINGLE:
                mSwitchMode.setDisplayedChild(1);
                break;
            case Player.MODE_RANDOM:
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
        if (mPlayer.isPlayingFile()) {
            Song playingSong = mPlayer.getPlayingSong();
            loadImage(playingSong);
            mTitleBarText.setText(playingSong.name);
            mArtist.setText(playingSong.artist);
            if (mPlayer.isPlaying()) {
                mPlayStop.setImageResource(R.drawable.icon_pause_white);
            } else {
                mPlayStop.setImageResource(R.drawable.icon_play_white);
            }
            mPosition.setText(getTime(mPlayer.getPosition()));
            mDuration.setText(getTime(mPlayer.getDuration()));
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
        mLrc = (ExtendedTextView) mRootView.findViewById(R.id.lrc);
        mDiskHandle = (ExtendedImageView) mRootView.findViewById(R.id.diskHandle);
        mLrcView = (LRCView) mRootView.findViewById(R.id.lrcView);
        mPosition = (ExtendedTextView) mRootView.findViewById(R.id.position);
        mPlayProgress = (PlayProgressView) mRootView.findViewById(R.id.playProgress);
        mDuration = (ExtendedTextView) mRootView.findViewById(R.id.duration);
        mSwitchMode = (ViewSwapper) mRootView.findViewById(R.id.switchMode);
        mPrevious = (ExtendedImageView) mRootView.findViewById(R.id.previous);
        mPlayStop = (ExtendedImageView) mRootView.findViewById(R.id.playStop);
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
        mPlayStop.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mSettings.setOnClickListener(this);
        mDiskContainer.setOnClickListener(this);
        mLrcView.setOnClickListener(this);
        mLrcView.setOnLongClickListener(this);

        refreshViews();
        refreshPlayMode();

        mUITimer.start(false);
    }

    private GetSongInfoReq mLastFetchSongInfo;

    private void loadImage(Song song) {
        if (mLastFetchSongInfo != null) {
            mLastFetchSongInfo.cancel();
            mLastFetchSongInfo = null;
        }
        mLastFetchSongInfo = new GetSongInfoReq();
        mLastFetchSongInfo.query = song.artist + " " + song.name;
        mLastFetchSongInfo.tag = song;
        mLastFetchSongInfo.start(this);

    }

    private BitmapFilter mBitmapFilter = new BitmapFilter() {
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

    private LyricManager mLyricManager;

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mLastFetchSongInfo) {
            GetSongInfoReq getSongInfoReq = (GetSongInfoReq) req;
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
            mSongArt.loadFromURLSource(imageLink == null ? null : imageLink.replace("90", "512"));
            if (!TextUtils.isEmpty(lrcLink)) {
                Song song = (Song) getSongInfoReq.tag;
                LyricCache lyricCache = LyricCache.getInstance();
                if (!lyricCache.exists(song)) {
                    lyricCache.download(song, lrcLink);
                }
            }
        }
    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        if (req == mLastFetchSongInfo) {
            // nothing
        }
    }

    private UITimer mUITimer = new UITimer(16) {

        private int mTicks = 0;

        private void setDisk() {
            boolean playing = mPlayer.isPlaying();
            if (playing) {
                float current = mDiskContainer.getRotation();
                current += 0.5;
                mDiskContainer.setRotation(current);
            }
        }

        private float mPausedDegrees = -30;
        private float mPlayingDegrees = 0;

        private void setDiskHandle() {
            boolean playing = mPlayer.isPlaying();
            if (playing) {
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
                if (mPlayer.isPlayingFile()) {
                    float duration = mPlayer.getDuration();
                    float position = mPlayer.getPosition();
                    float percent = position * 100 / duration;
                    mPlayProgress.setProgress(percent);
                    if (mTicks % 40 == 0) {
                        mPosition.setText(getTime(mPlayer.getPosition()));
                    }
                } else {
                    mPlayProgress.setProgress(0);
                    mPosition.setText(getTime(0));
                    mDuration.setText(getTime(0));
                }
            }
        }

        private long mLastLyricTime;

        private void setLyric() {
            if (mTicks % 8 == 0) {
                if (mPlayer.isPlayingFile()) {
                    Song playingSong = mPlayer.getPlayingSong();
                    if (mLyricManager == null || !mLyricManager.forSong(playingSong)) {
                        LyricCache lyricCache = LyricCache.getInstance();
                        if (lyricCache.exists(playingSong)) {
                            mLyricManager = lyricCache.get(playingSong);
                        } else {
                            mLyricManager = null;
                        }
                    }
                    mLrcView.setLyricManager(mLyricManager);
                    if (mLyricManager != null) {
                        int position = mPlayer.getPosition();
                        LyricLine lyricLine = mLyricManager.getCurrentLine(position);
                        mLrcView.setCurrentLine(lyricLine);
                        if (lyricLine != null) {
                            long lyricTime = lyricLine.millis;
                            if (mLastLyricTime != lyricTime) {
                                String newText = lyricLine.combineLineTexts();
                                mLrc.setText(newText);
                            }
                            mLastLyricTime = lyricTime;
                        } else {
                            mLrc.setText(null);
                        }
                    } else {
                        mLrc.setText(null);
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
            setLyric();
        }
    };

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_PLAYER_START_PLAY:
                refreshViews();
                break;
            case Constants.EV_PLAYER_PAUSED_PLAY:
            case Constants.EV_PLAYER_FINISHED_PLAY:
            case Constants.EV_PLAYER_PLAY_ERROR:
                mPlayStop.setImageResource(R.drawable.icon_play_white);
                break;
            case Constants.EV_PLAYER_MODE_CHANGED:
                refreshPlayMode();
                break;
        }
    }

    private static final int REQUEST_CODE_VIEW_SONGS = 0;

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            UIThread.event(Constants.EV_PLAY_LIST_VIEW);
        } else if (v == mTitleBarImage) {
            Intent intent = new Intent(mActivity, ViewSongsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_VIEW_SONGS);
        } else if (v == mSwitchMode) {
            switch (mPlayer.getPlayMode()) {
                case Player.MODE_LOOP_LIST:
                    mPlayer.setPlayMode(Player.MODE_LOOP_SINGLE);
                    break;
                case Player.MODE_LOOP_SINGLE:
                    mPlayer.setPlayMode(Player.MODE_RANDOM);
                    break;
                case Player.MODE_RANDOM:
                    mPlayer.setPlayMode(Player.MODE_LOOP_LIST);
                    break;
            }
        } else if (v == mPlayStop) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.play();
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
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPlayProgress(float progress) {
        mPlayer.seekTo((int) (mPlayer.getDuration() * progress / 100));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_VIEW_SONGS:
                if (resultCode == ViewSongsActivity.RESULT_OK) {
                    UIThread.event(Constants.EV_PLAY_LIST_REFRESH);
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
