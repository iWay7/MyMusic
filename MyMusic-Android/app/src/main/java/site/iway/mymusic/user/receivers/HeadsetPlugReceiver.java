package site.iway.mymusic.user.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;

public class HeadsetPlugReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra("state")) {
            int state = intent.getIntExtra("state", 0);
            if (state == 0) {
                Player player = Player.getInstance();
                PlayTask playTask = player.getPlayTask();
                if (playTask != null) {
                    playTask.pause();
                }
            }
        }
    }

}
