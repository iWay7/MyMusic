package site.iway.mymusic.utils;

import java.util.UUID;

import site.iway.javahelpers.PrefsStatic;

public class Settings extends PrefsStatic {

    // SETTINGS UUID //////////////////////////////////////////////////////////////////////////////////

    private static final String SETTING_UUID = "SETTING_UUID";

    public static String getSettingsUUID() {
        if (contains(SETTING_UUID)) {
            return getString(SETTING_UUID);
        } else {
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();
            putString(SETTING_UUID, uuidString);
            commit();
            return uuidString;
        }
    }

    private static final String PLAYER_PLAY_MODE = "PLAYER_PLAY_MODE";

    public static int getPlayerPlayMode() {
        return getInt(PLAYER_PLAY_MODE, Player.MODE_LOOP_LIST);
    }

    public static void setPlayerPlayMode(int playMode) {
        putInt(PLAYER_PLAY_MODE, playMode);
    }

}
