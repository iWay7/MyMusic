package site.iway.mymusic.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import site.iway.helpers.EventPoster;

/**
 * Created by iWay on 2017/12/27.
 */

public class Player implements OnCompletionListener, OnErrorListener {

    public static final int MODE_LOOP_LIST = 0;
    public static final int MODE_LOOP_SINGLE = 1;
    public static final int MODE_RANDOM = 2;

    private Context mContext;
    private MediaPlayer mMediaPlayer;

    private List<String> mPlayList;
    private int mPlayMode;
    private String mPlayingFile;

    public Player(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mPlayList = new ArrayList<>();
        mPlayMode = Settings.getPlayerPlayMode();
    }

    public void setPlayList(List<String> playList) {
        mPlayList = playList;
        if (mPlayList == null) {
            mPlayList = new ArrayList<>();
        }
        EventPoster.post(Constants.EV_PLAYER_LIST_UPDATED, mPlayList);
    }

    public void addToPlayList(String item) {
        mPlayList.add(item);
        EventPoster.post(Constants.EV_PLAYER_LIST_UPDATED, mPlayList);
    }

    public void removePlayListItems(List<String> items) {
        mPlayList.removeAll(items);
    }

    public boolean hasList() {
        return mPlayList != null && !mPlayList.isEmpty();
    }

    public String fetchListItem(boolean previous) {
        switch (mPlayMode) {
            case MODE_LOOP_LIST:
                if (previous) {
                    int nextIndex = mPlayList.indexOf(mPlayingFile) - 1;
                    if (nextIndex < 0) {
                        nextIndex = mPlayList.size() - 1;
                    }
                    return mPlayList.get(nextIndex);
                } else {
                    int nextIndex = mPlayList.indexOf(mPlayingFile) + 1;
                    if (nextIndex >= mPlayList.size()) {
                        nextIndex = 0;
                    }
                    return mPlayList.get(nextIndex);
                }
            case MODE_LOOP_SINGLE:
                if (isPlayingFile())
                    return mPlayingFile;
                int currIndex = mPlayList.indexOf(mPlayingFile);
                if (currIndex < 0) {
                    currIndex = 0;
                }
                return mPlayList.get(currIndex);
            case MODE_RANDOM:
                int random = new Random(System.nanoTime()).nextInt(mPlayList.size());
                return mPlayList.get(random);
            default:
                return null;
        }
    }

    public void setPlayMode(int playMode) {
        if (playMode < MODE_LOOP_LIST || playMode > MODE_RANDOM) {
            playMode = MODE_LOOP_LIST;
        }
        if (mPlayMode != playMode) {
            mPlayMode = playMode;
            Settings.setPlayerPlayMode(mPlayMode);
            EventPoster.post(Constants.EV_PLAYER_MODE_CHANGED, mPlayMode);
        }
    }

    public int getPlayMode() {
        return mPlayMode;
    }

    public boolean isPlayingFile() {
        return !TextUtils.isEmpty(mPlayingFile);
    }

    public String getPlayingFile() {
        return mPlayingFile;
    }

    public Song getPlayingSong() {
        return new Song(mPlayingFile);
    }

    public boolean playFile(String musicFileName) {
        try {
            if (isPlayingFile() && mPlayingFile.equals(musicFileName)) {
                mMediaPlayer.start();
            } else {
                if (isPlayingFile()) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer.reset();
                }
                mPlayingFile = musicFileName;
                MusicCache musicCache = MusicCache.getInstance();
                if (musicCache.exists(musicFileName)) {
                    mMediaPlayer.setDataSource(musicCache.get(musicFileName));
                } else {
                    mMediaPlayer.setDataSource(musicCache.getUrl(musicFileName));
                    musicCache.download(musicFileName);
                }
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
            EventPoster.post(Constants.EV_PLAYER_START_PLAY, mPlayingFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean next() {
        if (hasList()) {
            String musicFileName = fetchListItem(false);
            return playFile(musicFileName);
        } else {
            return false;
        }
    }

    public boolean previous() {
        if (hasList()) {
            String musicFileName = fetchListItem(true);
            return playFile(musicFileName);
        } else {
            return false;
        }
    }

    public boolean play() {
        try {
            if (isPlayingFile()) {
                mMediaPlayer.start();
                EventPoster.post(Constants.EV_PLAYER_START_PLAY, mPlayingFile);
                return true;
            } else {
                return next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPlaying() {
        return isPlayingFile() && mMediaPlayer.isPlaying();
    }

    public boolean pause() {
        try {
            mMediaPlayer.pause();
            EventPoster.post(Constants.EV_PLAYER_PAUSED_PLAY, mPlayingFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPaused() {
        return isPlayingFile() && !mMediaPlayer.isPlaying();
    }

    public int getDuration() {
        if (isPlayingFile()) {
            int duration = mMediaPlayer.getDuration();
            if (duration < 0)
                duration = 0;
            return duration;
        }
        return 0;
    }

    public int getPosition() {
        if (isPlayingFile()) {
            int position = mMediaPlayer.getCurrentPosition();
            if (position < 0)
                position = 0;
            return position;
        }
        return 0;
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        EventPoster.post(Constants.EV_PLAYER_FINISHED_PLAY, mPlayingFile);
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        EventPoster.post(Constants.EV_PLAYER_PLAY_ERROR, mPlayingFile);
        return true;
    }

    private static Player sInstance;

    public static void initialize(Context context) {
        sInstance = new Player(context);
    }

    public static Player getInstance() {
        return sInstance;
    }

}
