package site.iway.mymusic.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Set;

import site.iway.helpers.GsonHelper;
import site.iway.helpers.RPCInfo;
import site.iway.helpers.RPCInfoManager;
import site.iway.helpers.RPCListener;
import site.iway.helpers.URLCodec;
import site.iway.mymusic.BuildConfig;
import site.iway.mymusic.net.req.base.GetReq;
import site.iway.mymusic.net.req.base.HttpReq;
import site.iway.mymusic.net.req.base.PostReq;
import site.iway.mymusic.net.req.base.RPCReq;
import site.iway.mymusic.net.res.base.RPCRes;

/**
 * Created by iWay on 2015/10/23.
 */
public class RPCClient extends RPCInfoManager {

    protected String mLogTag;
    protected Context mContext;
    protected Gson mGson;
    protected Charset mCharset;

    public RPCClient(Context context) {
        super(2);
        mLogTag = "RPCClient";
        mContext = context;
        mGson = GsonHelper.create(GsonHelper.TYPE_NORMAL_AND_EXPOSE_BASED_FIELDS);
        mCharset = Charset.forName("utf-8");
    }

    private String getRequestString(HttpReq httpReq) {
        JsonObject jsonObject = (JsonObject) mGson.toJsonTree(httpReq);
        Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
        StringBuilder content = new StringBuilder();
        for (Entry<String, JsonElement> entry : set) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!TextUtils.isEmpty(key) && value != null) {
                content.append("&");
                String keyEncoded = URLCodec.encode(key);
                content.append(keyEncoded);
                content.append("=");
                String valueString = value.getAsString();
                String valueStringEncoded = URLCodec.encode(valueString);
                content.append(valueStringEncoded);
            }
        }
        if (TextUtils.isEmpty(content)) {
            return "";
        } else {
            return content.substring(1);
        }
    }

    @Override
    protected void onRPCInfoTaken(RPCInfo rpcInfo) throws Exception {
        RPCReq rpcReq = (RPCReq) rpcInfo.getRequest();
        if (rpcReq instanceof HttpReq) {
            HttpReq httpReq = (HttpReq) rpcReq;
            String params = getRequestString(httpReq);
            if (httpReq instanceof GetReq) {
                GetReq getReq = (GetReq) httpReq;
                if (TextUtils.isEmpty(params)) {
                    rpcInfo.setUrl(rpcReq.requestUrl);
                } else {
                    rpcInfo.setUrl(rpcReq.requestUrl + "?" + params);
                }
            }
            if (httpReq instanceof PostReq) {
                PostReq postReq = (PostReq) httpReq;
                byte[] data = null;
                if (!TextUtils.isEmpty(params)) {
                    data = params.getBytes(mCharset);
                }
                rpcInfo.setUrl(postReq.requestUrl);
                rpcInfo.setData(data);
            }
        }
        rpcInfo.setConnectTimeout(rpcReq.connectTimeout);
        rpcInfo.setReadTimeout(rpcReq.readTimeout);
        rpcInfo.setMinDelayTime(rpcReq.minDelayTime);
        rpcInfo.setOrdered(rpcReq.ordered);
        rpcInfo.setResponseClass(rpcReq.responseClass);
    }

    @Override
    protected void onUrlConnectionOpened(RPCInfo rpcInfo, HttpURLConnection connection) throws Exception {
        connection.setUseCaches(false);
        RPCReq rpcReq = (RPCReq) rpcInfo.getRequest();
        if (rpcReq instanceof GetReq) {
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
        }
        if (rpcReq instanceof PostReq) {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            connection.setRequestProperty("Content-Length", rpcInfo.getDataLengthString());
        }
        connection.setDoInput(true);
        connection.setRequestProperty("Accept-Encoding", "identity");
    }

    @Override
    protected void onUrlConnectionEstablished(RPCInfo info, HttpURLConnection connection) throws Exception {
        super.onUrlConnectionEstablished(info, connection);
        RPCReq rpcReq = (RPCReq) info.getRequest();
        if (BuildConfig.DEBUG) {
            String url = info.getUrl();
            Log.d(mLogTag, "Connected : " + url);
        }
    }

    @Override
    protected void sendData(RPCInfo info, HttpURLConnection connection, OutputStream stream) throws Exception {
        info.writeDataToStream(stream);
        RPCReq rpcReq = (RPCReq) info.getRequest();
        if (BuildConfig.DEBUG) {
            String string = info.getDataString(mCharset);
            if (TextUtils.isEmpty(string))
                Log.d(mLogTag, "Send data : NONE");
            else
                Log.d(mLogTag, "Send data : " + string);
        }
    }

    protected RPCRes getRPCResponse(RPCInfo rpcInfo, String jsonText) {
        Class cls = rpcInfo.getResponseClass();
        return (RPCRes) mGson.fromJson(jsonText, cls);
    }

    @Override
    protected void onGetData(RPCInfo rpcInfo, byte[] data) throws Exception {
        RPCReq rpcReq = (RPCReq) rpcInfo.getRequest();
        if (BuildConfig.DEBUG) {
            String string = new String(data, mCharset);
            Log.d(mLogTag, "Get data  : " + string);
        }
        String responseString = new String(data, mCharset);
        RPCRes rpcRes = getRPCResponse(rpcInfo, responseString);
        handleOKOnUIThread(rpcInfo, rpcRes);
    }

    @Override
    protected void onGetError(RPCInfo rpcInfo, Exception exception) {
        if (BuildConfig.DEBUG) {
            Log.d(mLogTag, "Get error : " + exception);
        }
        handleErrorOnUIThread(rpcInfo, exception);
    }

    private static RPCClient sClient;

    public static void initialize(Context context) {
        sClient = new RPCClient(context);
    }

    public static RPCInfo doRequest(RPCReq req, RPCListener listener) {
        RPCInfo rpcInfo = new RPCInfo();
        rpcInfo.setRequest(req);
        rpcInfo.setListener(listener);
        return sClient.addRequest(rpcInfo);
    }

}
