package site.iway.mymusic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import site.iway.androidhelpers.UIThread;

public class PlayList extends ArrayList<String> {

    public static final int MODE_LOOP_LIST = 0;
    public static final int MODE_LOOP_SINGLE = 1;
    public static final int MODE_RANDOM = 2;

    private int mPlayMode;

    private PlayList() {
        mPlayMode = Settings.getPlayerPlayMode();
    }

    public void setMode(int playMode) {
        if (playMode < MODE_LOOP_LIST || playMode > MODE_RANDOM) {
            playMode = MODE_LOOP_LIST;
        }
        if (mPlayMode != playMode) {
            mPlayMode = playMode;
            Settings.setPlayerPlayMode(mPlayMode);
            Settings.commit();
            UIThread.event(Constants.EV_PLAY_LIST_MODE_CHANGED, mPlayMode);
        }
    }

    public int getMode() {
        return mPlayMode;
    }

    public void replace(List<String> items) {
        if (items == null) {
            items = new ArrayList<>();
        }
        clear();
        addAll(items);
    }

    private Random mRandom = new Random(System.nanoTime());

    public String previous(String current) {
        if (isEmpty())
            return null;
        if (size() == 1)
            return get(0);
        switch (mPlayMode) {
            case MODE_LOOP_LIST:
            case MODE_LOOP_SINGLE:
                int currentIndex = indexOf(current);
                int previousIndex = currentIndex - 1;
                if (previousIndex <= -1) {
                    previousIndex = size() - 1;
                }
                return get(previousIndex);
            case MODE_RANDOM:
                return get(mRandom.nextInt(size()));
            default:
                return null;
        }
    }

    public String next(String current) {
        if (isEmpty())
            return null;
        if (size() == 1)
            return get(0);
        switch (mPlayMode) {
            case MODE_LOOP_LIST:
            case MODE_LOOP_SINGLE:
                int currentIndex = indexOf(current);
                int nextIndex = currentIndex + 1;
                if (nextIndex >= size()) {
                    nextIndex = 0;
                }
                return get(nextIndex);
            case MODE_RANDOM:
                return get(mRandom.nextInt(size()));
            default:
                return null;
        }
    }

    public String nextAuto(String current) {
        if (mPlayMode == MODE_LOOP_SINGLE && current != null) {
            return current;
        }
        return next(current);
    }

    private volatile static PlayList sInstance;

    public static PlayList getInstance() {
        if (sInstance == null) {
            synchronized (PlayList.class) {
                if (sInstance == null) {
                    sInstance = new PlayList();
                }
            }
        }
        return sInstance;
    }

}
