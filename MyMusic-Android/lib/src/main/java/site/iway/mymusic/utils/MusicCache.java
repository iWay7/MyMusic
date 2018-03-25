package site.iway.mymusic.utils;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import site.iway.javahelpers.HttpFileDownloader;
import site.iway.javahelpers.URLCodec;

/**
 * Created by iWay on 2017/12/28.
 */

public class MusicCache {

    private final Context mContext;
    private final Object mListAccessLock;
    private final List<String> mDownloadingList;

    private void cleanTempFiles() {
        File rootCacheDir = mContext.getCacheDir();
        File musicCacheDir = new File(rootCacheDir, Constants.DIR_NAME_MUSIC_CACHE);
        File[] musicFiles = musicCacheDir.listFiles();
        if (musicFiles != null) {
            for (File file : musicFiles) {
                String name = file.getName();
                if (!name.endsWith(".mp3")) {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
            }
        }
    }

    public MusicCache(Context context) {
        mContext = context;
        mListAccessLock = new Object();
        mDownloadingList = new ArrayList<>();
        cleanTempFiles();
    }

    public boolean exists(String musicFileName) {
        File rootCacheDir = mContext.getCacheDir();
        File musicCacheDir = new File(rootCacheDir, Constants.DIR_NAME_MUSIC_CACHE);
        File musicFile = new File(musicCacheDir, musicFileName);
        return musicFile.exists();
    }

    public boolean isDownloading(String musicFileName) {
        synchronized (mListAccessLock) {
            return mDownloadingList.contains(musicFileName);
        }
    }

    public String get(String musicFileName) {
        File rootCacheDir = mContext.getCacheDir();
        File musicCacheDir = new File(rootCacheDir, Constants.DIR_NAME_MUSIC_CACHE);
        return new File(musicCacheDir, musicFileName).toString();
    }

    public String getUrl(String musicFileName) {
        return "http://home.iway.site:8888/mm/GetSong?fileName=" + URLCodec.encode(musicFileName);
    }

    public void download(final String musicFileName) {
        synchronized (mListAccessLock) {
            if (!mDownloadingList.contains(musicFileName)) {
                String url = getUrl(musicFileName);
                String file = get(musicFileName);
                HttpFileDownloader httpFileDownloader = new HttpFileDownloader(url, file) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        synchronized (mListAccessLock) {
                            mDownloadingList.remove(musicFileName);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        super.onError(e);
                        synchronized (mListAccessLock) {
                            mDownloadingList.remove(musicFileName);
                        }
                    }
                };
                httpFileDownloader.start();
            }
        }
    }

    private static MusicCache sInstance;

    public static void initialize(Context context) {
        sInstance = new MusicCache(context);
    }

    public static MusicCache getInstance() {
        return sInstance;
    }

}
