package site.iway.mymusic.servlets;

import site.iway.javahelpers.StringHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.Base64.Decoder;

public abstract class SaveFileServlet extends BasicServlet {

    public abstract File getRoot();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getHeader("File-Name");
        if (StringHelper.nullOrWhiteSpace(fileName)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Header File-Name is empty.");
            return;
        }
        Decoder decoder = Base64.getDecoder();
        byte[] fileNameBytes = decoder.decode(fileName);
        fileName = new String(fileNameBytes, CHARSET);
        File file = getRoot();
        if (!file.exists() && !file.mkdirs()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Create directory failed.");
            return;
        }
        file = new File(file, fileName);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = req.getInputStream();
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[16 * 1024];
            int count;
            while ((count = inputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
            putJSONResponseOK(resp);
        } catch (Exception e) {
            putJSONResponseFail(resp);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    // nothing
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // nothing
                }
            }
        }
    }

}
