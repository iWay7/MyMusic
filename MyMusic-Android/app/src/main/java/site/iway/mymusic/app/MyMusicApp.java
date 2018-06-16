package site.iway.mymusic.app;

import android.app.Application;

import java.io.File;

import site.iway.androidhelpers.ActionTimer;
import site.iway.androidhelpers.BitmapCache;
import site.iway.androidhelpers.BuildConfig;
import site.iway.androidhelpers.RPCEngine;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UnitHelper;
import site.iway.javahelpers.HanziPinyinHelper;
import site.iway.javahelpers.ObjectSaver;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.FileCache;
import site.iway.mymusic.utils.Player;
import site.iway.mymusic.utils.Settings;
import site.iway.mymusic.utils.Toaster;

/**
 * Created by iWay on 2017/12/25.
 */

public class MyMusicApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();

        UnitHelper.initialize(this);
        BitmapCache.setIsDebugMode(BuildConfig.DEBUG);
        BitmapCache.setContext(this);
        BitmapCache.setDownloadDirectory(Constants.DIR_NAME_IMAGE_CACHE);
        BitmapCache.setIsDebugMode(true);
        BitmapCache.initialize();
        ActionTimer.initialize(this);
        ObjectSaver.initialize(cacheDir + "/" + Constants.DIR_NAME_OBJECT_CACHE, true);
        UIThread.initialize();
        RPCEngine.initialize(2);
        HanziPinyinHelper.initialize();

        Settings.initialize(filesDir + "/" + Constants.FILE_NAME_SETTINGS, "sPBocrqJKgBiSag3");
        Toaster.initialize(this);
        FileCache.initialize(this);
        Player.initialize(this);
    }

}
