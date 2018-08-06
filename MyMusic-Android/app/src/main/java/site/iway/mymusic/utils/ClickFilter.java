package site.iway.mymusic.utils;

public class ClickFilter {

    private static long sLastClickTime;

    public static boolean isTooFast() {
        long now = System.nanoTime();
        boolean isTooFast = now - sLastClickTime < 3000000;
        sLastClickTime = now;
        return isTooFast;
    }

}
