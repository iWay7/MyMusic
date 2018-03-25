package site.iway.mymusic.net.req;

import com.google.gson.annotations.Expose;

import site.iway.mymusic.net.res.PlayListRes;

/**
 * Created by iWay on 2017/12/26.
 */

public class PlayListReq extends MMReq {

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

    public Object tag;

}
