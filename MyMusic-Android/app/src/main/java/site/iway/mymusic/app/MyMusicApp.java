package site.iway.mymusic.app;

import android.app.Application;

import site.iway.androidhelpers.ActionTimer;
import site.iway.androidhelpers.BitmapCache;
import site.iway.androidhelpers.BuildConfig;
import site.iway.androidhelpers.DeviceHelper;
import site.iway.androidhelpers.RPCEngine;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UnitHelper;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.LyricCache;
import site.iway.mymusic.utils.MusicCache;
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

        UnitHelper.initialize(this);
        BitmapCache.setIsDebugMode(BuildConfig.DEBUG);
        BitmapCache.setLoaderCount(2);
        BitmapCache.setMaxRAMUsage(DeviceHelper.getHeapGrowthLimit(this) / 3);
        BitmapCache.setMaxRAMUsageForSingleBitmap(4 * 1024 * 1024);
        BitmapCache.setLoaderThreadPriority(Thread.NORM_PRIORITY);
        BitmapCache.setUrlConnectTimeout(20 * 1000);
        BitmapCache.setUrlReadTimeout(20 * 1000);
        BitmapCache.setUrlRetryCount(1);
        BitmapCache.setDownloadDirectoryByContext(this, Constants.DIR_NAME_IMAGE_CACHE);
        BitmapCache.initialize(this);
        ActionTimer.initialize(this);
        UIThread.initialize();
        RPCEngine.initialize(2);

        Settings.initialize(getFilesDir() + "/" + Constants.FILE_NAME_SETTINGS, "sPBocrqJKgBiSag3");
        Toaster.initialize(this);
        MusicCache.initialize(this);
        LyricCache.initialize(this);
        Player.initialize(this);
    }

}