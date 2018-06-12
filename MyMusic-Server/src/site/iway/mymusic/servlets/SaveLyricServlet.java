package site.iway.mymusic.servlets;

import site.iway.javahelpers.StringHelper;
import site.iway.javahelpers.TextRW;
import site.iway.mymusic.config.Environment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveLyricServlet extends BasicServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getParameter("fileName");
        if (StringHelper.nullOrWhiteSpace(fileName)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter action is empty.");
            return;
        }
        String lyric = req.getParameter("lyric");
        if (StringHelper.nullOrWhiteSpace(lyric)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter lyric is empty.");
            return;
        }
        File file = new File(Environment.LYRIC_ROOT);
        if (!file.exists() && !file.mkdirs()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Create directory failed.");
            return;
        }
        file = new File(file, fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            TextRW.writeAllText(fileOutputStream, CHARSET, lyric);
            putJSONResponseOK(resp);
        } catch (Exception e) {
            putJSONResponseFail(resp);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    // nothing
                }
            }
        }
    }

}
