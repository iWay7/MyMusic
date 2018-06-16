package site.iway.mymusic.net.mymusic.models;

import com.google.gson.annotations.Expose;

import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.net.mymusic.MyMusicReq;
import site.iway.mymusic.net.mymusic.models.common.SongInfo;
import site.iway.mymusic.utils.Constants;

/**
 * Created by iWay on 2017/12/28.
 */

public class GetSongInfoReq extends MyMusicReq {

    public GetSongInfoReq() {
        url += "GetSongInfo";
        responseClass = GetSongInfoRes.class;
    }

    @Expose
    public String query;

    @Expose
    public String fileName;

    public Object tag;

    @Override
    protected void onFinish() {
        if (response != null) {
            GetSongInfoRes getSongInfoRes = (GetSongInfoRes) response;
            if (getSongInfoRes.list != null) {
                for (SongInfo songInfo : getSongInfoRes.list) {
                    if ("MY".equals(songInfo.imgLink)) {
                        songInfo.imgLink = Constants.ALBUM_URL_BASE + StringHelper.urlEncode(fileName);
                    }
                    if ("MY".equals(songInfo.lrcLink)) {
                        songInfo.lrcLink = Constants.LYRIC_URL_BASE + StringHelper.urlEncode(fileName);
                    }
                }
            }
        }
        super.onFinish();
    }
}
