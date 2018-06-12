package site.iway.mymusic.servlets;

import site.iway.javahelpers.HanziPinyinHelper;
import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.config.Environment;
import site.iway.mymusic.protocol.data.Song;
import site.iway.mymusic.protocol.res.ListSongsRes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ListSongsServlet extends PlayListServlet {

    static {
        HanziPinyinHelper.initialize();
    }

    private String toPinYinPrefix(String string) {
        StringBuilder result = new StringBuilder();
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            String pinyin = HanziPinyinHelper.getAllPinyins(c, ' ');
            if (pinyin == null) {
                result.append(Character.toUpperCase(c));
            } else {
                result.append(Character.toUpperCase(pinyin.charAt(0)));
            }
        }
        return result.toString();
    }

    private boolean matchString(String string, String filter) {
        if (string.contains(filter)) {
            return true;
        } else {
            String pinYinPrefix = toPinYinPrefix(string);
            return pinYinPrefix.contains(filter.toUpperCase());
        }
    }

    private boolean matchFileName(String fileName, String filter) {
        if (fileName.endsWith(".mp3")) {
            if (StringHelper.nullOrEmpty(filter)) {
                return true;
            } else {
                Song song = new Song(fileName);
                return matchString(song.artist, filter) || matchString(song.name, filter);
            }
        } else {
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File directory = new File(Environment.MUSIC_ROOT);
        String filter = req.getParameter("filter");
        ListSongsRes listSongsRes = new ListSongsRes();
        listSongsRes.resultCode = ListSongsRes.OK;
        listSongsRes.resultMessage = ListSongsRes.OK_STRING;
        listSongsRes.fileNames = new ArrayList<>();
        String[] fileNames = directory.list();
        if (fileNames != null) {
            for (String fileName : fileNames) {
                if (matchFileName(fileName, filter)) {
                    listSongsRes.fileNames.add(fileName);
                }
            }
        }
        listSongsRes.playList = readPlayList();
        putJSONResponse(resp, listSongsRes);
    }

}
