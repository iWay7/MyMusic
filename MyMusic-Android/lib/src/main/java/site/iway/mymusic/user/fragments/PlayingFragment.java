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

import site.iway.helpers.BitmapFilter;
import site.iway.helpers.BitmapHelper;
import site.iway.helpers.BitmapView;
import site.iway.helpers.EventPoster;
import site.iway.helpers.ExtendedFrameLayout;
import site.iway.helpers.ExtendedImageView;
import site.iway.helpers.ExtendedTextView;
import site.iway.helpers.ExtendedView;
import site.iway.helpers.RPCInfo;
import site.iway.helpers.RPCListener;
import site.iway.helpers.Scale;
import site.iway.helpers.UITimer;
import site.iway.helpers.ViewSwapper;
import site.iway.helpers.WindowHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCClient;
import site.iway.mymusic.net.data.SongInfo;
import site.iway.mymusic.net.req.GetSongInfoReq;
import site.iway.mymusic.net.res.GetSongInfoRes;
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

public class PlayingFragment extends BaseFragment implements RPCListener, OnClickListener, OnRequestPlayProgressListener, OnLongClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playing, container, false);
    }

    private Player mPlayer = Player.getInstance();

    private BitmapView mBackground;
    private ExtendedView mTitlePad;
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
    private ExtendedImageView mRemove;

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
        mTitlePad = (ExtendedView) mRootView.findViewById(R.id.titlePad);
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
        mRemove = (ExtendedImageView) mRootView.findViewById(R.id.remove);

        if (WindowHelper.makeTranslucent(mActivity, true, false)) {
            mTitlePad.setVisibility(View.VISIBLE);
            int statusBarHeight = WindowHelper.getStatusBarHeight(mActivity);
            LayoutParams layoutParams = mTitlePad.getLayoutParams();
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
        mRemove.setOnClickListener(this);
        mDiskContainer.setOnClickListener(this);
        mLrcView.setOnClickListener(this);
        mLrcView.setOnLongClickListener(this);

        refreshViews();
        refreshPlayMode();

        mUITimer.start(false);
    }

    private RPCInfo mLastFetchSongInfo;

    private void loadImage(Song song) {
        if (mLastFetchSongInfo != null) {
            mLastFetchSongInfo.requestCancel();
            mLastFetchSongInfo = null;
        }
        GetSongInfoReq getSongInfoReq = new GetSongInfoReq();
        getSongInfoReq.query = song.artist + " " + song.name;
        mLastFetchSongInfo = RPCClient.doRequest(getSongInfoReq, this);
        mLastFetchSongInfo.setTag(song);
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
    public void onRequestOK(RPCInfo rpcInfo, Object data) {
        if (rpcInfo == mLastFetchSongInfo) {
            GetSongInfoReq getSongInfoReq = (GetSongInfoReq) rpcInfo.getRequest();
            GetSongInfoRes getSongInfoRes = (GetSongInfoRes) data;
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
            mSongArt.loadFromURLSource(imageLink);
            if (!TextUtils.isEmpty(lrcLink)) {
                Song song = (Song) rpcInfo.getTag();
                LyricCache lyricCache = LyricCache.getInstance();
                if (!lyricCache.exists(song)) {
                    lyricCache.download(song, lrcLink);
                }
            }
        }
    }

    @Override
    public void onRequestER(RPCInfo rpcInfo, Exception e) {
        if (rpcInfo == mLastFetchSongInfo) {
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
    public void onEvent(int event, Object data) {
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
            EventPoster.post(Constants.EV_PLAY_LIST_VIEW);
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
                    EventPoster.post(Constants.EV_PLAY_LIST_REFRESH);
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
