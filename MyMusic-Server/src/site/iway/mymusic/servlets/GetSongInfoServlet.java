package site.iway.mymusic.servlets;

import site.iway.mymusic.config.Environment;
import site.iway.mymusic.external.baidu.BaiduMusicResponse;
import site.iway.mymusic.external.baidu.BaiduSongListItem;
import site.iway.mymusic.protocol.data.Song;
import site.iway.mymusic.protocol.data.SongInfo;
import site.iway.mymusic.protocol.res.GetSongInfoRes;
import site.iway.mymusic.protocol.res.RPCRes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GetSongInfoServlet extends BasicServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String query = req.getParameter("query");
        String fileName = req.getParameter("fileName");
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
        getSongInfoRes.list = new ArrayList<>();
        File albumFile = new File(Environment.ALBUM_ROOT, fileName);
        File lyricFile = new File(Environment.LYRIC_ROOT, fileName);
        if (albumFile.exists() || lyricFile.exists()) {
            SongInfo songInfo = new SongInfo();
            if (albumFile.exists()) {
                songInfo.imgLink = "MY";
            }
            if (lyricFile.exists()) {
                Song song = new Song(fileName);
                songInfo.lrcTitle = song.artist + " - " + song.name;
                songInfo.lrcLink = "MY";
            }
            getSongInfoRes.list.add(songInfo);
        }
        if (response != null && response.error_code == 22000 &&
                response.result != null && response.result.song_info != null &&
                response.result.song_info.song_list != null) {
            for (BaiduSongListItem item : response.result.song_info.song_list) {
                SongInfo songInfo = new SongInfo();
                songInfo.imgLink = item.pic_small;
                songInfo.lrcTitle = item.author + " - " + item.title;
                songInfo.lrcLink = item.lrclink;
                getSongInfoRes.list.add(songInfo);
            }
        }
        getSongInfoRes.resultCode = RPCRes.OK;
        getSongInfoRes.resultMessage = RPCRes.OK_STRING;
        putJSONResponse(resp, getSongInfoRes);
    }

}
