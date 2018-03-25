package site.iway.mymusic.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import site.iway.javahelpers.HttpFileDownloader;
import site.iway.javahelpers.TextRW;

/**
 * Created by iWay on 2017/12/28.
 */

public class LyricCache {

    private final Context mContext;
    private final Object mListAccessLock;
    private final List<Song> mAvailableSongs;
    private final List<String> mDownloadingList;

    private void cleanTempFiles() {
        File rootCacheDir = mContext.getCacheDir();
        File musicCacheDir = new File(rootCacheDir, Constants.DIR_NAME_LYRIC_CACHE);
        File[] musicFiles = musicCacheDir.listFiles();
        if (musicFiles != null) {
            for (File file : musicFiles) {
                String name = file.getName();
                if (name.endsWith(".lrc")) {
                    Song song = new Song(name);
                    mAvailableSongs.add(song);
                } else {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
            }
        }
    }

    public LyricCache(Context context) {
        mContext = context;
        mAvailableSongs = new ArrayList<>();
        mListAccessLock = new Object();
        mDownloadingList = new ArrayList<>();
        cleanTempFiles();
    }

    private String getLrcFileName(Song song) {
        return song.artist + " - " + song.name + ".lrc";
    }

    private File getLrcFile(Song song) {
        File rootCacheDir = mContext.getCacheDir();
        File lyricCacheDir = new File(rootCacheDir, Constants.DIR_NAME_LYRIC_CACHE);
        return new File(lyricCacheDir, getLrcFileName(song));
    }

    public boolean exists(Song song) {
        synchronized (mListAccessLock) {
            return mAvailableSongs.contains(song);
        }
    }

    public LyricManager get(Song song) {
        File lyricFile = getLrcFile(song);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(lyricFile);
            List<String> lines = TextRW.readAllLines(inputStream);
            return new LyricManager(song, lines);
        } catch (IOException e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // nothing
                }
            }
        }
    }

    public void download(final Song song, final String url) {
        synchronized (mListAccessLock) {
            if (!mDownloadingList.contains(url)) {
                mDownloadingList.add(url);
                String file = getLrcFile(song).getAbsolutePath();
                HttpFileDownloader httpFileDownloader = new HttpFileDownloader(url, file) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        synchronized (mListAccessLock) {
                            mAvailableSongs.add(song);
                            mDownloadingList.remove(url);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        super.onError(e);
                        synchronized (mListAccessLock) {
                            mDownloadingList.remove(url);
                        }
                    }
                };
                httpFileDownloader.start();
            }
        }
    }

    private static LyricCache sInstance;

    public static void initialize(Context context) {
        sInstance = new LyricCache(context);
    }

    public static LyricCache getInstance() {
        return sInstance;
    }

}
