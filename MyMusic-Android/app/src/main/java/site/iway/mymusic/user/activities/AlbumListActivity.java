package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.javahelpers.StringHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.mymusic.GetSongInfoReq;
import site.iway.mymusic.net.mymusic.GetSongInfoRes;
import site.iway.mymusic.net.mymusic.SaveAlbumReq;
import site.iway.mymusic.net.mymusic.internal.SongInfo;
import site.iway.mymusic.user.dialogs.BaseDialog.OnUserActionListener;
import site.iway.mymusic.user.dialogs.DoubleActionDialog;
import site.iway.mymusic.user.views.AlbumAdapter;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.Song;

public class AlbumListActivity extends BaseActivity implements OnClickListener, OnItemClickListener, RPCCallback, OnItemLongClickListener {

    public static final String FILE_NAME = "FILE_NAME";

    private static final int INDEX_LOADING = 0;
    private static final int INDEX_LIST = 1;
    private static final int INDEX_EMPTY = 2;
    private static final int INDEX_ERROR = 3;

    private ViewSwapper mViewSwapper;
    private GridView mGridView;
    private ExtendedImageView mEmptyImage;
    private ExtendedTextView mEmptyText;

    private AlbumAdapter mAlbumAdapter;

    private void setViews() {
        mViewSwapper = (ViewSwapper) findViewById(R.id.viewSwapper);
        mGridView = (GridView) findViewById(R.id.gridView);
        mEmptyImage = (ExtendedImageView) findViewById(R.id.emptyImage);
        mEmptyText = (ExtendedTextView) findViewById(R.id.emptyText);

        mTitleBarBack.setOnClickListener(this);
        mTitleBarText.setText("所有专辑封面");

        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        mEmptyText.setText("没有专辑封面");

        mAlbumAdapter = new AlbumAdapter(this);
        mGridView.setAdapter(mAlbumAdapter);
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
        setContentView(R.layout.activity_album_list);
        setViews();
        loadData();
    }

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mGetSongInfoReq) {
            GetSongInfoRes getSongInfoRes = (GetSongInfoRes) req.response;
            if (getSongInfoRes.resultCode == GetSongInfoRes.OK) {
                Set<String> urls = new HashSet<>();
                if (getSongInfoRes.list != null) {
                    for (SongInfo songInfo : getSongInfoRes.list) {
                        String imageLink = songInfo.imgLink;
                        if (!StringHelper.nullOrEmpty(imageLink)) {
                            int lastIndexOfAt = imageLink.lastIndexOf('@');
                            if (lastIndexOfAt > -1) {
                                imageLink = imageLink.substring(0, lastIndexOfAt);
                            }
                        }
                        if (!StringHelper.nullOrEmpty(imageLink)) {
                            urls.add(imageLink);
                        }
                    }
                }
                mAlbumAdapter.setData(new ArrayList<>(urls));
                if (urls.isEmpty()) {
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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DoubleActionDialog dialog = new DoubleActionDialog(this);
        dialog.setActionLeftText("取消");
        dialog.setActionRightText("确定");
        dialog.setMessageText("设定为此专辑封面？");
        dialog.setOnUserActionListener(new OnUserActionListener() {
            @Override
            public void onUserAction(int action, Object data) {
                if (action == DoubleActionDialog.ACTION_RIGHT) {
                    disableUserInteract();
                    showLoadingView();
                    SaveAlbumReq saveAlbumReq = new SaveAlbumReq();
                    saveAlbumReq.fileName = mIntent.getStringExtra(FILE_NAME);
                    File cacheDir = getCacheDir();
                    File imageCacheDir = new File(cacheDir, Constants.DIR_NAME_IMAGE_CACHE);
                    String fileUrl = mAlbumAdapter.getItem(position);
                    String fileName = StringHelper.md5(fileUrl);
                    saveAlbumReq.file = new File(imageCacheDir, fileName);
                    saveAlbumReq.minDelayTime = 500;
                    saveAlbumReq.start(new RPCCallback() {
                        @Override
                        public void onRequestOK(RPCBaseReq req) {
                            hideLoadingView();
                            UIThread.event(Constants.EV_ALBUM_CHANGED);
                            enableUserInteract();
                            finish();
                        }

                        @Override
                        public void onRequestER(RPCBaseReq req) {
                            hideLoadingView();
                            simulateToast("网络错误，请重试！");
                            enableUserInteract();
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra(ViewImageActivity.TITLE, "查看图片");
        intent.putExtra(ViewImageActivity.IMAGE_URL, mAlbumAdapter.getItem(position));
        startActivity(intent);
        return true;
    }

}

