package site.iway.mymusic.utils;

/**
 * Created by iWay on 2017/12/27.
 */

public class Song {

    public Song(String fileName) {
        int splitterIndex = fileName.indexOf(" - ");
        artist = fileName.substring(0, splitterIndex);
        int totalLength = fileName.length();
        name = fileName.substring(splitterIndex + 3, totalLength - 4);
    }

    public final String artist;
    public final String name;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Song) {
            Song another = (Song) obj;
            return artist.equals(another.artist) && name.equals(another.name);
        } else {
            return false;
        }
    }

}
