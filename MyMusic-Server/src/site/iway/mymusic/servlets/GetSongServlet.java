package site.iway.mymusic.servlets;

import site.iway.mymusic.config.Environment;

import java.io.File;

public class GetSongServlet extends GetFileServlet {

    @Override
    public File getFile(String fileName) {
        return new File(Environment.MUSIC_ROOT + fileName);
    }

    @Override
    public String getContentType() {
        return "audio/mpeg";
    }

}
