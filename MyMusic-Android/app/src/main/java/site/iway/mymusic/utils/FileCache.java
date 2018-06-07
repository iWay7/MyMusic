package site.iway.mymusic.utils;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import site.iway.javahelpers.HttpFileDownloader;
import site.iway.javahelpers.StringHelper;

public class FileCache {

    public interface UrlGenerator {
        public String generate(String fileName);
    }

    private static final String EXTENSION = ".url";

    private final Context mContext;
    private final String mDirectory;
    private final Object mListAccessLock;
    private final List<String> mDownloadingList;

    private void cleanTempFiles() {
        File rootCacheDir = mContext.getCacheDir();
        File cacheDir = new File(rootCacheDir, mDirectory);
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (!fileName.endsWith(".url")) {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
            }
        }
    }

    public FileCache(Context context, String directory) {
        mContext = context;
        mDirectory = directory;
        mListAccessLock = new Object();
        mDownloadingList = new ArrayList<>();
        cleanTempFiles();
    }

    private String getFileName(String url) {
        return StringHelper.md5(url) + EXTENSION;
    }

    public boolean exists(String url) {
        File rootCacheDir = mContext.getCacheDir();
        File cacheDir = new File(rootCacheDir, mDirectory);
        String fileName = getFileName(url);
        File file = new File(cacheDir, fileName);
        return file.exists();
    }

    public boolean delete(String url) {
        File rootCacheDir = mContext.getCacheDir();
        File cacheDir = new File(rootCacheDir, mDirectory);
        String fileName = getFileName(url);
        File file = new File(cacheDir, fileName);
        return file.delete();
    }

    public boolean isDownloading(String url) {
        synchronized (mListAccessLock) {
            return mDownloadingList.contains(url);
        }
    }

    public String getFilePath(String url) {
        File rootCacheDir = mContext.getCacheDir();
        File cacheDir = new File(rootCacheDir, mDirectory);
        String fileName = getFileName(url);
        return new File(cacheDir, fileName).toString();
    }

    public void download(final String url) {
        synchronized (mListAccessLock) {
            if (!exists(url) && !isDownloading(url)) {
                mDownloadingList.add(url);
                String filePath = getFilePath(url);
                HttpFileDownloader httpFileDownloader = new HttpFileDownloader(url, filePath) {
                    @Override
                    public void onFinally() {
                        super.onFinally();
                        synchronized (mListAccessLock) {
                            mDownloadingList.remove(url);
                        }
                    }
                };
                httpFileDownloader.setOverwriteIfExisted(true);
                httpFileDownloader.start();
            }
        }
    }

    private static FileCache mMusicCache;
    private static FileCache mLyricCache;

    public static void initialize(Context context) {
        mMusicCache = new FileCache(context, Constants.DIR_NAME_MUSIC_CACHE);
        mLyricCache = new FileCache(context, Constants.DIR_NAME_LYRIC_CACHE);
    }

    public static FileCache getMusic() {
        return mMusicCache;
    }

    public static FileCache getLyric() {
        return mLyricCache;
    }

}
