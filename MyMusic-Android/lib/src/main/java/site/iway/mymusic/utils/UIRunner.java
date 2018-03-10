package site.iway.mymusic.utils;

import android.os.Handler;

/**
 * Created by iWay on 2017/7/13.
 */
public class UIRunner {

    private static Thread sUIThread;
    private static Handler sUIHandler;

    public static void initialize() {
        sUIThread = Thread.currentThread();
        sUIHandler = new Handler();
    }

    private static boolean checkThread() {
        Thread currentThread = Thread.currentThread();
        return currentThread == sUIThread;
    }

    public static void run(Runnable runnable) {
        if (checkThread())
            runnable.run();
        else
            sUIHandler.post(runnable);
    }

    public static void runDelayed(Runnable runnable, long delayMillis) {
        sUIHandler.postDelayed(runnable, delayMillis);
    }

}
