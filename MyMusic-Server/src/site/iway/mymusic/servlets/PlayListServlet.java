package site.iway.mymusic.servlets;

import site.iway.mymusic.config.Environment;
import site.iway.mymusic.protocol.res.PlayListRes;
import site.iway.mymusic.protocol.res.RPCRes;
import site.iway.mymusic.utilities.StringHelper;
import site.iway.mymusic.utilities.TextReadWrite;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayListServlet extends BasicServlet {

    protected List<String> readPlayList() throws IOException {
        List<String> fileNames = new ArrayList<>();
        File playListFile = new File(Environment.PLAY_LIST);
        if (playListFile.exists()) {
            FileInputStream inputStream = new FileInputStream(playListFile);
            List<String> lines = TextReadWrite.readAllLines(inputStream, CHARSET);
            fileNames.addAll(lines);
            inputStream.close();
        }
        return fileNames;
    }

    protected void writePlayList(List<String> fileNames) throws IOException {
        File playListFile = new File(Environment.PLAY_LIST);
        FileOutputStream outputStream = new FileOutputStream(playListFile);
        TextReadWrite.writeAllLines(outputStream, CHARSET, fileNames);
        outputStream.close();
    }

    private static final String ACTION_GET = "get";
    private static final String ACTION_ADD = "add";
    private static final String ACTION_REMOVE = "remove";

    @Override
    protected void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (StringHelper.nullOrWhiteSpace(action)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter action is empty.");
            return;
        }
        switch (action) {
            case ACTION_GET:
                PlayListRes playListRes = new PlayListRes();
                playListRes.resultCode = RPCRes.OK;
                playListRes.resultMessage = RPCRes.OK_STRING;
                playListRes.fileNames = readPlayList();
                putJSONResponse(resp, playListRes);
                break;
            case ACTION_ADD:
            case ACTION_REMOVE:
                String fileNamesParam = req.getParameter("fileNames");
                if (StringHelper.nullOrWhiteSpace(action)) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter fileNames is empty.");
                    break;
                }
                String[] fileNames = fileNamesParam.split(";");
                List<String> playList = readPlayList();
                for (String fileName : fileNames) {
                    switch (action) {
                        case ACTION_ADD:
                            if (!playList.contains(fileName)) {
                                playList.add(fileName);
                            }
                            break;
                        case ACTION_REMOVE:
                            if (playList.contains(fileName)) {
                                playList.remove(fileName);
                            }
                            break;
                    }
                }
                writePlayList(playList);
                putJSONResponseOK(resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter action " + action + " unknown.");
                break;
        }
    }

}
