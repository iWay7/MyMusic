package site.iway.mymusic.net.mymusic;

import com.google.gson.annotations.Expose;

import java.util.Collections;
import java.util.Comparator;

import site.iway.javahelpers.HanziPinyinHelper;
import site.iway.mymusic.utils.Settings;
import site.iway.mymusic.utils.Song;

/**
 * Created by iWay on 2017/12/27.
 */

public class ListSongsReq extends MyMusicReq {

    public ListSongsReq() {
        url += "ListSongs";
        responseClass = ListSongsRes.class;
    }

    @Expose
    public String filter;

    @Override
    protected void onFinish() {
        if (response != null) {
            ListSongsRes listSongRes = (ListSongsRes) response;
            Collections.sort(listSongRes.fileNames, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    switch (Settings.getSearchSortType()) {
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
        super.onFinish();
    }

}
