package site.iway.mymusic.net.req;

import site.iway.mymusic.net.req.base.MMReq;
import site.iway.mymusic.net.res.ListSongsRes;

/**
 * Created by iWay on 2017/12/27.
 */

public class ListSongsReq extends MMReq {

    public ListSongsReq() {
        requestUrl += "ListSongs";
        responseClass = ListSongsRes.class;
    }

    public String filter;

}
