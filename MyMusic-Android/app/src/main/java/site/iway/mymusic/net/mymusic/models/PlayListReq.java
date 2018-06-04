package site.iway.mymusic.net.mymusic.models;

import com.google.gson.annotations.Expose;

import java.util.Collections;
import java.util.Comparator;

import site.iway.javahelpers.HanziPinyinHelper;
import site.iway.mymusic.net.mymusic.MyMusicReq;
import site.iway.mymusic.utils.Settings;
import site.iway.mymusic.utils.Song;

/**
 * Created by iWay on 2017/12/26.
 */

public class PlayListReq extends MyMusicReq {

    public static final String ACTION_GET = "get";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_REMOVE = "remove";

    public PlayListReq() {
        url += "PlayList";
        responseClass = PlayListRes.class;
    }

    @Expose
    public String action;
    @Expose
    public String fileNames;

    @Override
    protected void onFinish() {
        if (ACTION_GET.equals(action) && response != null) {
            PlayListRes playListRes = (PlayListRes) response;
            if (playListRes.fileNames != null) {
                Collections.sort(playListRes.fileNames, new Comparator<String>() {

                    @Override
                    public int compare(String o1, String o2) {
                        switch (Settings.getPlayListSortType()) {
                            case Settings.SORT_BY_ARTIST_NAME:
                                Song o1s = new Song(o1);
                                Song o2s = new Song(o2);
                                String pinyin1 = HanziPinyinHelper.getPinyin(o1s.artist + o1s.name);
                                String pinyin2 = HanziPinyinHelper.getPinyin(o2s.artist + o2s.name);
                                return pinyin1.compareTo(pinyin2);
                            case Settings.SORT_BY_SONG_NAME:
                                o1s = new Song(o1);
                                o2s = new Song(o2);
                                pinyin1 = HanziPinyinHelper.getPinyin(o1s.name + o1s.artist);
                                pinyin2 = HanziPinyinHelper.getPinyin(o2s.name + o2s.artist);
                                return pinyin1.compareTo(pinyin2);
                        }
                        return 0;
                    }
                });
            }
        }
    }

    public Object tag;

}
