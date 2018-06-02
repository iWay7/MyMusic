package site.iway.mymusic.utils;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import site.iway.javahelpers.URLCodec;

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
    private final String mFileName;
    private final PlayStateListener mListener;
    private volatile int mState;
    private volatile boolean mIsCanceled;

    public PlayTask(String fileName, PlayStateListener listener) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mFileName = fileName;
        mListener = listener;
        updateTaskState(STATE_READY);
        mIsCanceled = false;
    }

    public String getFileName() {
        return mFileName;
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
        String url = Constants.MUSIC_URL_BASE + URLCodec.encode(mFileName);
        FileCache musicCache = FileCache.getMusic();
        if (musicCache.exists(url)) {
            String filePath = musicCache.getFilePath(url);
            try {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                updateTaskState(STATE_PREPARED);
            } catch (Exception e) {
                updateTaskState(STATE_ERROR);
            }
        } else {
            musicCache.download(url);
            do {
                try {
                    Thread.sleep(333);
                } catch (Exception e) {
                    break;
                }
            }
            while (musicCache.isDownloading(url));
            if (musicCache.exists(url)) {
                String filePath = musicCache.getFilePath(url);
                try {
                    mMediaPlayer.setDataSource(filePath);
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
