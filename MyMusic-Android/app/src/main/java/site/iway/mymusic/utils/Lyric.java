package site.iway.mymusic.utils;

public class Lyric {

    public Lyric(String lyricName, String lyricUrl) {
        int blankIndex = lyricName.indexOf(" - ");
        if (blankIndex > -1) {
            artist = lyricName.substring(0, blankIndex);
            name = lyricName.substring(blankIndex + 3);
        } else {
            artist = null;
            name = lyricName;
        }
        url = lyricUrl;
    }

    public final String artist;
    public final String name;
    public final String url;

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
