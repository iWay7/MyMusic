package site.iway.mymusic.protocol.data;

/**
 * Created by iWay on 2017/12/27.
 */

public class Song extends RPCData {

    public Song(String fileName) {
        int splitterIndex = fileName.indexOf('-');
        artist = fileName.substring(0, splitterIndex - 1);
        int totalLength = fileName.length();
        name = fileName.substring(splitterIndex + 2, totalLength - 4);
    }

    public String name;
    public String artist;

}
