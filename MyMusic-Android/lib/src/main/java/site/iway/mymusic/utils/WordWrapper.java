package site.iway.mymusic.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iWay on 2018/1/5.
 */

public class WordWrapper {

    public interface CharWidthMeasurer {
        public float measureCharWidth(char c);
    }

    public static List<String> wordWrap(String text, float length, CharWidthMeasurer measurer) {
        List<String> lines = new ArrayList<>();
        char[] chars = text.toCharArray();
        if (chars.length == 0) {
            return lines;
        }
        StringBuilder lineBuilder = new StringBuilder();
        float lineLength = 0;
        for (int i = 0; i < chars.length; i++) {
            float charLength = measurer.measureCharWidth(chars[i]);
            if (lineLength + charLength > length) {
                if (lineBuilder.length() > 0) {
                    lines.add(lineBuilder.toString());
                    lineBuilder.delete(0, lineBuilder.length());
                }
                lineBuilder.append(chars[i]);
                lineLength = charLength;
            } else {
                lineBuilder.append(chars[i]);
                lineLength += charLength;
            }
        }
        lines.add(lineBuilder.toString());
        return lines;
    }

    public static List<String> wordWrap(String text, float width, CharWidthMeasurer measurer, char ignoreChar) {
        List<String> lines = new ArrayList<>();
        char[] chars = text.toCharArray();
        if (chars.length == 0) {
            return lines;
        }
        float[] lefts = new float[chars.length + 1];
        float[] rights = new float[chars.length];
        float sum = 0;
        for (int i = 0; i < chars.length; i++) {
            lefts[i] = sum;
            rights[i] = sum + measurer.measureCharWidth(chars[i]);
            sum = rights[i];
        }
        lefts[lefts.length - 1] = rights[rights.length - 1];
        int start = 0;
        while (start < chars.length && chars[start] == ignoreChar) {
            start++;
        }
        if (start < chars.length) {
            float lengthToStart = lefts[start];
            for (int i = start; i < chars.length; i++) {
                if (rights[i] - lengthToStart > width) {
                    int temp = i;
                    while (temp > start && chars[temp - 1] == ignoreChar) {
                        temp--;
                    }
                    if (temp > start) {
                        lines.add(new String(chars, start, temp - start));
                    }
                    while (i < chars.length && chars[i] == ignoreChar) {
                        i++;
                    }
                    start = i;
                    lengthToStart = lefts[i];
                }
            }
            int temp = chars.length;
            while (temp > start && chars[temp - 1] == ignoreChar) {
                temp--;
            }
            if (temp > start) {
                lines.add(new String(chars, start, temp - start));
            }
        }
        return lines;
    }

}
