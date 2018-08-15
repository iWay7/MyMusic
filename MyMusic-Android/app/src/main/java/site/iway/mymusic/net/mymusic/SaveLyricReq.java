package site.iway.mymusic.net.mymusic;

import com.google.gson.annotations.Expose;

public class SaveLyricReq extends MyMusicReq {

    public SaveLyricReq() {
        url += "SaveLyric";
        responseClass = SaveLyricRes.class;
    }

    @Expose
    public String fileName;

    @Expose
    public String lyric;

}
