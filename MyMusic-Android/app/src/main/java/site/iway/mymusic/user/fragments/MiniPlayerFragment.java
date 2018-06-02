package site.iway.mymusic.user.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import site.iway.androidhelpers.BitmapView;
import site.iway.androidhelpers.CircleProgressBar;
import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.UITimer;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.mymusic.models.GetSongInfoReq;
import site.iway.mymusic.net.mymusic.models.GetSongInfoRes;
import site.iway.mymusic.net.mymusic.models.common.SongInfo;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;
import site.iway.mymusic.utils.Song;

/**
 * Created by iWay on 2017/12/26.
 */

public class MiniPlayerFragment extends BaseFragment implements RPCCallback, OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mini_player, container, false);
    }

    private Player mPlayer = Player.getInstance();

    private BitmapView mSongCover;
    private ExtendedTextView mSongName;
    private ExtendedTextView mSongArtist;
    private ExtendedImageView mPrevious;
    private ViewSwapper mPlayPause;
    private CircleProgressBar mProgress;
    private ExtendedImageView mNext;

    private static final int INDEX_PLAY = 0;
    private static final int INDEX_PAUSE = 1;

    private void refreshViews() {
        PlayTask playTask = mPlayer.getPlayTask();
        if (playTask != null) {
            String musicFileName = playTask.getMusicFileName();
            Song song = new Song(musicFileName);
            loadImage(song);
            mSongName.setText(song.name);
            mSongArtist.setText(song.artist);
        }
    }

    private void setViews() {
        mSongCover = (BitmapView) mRootView.findViewById(R.id.songCover);
        mSongName = (ExtendedTextView) mRootView.findViewById(R.id.songName);
        mSongArtist = (ExtendedTextView) mRootView.findViewById(R.id.songArtist);
        mPrevious = (ExtendedImageView) mRootView.findViewById(R.id.previous);
        mPlayPause = (ViewSwapper) mRootView.findViewById(R.id.playPause);
        mProgress = (CircleProgressBar) mRootView.findViewById(R.id.progress);
        mNext = (ExtendedImageView) mRootView.findViewById(R.id.next);
        mPrevious.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mUITimer.start(true);
        refreshViews();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    public void setVisibility(int visibility) {
        mRootView.setVisibility(visibility);
    }

    public void setAlpha(float alpha) {
        mRootView.setAlpha(alpha);
    }

    private UITimer mUITimer = new UITimer(200) {
        @Override
        public void doOnUIThread() {
            PlayTask playTask = mPlayer.getPlayTask();
            if (playTask == null) {
                mPlayPause.setDisplayedChild(INDEX_PLAY);
                mProgress.setProgress(0, false);
            } else {
                switch (playTask.getTaskState()) {
                    case PlayTask.STATE_PLAYING:
                        mPlayPause.setDisplayedChild(INDEX_PAUSE);
                        break;
                    default:
                        mPlayPause.setDisplayedChild(INDEX_PLAY);
                        break;
                }
                switch (playTask.getTaskState()) {
                    case PlayTask.STATE_READY:
                    case PlayTask.STATE_TASK_START:
                    case PlayTask.STATE_DOWNLOADING:
                    case PlayTask.STATE_ERROR:
                    case PlayTask.STATE_TASK_CANCELED:
                        mProgress.setProgress(0, false);
                        break;
                    case PlayTask.STATE_PREPARED:
                    case PlayTask.STATE_PLAYING:
                    case PlayTask.STATE_PAUSED:
                    case PlayTask.STATE_COMPLETED:
                        float duration = playTask.getDuration();
                        float position = playTask.getPosition();
                        float percent = position * 100 / duration;
                        mProgress.setProgress(percent, false);
                        break;
                }
            }
        }
    };

    private GetSongInfoReq mLastFetchSongInfo;

    private void loadImage(Song song) {
        if (mLastFetchSongInfo != null) {
            mLastFetchSongInfo.cancel();
            mLastFetchSongInfo = null;
        }
        mLastFetchSongInfo = new GetSongInfoReq();
        mLastFetchSongInfo.query = song.artist + " " + song.name;
        mLastFetchSongInfo.start(this);
    }

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mLastFetchSongInfo) {
            GetSongInfoRes getSongInfoRes = (GetSongInfoRes) req.response;
            String imageLink = null;
            if (getSongInfoRes.list != null) {
                for (SongInfo songInfo : getSongInfoRes.list) {
                    if (!TextUtils.isEmpty(songInfo.imgLink)) {
                        imageLink = songInfo.imgLink;
                        break;
                    }
                }
            }
            mSongCover.loadFromURLSource(imageLink);
        }
    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        if (req == mLastFetchSongInfo) {
            // nothing
        }
    }

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_PLAYER_TASK_CHANGED:
                refreshViews();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mPlayPause) {
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
        }
    }

    @Override
    public void onDestroyView() {
        mUITimer.stop();
        super.onDestroyView();
    }

}
