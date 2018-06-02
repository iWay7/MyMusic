package site.iway.mymusic.user.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import site.iway.androidhelpers.UITimer;
import site.iway.mymusic.utils.LyricManager;
import site.iway.mymusic.utils.LyricManager.LyricLine;
import site.iway.mymusic.utils.LyricTask;
import site.iway.mymusic.utils.LyricTask.LyricStateListener;
import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;

public class LineLRCView extends TextView implements LyricStateListener {

    public LineLRCView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LineLRCView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineLRCView(Context context) {
        super(context);
    }

    private volatile LyricTask mLyricTask;
    private volatile LyricManager mLyricManager;

    @Override
    public void onLyricStateChanged(LyricTask lyricTask) {
        if (mLyricTask == lyricTask) {
            switch (lyricTask.getTaskState()) {
                case LyricTask.STATE_SUCCESS:
                    mLyricManager = lyricTask.getLyricManager();
                    break;
            }
        }
    }

    public void load(String url) {
        mLyricManager = null;
        mLyricTask = null;
        setText(null);
        if (url != null) {
            mLyricTask = new LyricTask(url, this);
            mLyricTask.start();
        }
    }

    private UITimer mUITimer = new UITimer(200) {

        private LyricLine mLastLyricLine;

        @Override
        public void doOnUIThread() {
            if (mLyricManager == null) {
                setText(null);
            } else {
                Player player = Player.getInstance();
                PlayTask playTask = player.getPlayTask();
                if (playTask == null) {
                    setText(null);
                } else {
                    int position = playTask.getPosition();
                    LyricLine currentLine = mLyricManager.getCurrentLine(position);
                    if (currentLine != mLastLyricLine) {
                        if (currentLine == null) {
                            setText(null);
                        } else {
                            String text = currentLine.combineLineTexts();
                            setText(text);
                        }
                    }
                    mLastLyricLine = currentLine;
                }
            }
        }

    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mUITimer.start(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        mUITimer.stop();
        super.onDetachedFromWindow();
    }
}
