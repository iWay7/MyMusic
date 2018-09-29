package site.iway.mymusic.servlets;

import site.iway.javahelpers.StringHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public abstract class GetFileServlet extends HttpServlet {

    public abstract File getFile(String fileName);

    public abstract String getContentType();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getParameter("fileName");
        if (StringHelper.nullOrBlank(fileName)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter fileName is empty.");
            return;
        }
        File file = getFile(fileName);
        if (file.exists()) {
            resp.setContentType(getContentType());
            resp.setContentLengthLong(file.length());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(file);
                outputStream = resp.getOutputStream();
                byte[] buffer = new byte[65536];
                int count;
                while ((count = inputStream.read(buffer, 0, buffer.length)) > -1) {
                    outputStream.write(buffer, 0, count);
                }
                outputStream.flush();
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request.");
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        // nothing
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        // nothing
                    }
                }
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested resource not found.");
        }

    }

}
