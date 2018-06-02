package site.iway.mymusic.utils;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class PlayTask extends Thread implements OnCompletionListener, OnErrorListener {

    public interface PlayStateListener {
        public void onPlayStateChanged(PlayTask playTask);
    }

    public static final int STATE_READY = 0;
    public static final int STATE_TASK_START = 1;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_PREPARED = 3;
    public static final int STATE_PLAYING = 4;
    public static final int STATE_PAUSED = 5;
    public static final int STATE_COMPLETED = 6;
    public static final int STATE_ERROR = 7;
    public static final int STATE_TASK_CANCELED = 9;

    private final MediaPlayer mMediaPlayer;
    private final String mMusicFileName;
    private final PlayStateListener mListener;
    private volatile int mState;
    private volatile boolean mIsCanceled;

    public PlayTask(String musicFileName, PlayStateListener listener) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMusicFileName = musicFileName;
        mListener = listener;
        updateTaskState(STATE_READY);
        mIsCanceled = false;
    }

    public String getMusicFileName() {
        return mMusicFileName;
    }

    public int getTaskState() {
        return mState;
    }

    private void updateTaskState(int state) {
        mState = state;
        mListener.onPlayStateChanged(this);
    }

    @Override

    public void onCompletion(MediaPlayer mp) {
        updateTaskState(STATE_COMPLETED);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        updateTaskState(STATE_ERROR);
        return true;
    }

    @Override
    public void run() {
        updateTaskState(STATE_TASK_START);
        updateTaskState(STATE_DOWNLOADING);
        MusicCache musicCache = MusicCache.getInstance();
        if (musicCache.exists(mMusicFileName)) {
            String musicFilePath = musicCache.get(mMusicFileName);
            try {
                mMediaPlayer.setDataSource(musicFilePath);
                mMediaPlayer.prepare();
                updateTaskState(STATE_PREPARED);
            } catch (Exception e) {
                updateTaskState(STATE_ERROR);
            }
        } else {
            musicCache.download(mMusicFileName);
            do {
                try {
                    Thread.sleep(333);
                } catch (Exception e) {
                    break;
                }
            }
            while (musicCache.isDownloading(mMusicFileName));
            if (musicCache.exists(mMusicFileName)) {
                String musicFilePath = musicCache.get(mMusicFileName);
                try {
                    mMediaPlayer.setDataSource(musicFilePath);
                    mMediaPlayer.prepare();
                    updateTaskState(STATE_PREPARED);
                } catch (Exception e) {
                    updateTaskState(STATE_ERROR);
                }
            } else {
                updateTaskState(STATE_ERROR);
            }
        }
        if (mState == STATE_PREPARED) {
            if (!mIsCanceled) {
                mMediaPlayer.start();
                updateTaskState(STATE_PLAYING);
            }
        }
    }

    public void pause() {
        if (mState == STATE_PLAYING) {
            mMediaPlayer.pause();
            updateTaskState(STATE_PAUSED);
        }
    }

    public void play() {
        if (mState == STATE_PAUSED) {
            mMediaPlayer.start();
            updateTaskState(STATE_PLAYING);
        }
    }

    public void seekTo(int msec) {
        if (mState == STATE_PREPARED ||
                mState == STATE_PAUSED ||
                mState == STATE_PLAYING ||
                mState == STATE_COMPLETED) {
            mMediaPlayer.seekTo(msec);
        }
    }

    public int getDuration() {
        if (mState == STATE_PREPARED ||
                mState == STATE_PAUSED ||
                mState == STATE_PLAYING ||
                mState == STATE_COMPLETED) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getPosition() {
        if (mState == STATE_PREPARED ||
                mState == STATE_PAUSED ||
                mState == STATE_PLAYING ||
                mState == STATE_COMPLETED) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void cancel() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }
        mIsCanceled = true;
        updateTaskState(STATE_TASK_CANCELED);
    }

}
