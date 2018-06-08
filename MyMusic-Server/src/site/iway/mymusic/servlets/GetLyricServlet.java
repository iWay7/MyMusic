package site.iway.mymusic.servlets;

import site.iway.mymusic.config.Environment;

import java.io.File;

public class GetLyricServlet extends GetFileServlet {

    @Override
    public File getFile(String fileName) {
        return new File(Environment.LYRIC_ROOT + fileName);
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }

}
