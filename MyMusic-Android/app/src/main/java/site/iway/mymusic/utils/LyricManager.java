package site.iway.mymusic.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import site.iway.mymusic.utils.WordWrapper.CharWidthMeasurer;


/**
 * Created by iWay on 2017/12/30.
 */

public class LyricManager {

    private static final Pattern mPattern = Pattern.compile("\\[[0-9]{2}\\:[0-9]{2}\\.[0-9]{2}\\]");

    public static class LyricLine implements Comparable<LyricLine> {

        public long millis = Long.MAX_VALUE;
        public List<String> sourceTextLines;
        public List<String> displayTextLines;
        public List<Rect> displayTextLineRects;
        public int minY;
        public int middleY;
        public int maxY;

        @Override
        public int compareTo(LyricLine another) {
            return (int) (millis - another.millis);
        }

        public String combineLineTexts() {
            StringBuilder stringBuilder = new StringBuilder();
            int size = sourceTextLines.size();
            for (int i = 0; i < size; i++) {
                stringBuilder.append('\n');
                stringBuilder.append(sourceTextLines.get(i));
            }
            return stringBuilder.substring(1);
        }

    }

    private final List<LyricLine> mLyricLines = new ArrayList<>();

    private long getMillis(String time) {
        String mm = time.substring(1, 3);
        String ss = time.substring(4, 6);
        String xx = time.substring(7, 9);
        int mmInt = Integer.parseInt(mm);
        int ssInt = Integer.parseInt(ss);
        int xxInt = Integer.parseInt(xx);
        return mmInt * 60 * 1000 + ssInt * 1000 + xxInt * 10;
    }

    private void addLine(long millis, String lineText) {
        int size = mLyricLines.size();
        LyricLine lyricLineToAdd = null;
        for (int i = 0; i < size; i++) {
            LyricLine lyricLine = mLyricLines.get(i);
            if (lyricLine.millis == millis) {
                lyricLineToAdd = mLyricLines.get(i);
            }
        }
        if (lyricLineToAdd == null) {
            lyricLineToAdd = new LyricLine();
            lyricLineToAdd.millis = millis;
            lyricLineToAdd.sourceTextLines = new ArrayList<>();
            lyricLineToAdd.sourceTextLines.add(lineText);
            mLyricLines.add(lyricLineToAdd);
        } else {
            lyricLineToAdd.sourceTextLines.add(lineText);
        }
    }

