package site.iway.mymusic.net.req;

import com.google.gson.annotations.Expose;

import site.iway.mymusic.net.res.GetSongInfoRes;

/**
 * Created by iWay on 2017/12/28.
 */

public class GetSongInfoReq extends MMReq {

    public GetSongInfoReq() {
        url += "GetSongInfo";
        responseClass = GetSongInfoRes.class;
    }

    @Expose
    public String query;

    public Object tag;
}
