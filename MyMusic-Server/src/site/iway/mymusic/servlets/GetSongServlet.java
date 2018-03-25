package site.iway.mymusic.servlets;

import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.config.Environment;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class GetSongServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getParameter("fileName");
        if (StringHelper.nullOrWhiteSpace(fileName)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter fileName is empty.");
            return;
        }
        File file = new File(Environment.MUSIC_ROOT + fileName);
        if (file.exists()) {
            resp.setContentType("audio/mpeg");
            resp.setContentLengthLong(file.length());
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = resp.getOutputStream();
            byte[] buffer = new byte[65536];
            int count;
            while ((count = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested song not found.");
        }
    }

}
