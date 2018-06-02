package site.iway.mymusic.utils;

import java.io.FileInputStream;
import java.util.List;

import site.iway.javahelpers.TextRW;

public class LyricTask extends Thread {

    public interface LyricStateListener {
        public void onLyricStateChanged(LyricTask lyricTask);
    }

    public static final int STATE_READY = 0;
    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_SUCCESS = 3;

    private final String mUrl;
    private final LyricStateListener mListener;
    private volatile int mState;

    public LyricTask(String url, LyricStateListener listener) {
        mUrl = url;
        mListener = listener;
        updateTaskState(STATE_READY);
    }

    public int getTaskState() {
        return mState;
    }

    private void updateTaskState(int state) {
        mState = state;
        mListener.onLyricStateChanged(this);
    }

    private LyricManager mLyricManager;

    public LyricManager getLyricManager() {
        return mLyricManager;
    }

    @Override
    public void run() {
        updateTaskState(STATE_DOWNLOADING);
        FileCache lyricCache = FileCache.getLyric();
        if (lyricCache.exists(mUrl)) {
            String filePath = lyricCache.getFilePath(mUrl);
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
                List<String> lines = TextRW.readAllLines(inputStream);
                mLyricManager = new LyricManager(lines);
                updateTaskState(STATE_SUCCESS);
            } catch (Exception e) {
                updateTaskState(STATE_ERROR);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        // nothing
                    }
                }
            }
        } else {
            lyricCache.download(mUrl);
            do {
                try {
                    Thread.sleep(333);
                } catch (Exception e) {
                    break;
                }
            }
            while (lyricCache.isDownloading(mUrl));
            if (lyricCache.exists(mUrl)) {
                String filePath = lyricCache.getFilePath(mUrl);
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    List<String> lines = TextRW.readAllLines(inputStream);
                    mLyricManager = new LyricManager(lines);
                    updateTaskState(STATE_SUCCESS);
                } catch (Exception e) {
                    updateTaskState(STATE_ERROR);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            // nothing
                        }
                    }
                }
            } else {
                updateTaskState(STATE_ERROR);
            }
        }
    }

}
