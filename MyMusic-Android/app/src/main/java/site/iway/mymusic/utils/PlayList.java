package site.iway.mymusic.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import site.iway.androidhelpers.UIThread;
import site.iway.javahelpers.HanziPinyinHelper;

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

    public void resort() {
        Collections.sort(this, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                switch (Settings.getPlayListSortType()) {
                    case Settings.SORT_BY_ARTIST_NAME:
                        Song o1s = new Song(o1);
                        Song o2s = new Song(o2);
                        String pinyin1 = HanziPinyinHelper.getPinyin(o1s.artist + o1s.name);
                        String pinyin2 = HanziPinyinHelper.getPinyin(o2s.artist + o2s.name);
                        return pinyin1.compareTo(pinyin2);
                    case Settings.SORT_BY_SONG_NAME:
                        o1s = new Song(o1);
                        o2s = new Song(o2);
                        pinyin1 = HanziPinyinHelper.getPinyin(o1s.name + o1s.artist);
                        pinyin2 = HanziPinyinHelper.getPinyin(o2s.name + o2s.artist);
                        return pinyin1.compareTo(pinyin2);
                }
                return 0;
            }
        });
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
