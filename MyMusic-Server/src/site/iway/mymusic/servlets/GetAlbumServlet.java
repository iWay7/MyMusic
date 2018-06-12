package site.iway.mymusic.servlets;

import site.iway.mymusic.config.Environment;

import java.io.File;

public class GetAlbumServlet extends GetFileServlet {

    @Override
    public File getFile(String fileName) {
        return new File(Environment.ALBUM_ROOT + fileName);
    }

    @Override
    public String getContentType() {
        return "image/jpeg";
    }

}
