package site.iway.mymusic.net.mymusic.models;

import com.google.gson.annotations.Expose;

import site.iway.mymusic.net.mymusic.MyMusicReq;

/**
 * Created by iWay on 2017/12/28.
 */

public class GetSongInfoReq extends MyMusicReq {

    public GetSongInfoReq() {
        url += "GetSongInfo";
        responseClass = GetSongInfoRes.class;
    }

    @Expose
    public String query;

    public Object tag;
}
