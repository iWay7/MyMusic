package site.iway.mymusic.net.mymusic;

import java.net.HttpURLConnection;

import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.utils.Constants;

/**
 * Created by iWay on 2018/3/25.
 */

public class MyMusicReq extends RPCBaseReq {

    public MyMusicReq() {
        url = Constants.SERVICE_URL_BASE;
    }

    @Override
    protected void onPrepare() throws Exception {
        String query = buildQuery();
        if (!query.isEmpty()) {
            mOutputData = query.getBytes(CHARSET);
        }
    }

    @Override
    protected void onConnect(HttpURLConnection connection) throws Exception {
        super.onConnect(connection);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + CHARSET.name());
        connection.setRequestProperty("Content-Length", String.valueOf(mOutputData == null ? 0 : mOutputData.length));
    }
}