    public LyricManager(List<String> lyricLines) {
        mLyricLines.clear();
        for (String line : lyricLines) {
            Matcher matcher = mPattern.matcher(line);
            List<String> finds = new ArrayList<>();
            int lastEnd = 0;
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                lastEnd = Math.max(lastEnd, end);
                finds.add(line.substring(start, end));
            }
            int count = finds.size();
            if (count > 0) {
                String text = line.substring(lastEnd);
                for (int i = 0; i < count; i++) {
                    long millis = getMillis(finds.get(i));
                    addLine(millis, text);
                }
            }
        }
        Collections.sort(mLyricLines);
    }

    public LyricLine getCurrentLine(int songPosition) {
        int size = mLyricLines.size();
        for (int i = size - 1; i >= 0; i--) {
            LyricLine lyricLine = mLyricLines.get(i);
            if (lyricLine.millis < songPosition) {
                return lyricLine;
            }
        }
        return null;
    }

    private Paint mPaint;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private int mTextLineSpacing;
    private int mLineSpacing;
    private int mNormalColor;
    private int mHighLightColor;
    private LyricLine mCurrentLine;

    public void setPaint(Paint paint) {
        mPaint = paint;
    }

    public void setDisplayWidth(int displayWidth) {
        mDisplayWidth = displayWidth;
    }

    public void setDisplayHeight(int displayHeight) {
        mDisplayHeight = displayHeight;
    }

    public void setTextLineSpacing(int textLineSpacing) {
        mTextLineSpacing = textLineSpacing;
    }

    public void setLineSpacing(int lineSpacing) {
        mLineSpacing = lineSpacing;
    }

    public void setNormalColor(int normalColor) {
        mNormalColor = normalColor;
    }

    public void setHighLightColor(int highLightColor) {
        mHighLightColor = highLightColor;
    }

    public void setCurrentLine(LyricLine currentLine) {
        mCurrentLine = currentLine;
    }

    private CharWidthMeasurer mCharWidthMeasurer = new CharWidthMeasurer() {
        private char[] mChars = new char[1];

        @Override
        public float measureCharWidth(char c) {
            mChars[0] = c;
            return mPaint.measureText(mChars, 0, 1);
        }
    };

    public void computePositions() {
        if (mDisplayWidth >= 0 && mDisplayHeight > 0) {
            int size = mLyricLines.size();
            for (int i = 0; i < size; i++) {
                LyricLine lyricLine = mLyricLines.get(i);
                lyricLine.displayTextLines = new ArrayList<>();
                for (int j = 0; j < lyricLine.sourceTextLines.size(); j++) {
                    String text = lyricLine.sourceTextLines.get(j);
                    List<String> newTextLines = WordWrapper.wordWrap(text, mDisplayWidth, mCharWidthMeasurer, ' ');
                    if (newTextLines.isEmpty()) {
                        lyricLine.displayTextLines.add("");
                    } else {
                        lyricLine.displayTextLines.addAll(newTextLines);
                    }
                }
            }
            int lastBottom = 0;
            for (int i = 0; i < size; i++) {
                LyricLine lyricLine = mLyricLines.get(i);
                lyricLine.displayTextLineRects = new ArrayList<>();
                for (int j = 0; j < lyricLine.displayTextLines.size(); j++) {
                    String text = lyricLine.displayTextLines.get(j);
                    Rect textRect = new Rect();
                    mPaint.getTextBounds(text, 0, text.length(), textRect);
                    int textWidth = textRect.width();
                    textRect.left = (mDisplayWidth - textWidth) / 2;
                    textRect.right = textRect.left + textWidth;
                    int textHeight = textRect.height();
                    if (i == 0 && j == 0) {
                        lastBottom = (mDisplayHeight - textHeight) / 2;
                    }
                    if (i != 0 && j == 0) {
                        lastBottom += mLineSpacing;
                    }
                    textRect.top = lastBottom;
                    textRect.bottom = textRect.top + textHeight;
                    lyricLine.displayTextLineRects.add(textRect);
                    lastBottom = textRect.bottom + mTextLineSpacing;
                }
                int lineTop = lyricLine.displayTextLineRects.get(0).top;
                int lineBottom = lyricLine.displayTextLineRects.get(lyricLine.displayTextLineRects.size() - 1).bottom;
                lyricLine.minY = lineTop;
                lyricLine.middleY = (lineTop + lineBottom) / 2;
                lyricLine.maxY = lineBottom;
            }
        }
    }

    public void drawNormal(Canvas canvas) {
        mPaint.setColor(mNormalColor);
        int size = mLyricLines.size();
        for (int i = 0; i < size; i++) {
            LyricLine lyricLine = mLyricLines.get(i);
            if (lyricLine != mCurrentLine) {
                for (int j = 0; j < lyricLine.displayTextLines.size(); j++) {
                    String text = lyricLine.displayTextLines.get(j);
                    Rect textRect = lyricLine.displayTextLineRects.get(j);
                    canvas.drawText(text, textRect.left, textRect.top, mPaint);
                }
            }
        }
    }

    public void drawHighLight(Canvas canvas) {
        mPaint.setColor(mHighLightColor);
        if (mCurrentLine != null) {
            for (int j = 0; j < mCurrentLine.displayTextLines.size(); j++) {
                String text = mCurrentLine.displayTextLines.get(j);
                Rect textRect = mCurrentLine.displayTextLineRects.get(j);
                canvas.drawText(text, textRect.left, textRect.top, mPaint);
            }
        }
    }

}
