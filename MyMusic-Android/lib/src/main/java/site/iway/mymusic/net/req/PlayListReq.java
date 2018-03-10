package site.iway.mymusic.net.req;

import site.iway.mymusic.net.req.base.MMReq;
import site.iway.mymusic.net.res.PlayListRes;

/**
 * Created by iWay on 2017/12/26.
 */

public class PlayListReq extends MMReq {

    public static final String ACTION_GET = "get";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_REMOVE = "remove";

    public PlayListReq() {
        requestUrl += "PlayList";
        responseClass = PlayListRes.class;
    }

    public String action;
    public String fileNames;

}
