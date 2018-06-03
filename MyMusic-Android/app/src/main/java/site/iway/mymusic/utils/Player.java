package site.iway.mymusic.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import site.iway.androidhelpers.UIThread;
import site.iway.mymusic.user.receivers.HeadsetPlugReceiver;
import site.iway.mymusic.utils.PlayTask.PlayStateListener;

/**
 * Created by iWay on 2017/12/27.
 */

public class Player implements PlayStateListener {

    private Context mContext;
    private Handler mHandler;
    private volatile PlayList mPlayList;
    private volatile PlayTask mPlayTask;

    private void setHeadSetPlugReceiver() {
        HeadsetPlugReceiver headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        mContext.registerReceiver(headsetPlugReceiver, intentFilter);
    }

    public Player(Context context) {
        mContext = context;
        mHandler = new Handler();
        mPlayList = PlayList.getInstance();
        setHeadSetPlugReceiver();
    }

    public PlayList getPlayList() {
        return mPlayList;
    }

    public PlayTask getPlayTask() {
        return mPlayTask;
    }

    public void play(String fileName) {
        if (mPlayTask != null) {
            mPlayTask.cancel();
            mPlayTask = null;
        }
        if (fileName != null) {
            mPlayTask = new PlayTask(fileName, this);
            mPlayTask.start();
        }
        UIThread.event(Constants.EV_PLAYER_TASK_CHANGED);
    }

    public void play() {
        String next = mPlayList.next(null);
        play(next);
    }

    public void previous() {
        String current = null;
        if (mPlayTask != null) {
            current = mPlayTask.getFileName();
        }
        String previous = mPlayList.previous(current);
        play(previous);
    }

    public void next(boolean auto) {
        String current = null;
        if (mPlayTask != null) {
            current = mPlayTask.getFileName();
        }
        String next = auto ? mPlayList.nextAuto(current) : mPlayList.next(current);
        play(next);
    }

    public void next() {
        next(false);
    }

    @Override
    public void onPlayStateChanged(PlayTask playTask) {
        if (playTask == mPlayTask) {
            switch (playTask.getTaskState()) {
                case PlayTask.STATE_COMPLETED:
                case PlayTask.STATE_ERROR:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            next(true);
                        }
                    });
                    break;
            }
        }
    }

    private static Player sInstance;

    public static void initialize(Context context) {
        sInstance = new Player(context);
    }

    public static Player getInstance() {
        return sInstance;
    }

}
