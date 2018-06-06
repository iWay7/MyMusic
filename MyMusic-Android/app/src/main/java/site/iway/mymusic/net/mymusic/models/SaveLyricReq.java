package site.iway.mymusic.net.mymusic.models;

import com.google.gson.annotations.Expose;

import site.iway.mymusic.net.mymusic.MyMusicReq;

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
