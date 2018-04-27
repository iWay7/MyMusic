package site.iway.mymusic.net.req;

import com.google.gson.annotations.Expose;

import site.iway.mymusic.net.res.ListSongsRes;

/**
 * Created by iWay on 2017/12/27.
 */

public class ListSongsReq extends MMReq {

    public ListSongsReq() {
        url += "ListSongs";
        responseClass = ListSongsRes.class;
    }

    @Expose
    public String filter;

}
