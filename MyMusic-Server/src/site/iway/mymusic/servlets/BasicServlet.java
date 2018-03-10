package site.iway.mymusic.servlets;

import com.google.gson.Gson;
import site.iway.mymusic.protocol.res.RPCRes;
import site.iway.mymusic.utilities.StreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public abstract class BasicServlet extends HttpServlet {

    protected static final Charset CHARSET = Charset.forName("utf-8");
    protected static final Gson GSON = new Gson();

    private static final String SIGN_KEY = "553afd92738fd27c984c5b5ba8b4ad4d";

    protected byte[] readFromURL(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        return StreamReader.readAllBytes(inputStream, 32 * 1024, 256 * 1024 * 1024);
    }

    protected String readFromURLAsString(String url) throws IOException {
        byte[] content = readFromURL(url);
        return new String(content, CHARSET);
    }

    protected <T> T readFromURLAsObject(String url, Class<T> cls) throws IOException {
        String content = readFromURLAsString(url);
        return GSON.fromJson(content, cls);
    }

    protected void putJSONResponse(HttpServletResponse resp, String responseString) throws ServletException, IOException {
        byte[] data = responseString.getBytes(CHARSET);
        resp.setContentType("application/json; charset=" + CHARSET.name());
        resp.setContentLength(data.length);
        OutputStream outputStream = resp.getOutputStream();
        outputStream.write(data);
        outputStream.flush();
    }

    protected void putJSONResponse(HttpServletResponse resp, RPCRes resObject) throws ServletException, IOException {
        String json = GSON.toJson(resObject);
        putJSONResponse(resp, json);
    }

    protected void putJSONResponseOK(HttpServletResponse resp) throws ServletException, IOException {
        RPCRes rpcRes = new RPCRes();
        rpcRes.resultCode = RPCRes.OK;
        rpcRes.resultMessage = RPCRes.OK_STRING;
        putJSONResponse(resp, rpcRes);
    }

    protected void putJSONResponseFAIL(HttpServletResponse resp, RPCRes resObject) throws ServletException, IOException {
        RPCRes rpcRes = new RPCRes();
        rpcRes.resultCode = RPCRes.FAIL;
        rpcRes.resultMessage = RPCRes.FAIL_STRING;
        putJSONResponse(resp, rpcRes);
    }

    protected abstract void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequest(req, resp);
    }

}
