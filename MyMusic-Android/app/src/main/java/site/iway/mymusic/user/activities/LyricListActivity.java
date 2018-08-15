package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedListView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.mymusic.GetSongInfoReq;
import site.iway.mymusic.net.mymusic.GetSongInfoRes;
import site.iway.mymusic.net.mymusic.internal.SongInfo;
import site.iway.mymusic.user.views.LyricsAdapter;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.Lyric;
import site.iway.mymusic.utils.Song;

public class LyricListActivity extends BaseActivity implements OnClickListener, RPCCallback, OnItemClickListener {

    public static final String FILE_NAME = "FILE_NAME";

    private static final int INDEX_LOADING = 0;
    private static final int INDEX_LIST = 1;
    private static final int INDEX_EMPTY = 2;
    private static final int INDEX_ERROR = 3;

    private ViewSwapper mViewSwapper;
    private ExtendedListView mListView;
    private ExtendedImageView mEmptyImage;
    private ExtendedTextView mEmptyText;

    private void setViews() {
        mViewSwapper = (ViewSwapper) findViewById(R.id.viewSwapper);
        mListView = (ExtendedListView) findViewById(R.id.listView);
        mEmptyImage = (ExtendedImageView) findViewById(R.id.emptyImage);
        mEmptyText = (ExtendedTextView) findViewById(R.id.emptyText);

        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText("所有歌词");

        mListView.setOnItemClickListener(this);

        mEmptyImage.setImageResource(R.drawable.icon_add_big);
        mEmptyImage.setOnClickListener(this);
        mEmptyText.setText("没有歌词，点击添加");
    }

    private GetSongInfoReq mGetSongInfoReq;

    private void loadData() {
        String fileName = mIntent.getStringExtra(FILE_NAME);
        Song song = new Song(fileName);
        mGetSongInfoReq = new GetSongInfoReq();
        mGetSongInfoReq.minDelayTime = 300;
        mGetSongInfoReq.query = song.artist + " " + song.name;
        mGetSongInfoReq.fileName = fileName;
        mGetSongInfoReq.tag = song;
        mGetSongInfoReq.cacheEnabled = true;
        mGetSongInfoReq.start(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_list);
        setViews();
        loadData();
    }

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mGetSongInfoReq) {
            GetSongInfoRes getSongInfoRes = (GetSongInfoRes) req.response;
            if (getSongInfoRes.resultCode == GetSongInfoRes.OK) {
                List<Lyric> lyrics = new ArrayList<>();
                if (getSongInfoRes.list != null) {
                    for (SongInfo songInfo : getSongInfoRes.list) {
                        if (!TextUtils.isEmpty(songInfo.lrcTitle) && !TextUtils.isEmpty(songInfo.lrcLink)) {
                            Lyric lyric = new Lyric(songInfo.lrcTitle, songInfo.lrcLink);
                            lyrics.add(lyric);
                        }
                    }
                }
                LyricsAdapter lyricsAdapter = new LyricsAdapter(this);
                lyricsAdapter.setData(lyrics);
                mListView.setAdapter(lyricsAdapter);
                if (lyrics.isEmpty()) {
                    mViewSwapper.setDisplayedChild(INDEX_EMPTY);
                } else {
                    mViewSwapper.setDisplayedChild(INDEX_LIST);
                }
            } else {
                mViewSwapper.setDisplayedChild(INDEX_ERROR);
            }
        }
    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        if (req == mGetSongInfoReq) {
            mViewSwapper.setDisplayedChild(INDEX_ERROR);
        }
    }

    private static final int REQUEST_ADD_LYRIC = 0;

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mEmptyImage) {
            Intent intent = new Intent(this, EditLyricActivity.class);
            intent.putExtra(ViewLyricActivity.SONG_FILE_NAME, mIntent.getStringExtra(FILE_NAME));
            startActivityForResult(intent, REQUEST_ADD_LYRIC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_LYRIC && resultCode == EditLyricActivity.RESULT_OK) {
            UIThread.event(Constants.EV_LYRIC_CHANGED);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LyricsAdapter lyricsAdapter = (LyricsAdapter) parent.getAdapter();
        Intent intent = new Intent(this, ViewLyricActivity.class);
        intent.putExtra(ViewLyricActivity.SONG_FILE_NAME, mIntent.getStringExtra(FILE_NAME));
        intent.putExtra(ViewLyricActivity.SONG_LYRIC_URL, lyricsAdapter.getItem(position).url);
        startActivity(intent);
    }

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_LYRIC_CHANGED:
                mViewSwapper.setDisplayedChild(INDEX_LOADING);
                loadData();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
