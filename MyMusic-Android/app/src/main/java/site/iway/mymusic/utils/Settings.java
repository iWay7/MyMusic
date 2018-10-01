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
            return uuidString;
        }
    }

    private static final String PLAYER_PLAY_MODE = "PLAYER_PLAY_MODE";

    public static int getPlayerPlayMode() {
        return getInt(PLAYER_PLAY_MODE, PlayList.MODE_LOOP_LIST);
    }

    public static void setPlayerPlayMode(int playMode) {
        putInt(PLAYER_PLAY_MODE, playMode);
    }

    public static final int SORT_BY_ADD_TIME = -1;
    public static final int SORT_BY_ARTIST_NAME = 0;
    public static final int SORT_BY_SONG_NAME = 1;

    private static final String PLAY_LIST_SORT_TYPE = "PLAY_LIST_SORT_TYPE";

    public static int getPlayListSortType() {
        return getInt(PLAY_LIST_SORT_TYPE, SORT_BY_ADD_TIME);
    }

    public static void setPlayListSortType(int sortType) {
        putInt(PLAY_LIST_SORT_TYPE, sortType);
    }

    private static final String SEARCH_SORT_TYPE = "SEARCH_SORT_TYPE";

    public static int getSearchSortType() {
        return getInt(SEARCH_SORT_TYPE, SORT_BY_ADD_TIME);
    }

    public static void setSearchSortType(int sortType) {
        putInt(SEARCH_SORT_TYPE, sortType);
    }
}
