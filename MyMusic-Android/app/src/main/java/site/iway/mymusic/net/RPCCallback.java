package site.iway.mymusic.net;

/**
 * Created by iWay on 2018/3/25.
 */

public interface RPCCallback {

    public void onRequestOK(RPCBaseReq req);

    public void onRequestER(RPCBaseReq req);

}
