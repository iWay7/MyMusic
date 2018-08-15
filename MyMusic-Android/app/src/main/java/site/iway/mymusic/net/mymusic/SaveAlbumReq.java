package site.iway.mymusic.net.mymusic;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class SaveAlbumReq extends MyMusicReq {

    public SaveAlbumReq() {
        url += "SaveAlbum";
        responseClass = SaveLyricRes.class;
    }

    public String fileName;
    public File file;

    @Override
    protected void onPrepare() throws Exception {
        // nothing
    }

    @Override
    protected void onConnect(HttpURLConnection connection) throws Exception {
        super.onConnect(connection);
        byte[] fileNameBytes = fileName.getBytes(CHARSET);
        String fileNameBase64 = Base64.encodeToString(fileNameBytes, Base64.NO_WRAP);
        connection.setRequestProperty("File-Name", fileNameBase64);
        connection.setRequestProperty("Content-Type", "binary");
        connection.setRequestProperty("Content-Length", String.valueOf(file.length()));
    }

    private InputStream mInputStream;
    private OutputStream mOutputStream;

    protected void doOutput(HttpURLConnection connection) throws Exception {
        mInputStream = new FileInputStream(file);
        mOutputStream = connection.getOutputStream();
        byte[] buffer = new byte[16 * 1024];
        int count = mInputStream.read(buffer);
        while (count > -1) {
            mOutputStream.write(buffer, 0, count);
            count = mInputStream.read(buffer);
        }
    }

    @Override
    protected void onFinally() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (Exception e) {
                // nothing
            }
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (Exception e) {
                // nothing
            }
        }
        super.onFinally();
    }

}
