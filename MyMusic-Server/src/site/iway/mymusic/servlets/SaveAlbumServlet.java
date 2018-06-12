package site.iway.mymusic.servlets;

import site.iway.mymusic.config.Environment;

import java.io.File;

public class SaveAlbumServlet extends SaveFileServlet {

    @Override
    public File getRoot() {
        return new File(Environment.ALBUM_ROOT);
    }

}
