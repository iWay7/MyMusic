package site.iway.mymusic.net.mymusic.models;

import com.google.gson.annotations.Expose;

import site.iway.mymusic.net.mymusic.MyMusicReq;

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

}
