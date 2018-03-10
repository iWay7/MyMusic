package site.iway.mymusic.servlets;

import site.iway.mymusic.external.baidu.BaiduMusicResponse;
import site.iway.mymusic.external.baidu.BaiduSongListItem;
import site.iway.mymusic.protocol.data.SongInfo;
import site.iway.mymusic.protocol.res.GetSongInfoRes;
import site.iway.mymusic.protocol.res.RPCRes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GetSongInfoServlet extends BasicServlet {

    private static ConcurrentMap<String, GetSongInfoRes> sResultCache = new ConcurrentHashMap<>();

    @Override
    protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        if (sResultCache.containsKey(query)) {
            putJSONResponse(resp, sResultCache.get(query));
            return;
        }
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?" +
                "from=android&" +
                "version=5.6.5.0&" +
                "method=baidu.ting.search.merge&" +
                "format=json&" +
                "query=" + URLEncoder.encode(query, "utf-8") + "&" +
                "page_no=1&" +
                "page_size=50&" +
                "type=-1&" +
                "data_source=0&" +
                "use_cluster=1";
        BaiduMusicResponse response = readFromURLAsObject(url, BaiduMusicResponse.class);
        GetSongInfoRes getSongInfoRes = new GetSongInfoRes();
        if (response != null && response.error_code == 22000 &&
                response.result != null && response.result.song_info != null &&
                response.result.song_info.song_list != null && !response.result.song_info.song_list.isEmpty()) {
            getSongInfoRes.resultCode = RPCRes.OK;
            getSongInfoRes.resultMessage = RPCRes.OK_STRING;
            getSongInfoRes.list = new ArrayList<>();
            for (BaiduSongListItem item : response.result.song_info.song_list) {
                SongInfo songInfo = new SongInfo();
                songInfo.imgLink = item.pic_small;
                songInfo.lrcTitle = item.author + " - " + item.title;
                songInfo.lrcLink = item.lrclink;
                getSongInfoRes.list.add(songInfo);
            }
        } else {
            getSongInfoRes.resultCode = RPCRes.FAIL;
            getSongInfoRes.resultMessage = RPCRes.FAIL_STRING;
        }
        sResultCache.put(query, getSongInfoRes);
        putJSONResponse(resp, getSongInfoRes);
    }

}
