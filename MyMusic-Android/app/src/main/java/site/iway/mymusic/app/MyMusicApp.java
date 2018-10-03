package site.iway.mymusic.app;

import android.app.Application;

import java.io.File;

import site.iway.androidhelpers.ActionTimer;
import site.iway.androidhelpers.BitmapCache;
import site.iway.androidhelpers.RPCEngine;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UnitHelper;
import site.iway.javahelpers.HanziPinyinHelper;
import site.iway.javahelpers.ObjectStore;
import site.iway.mymusic.BuildConfig;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.FileCache;
import site.iway.mymusic.utils.Player;
import site.iway.mymusic.utils.Settings;

/**
 * Created by iWay on 2017/12/25.
 */
public class MyMusicApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();

        Constants.FILES_DIRECTORY = filesDir.getAbsolutePath();
        Constants.CACHE_DIRECTORY = cacheDir.getAbsolutePath();

        UnitHelper.initialize(this);
        BitmapCache.setLogEnabled(BuildConfig.DEBUG);
        BitmapCache.setContext(this);
        BitmapCache.setLoaderCount(4);
        BitmapCache.setMaxRAMUsageForSingleBitmap(16 * 1024 * 1024);
        BitmapCache.setDownloadDirectory(Constants.DIR_NAME_IMAGE_CACHE);
        BitmapCache.initialize();
        ActionTimer.initialize(this);
        ObjectStore.initialize(filesDir + "/" + Constants.DIR_NAME_OBJECT_STORE, true, "sPBocrqJKgBiSagg4vfhshcx");
        UIThread.initialize();
        RPCEngine.initialize(2);
        HanziPinyinHelper.initialize();

        Settings.initialize(filesDir + "/" + Constants.FILE_NAME_SETTINGS, "sPBocrqJKgBiSagg4vfhshcx");
        FileCache.initialize(this);
        Player.initialize(this);
    }

}
