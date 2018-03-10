package site.iway.mymusic.net.req;

import site.iway.mymusic.net.req.base.MMReq;
import site.iway.mymusic.net.res.GetSongInfoRes;

/**
 * Created by iWay on 2017/12/28.
 */

public class GetSongInfoReq extends MMReq {

    public GetSongInfoReq() {
        requestUrl += "GetSongInfo";
        responseClass = GetSongInfoRes.class;
    }

    public String query;

}
