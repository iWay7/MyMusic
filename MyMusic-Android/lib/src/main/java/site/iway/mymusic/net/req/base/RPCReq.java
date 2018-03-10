package site.iway.mymusic.net.req.base;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import site.iway.mymusic.net.res.base.RPCRes;

/**
 * Created by iWay on 2016/5/23.
 */
public abstract class RPCReq implements Serializable {

    @Expose(serialize = false, deserialize = false)
    public String requestUrl;
    @Expose(serialize = false, deserialize = false)
    public int connectTimeout = 20 * 1000;
    @Expose(serialize = false, deserialize = false)
    public int readTimeout = 20 * 1000;
    @Expose(serialize = false, deserialize = false)
    public long minDelayTime = 0;
    @Expose(serialize = false, deserialize = false)
    public boolean ordered = false;
    @Expose(serialize = false, deserialize = false)
    public Class<? extends RPCRes> responseClass;

}
