package site.iway.mymusic.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.text.DecimalFormat;

import site.iway.androidhelpers.UIThread;
import site.iway.javahelpers.FolderScanner;
import site.iway.mymusic.R;
import site.iway.mymusic.user.dialogs.BaseDialog.OnUserActionListener;
import site.iway.mymusic.user.dialogs.DoubleActionDialog;
import site.iway.mymusic.user.dialogs.ListActionDialog;
import site.iway.mymusic.user.views.ListActionItem;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.Settings;

public class SettingsActivity extends BaseActivity implements OnClickListener {

    private ListActionItem mGoImageCache;
    private ListActionItem mGoSongCache;
    private ListActionItem mGoLyricCache;
    private ListActionItem mGoNetworkCache;
    private ListActionItem mGoPlayListSortType;
    private ListActionItem mGoSearchSortType;
    private ListActionItem mGoAbout;

    private void setPlayListSortType() {
        switch (Settings.getPlayListSortType()) {
            case Settings.SORT_BY_ADD_TIME:
                mGoPlayListSortType.setDesc(SORT_BY_ADD_TIME);
                break;
            case Settings.SORT_BY_ARTIST_NAME:
                mGoPlayListSortType.setDesc(SORT_BY_ARTIST_NAME);
                break;
            case Settings.SORT_BY_SONG_NAME:
                mGoPlayListSortType.setDesc(SORT_BY_SONG_NAME);
                break;
        }
    }

    private void setSearchSortType() {
        switch (Settings.getSearchSortType()) {
            case Settings.SORT_BY_ADD_TIME:
                mGoSearchSortType.setDesc(SORT_BY_ADD_TIME);
                break;
            case Settings.SORT_BY_ARTIST_NAME:
                mGoSearchSortType.setDesc(SORT_BY_ARTIST_NAME);
                break;
            case Settings.SORT_BY_SONG_NAME:
                mGoSearchSortType.setDesc(SORT_BY_SONG_NAME);
                break;
        }
    }

    private String getFileSize(double fileSize) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        double p = 1024d * 1024d * 1024d * 1024d * 1024d;
        if (fileSize >= p) {
            return decimalFormat.format(fileSize / p) + " P";
        }
        double t = 1024d * 1024d * 1024d * 1024d;
        if (fileSize >= t) {
            return decimalFormat.format(fileSize / t) + " T";
        }
        double g = 1024d * 1024d * 1024d;
        if (fileSize >= g) {
            return decimalFormat.format(fileSize / g) + " G";
        }
        double m = 1024d * 1024d;
        if (fileSize >= m) {
            return decimalFormat.format(fileSize / m) + " M";
        }
        double k = 1024d;
        if (fileSize >= k) {
            return decimalFormat.format(fileSize / k) + " K";
        }
        return decimalFormat.format(fileSize) + " B";
    }

    private OnClickListener mNoNeedCleanClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showToastView("没有缓存，无需清理~");
        }
    };

    private FolderScanner mImageScanner = new FolderScanner() {
        private long mFileSize;

        @Override
        protected void onDetectFile(File file) {
            mFileSize += file.length();
        }

        @Override
        protected void onCompleted(File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        mGoImageCache.setDesc(getFileSize(mFileSize));
                        if (mFileSize == 0)
                            mGoImageCache.setOnClickListener(mNoNeedCleanClickListener);
                        else
                            mGoImageCache.setOnClickListener(SettingsActivity.this);
                    }
                }
            });
        }
    };

    private FolderScanner mMP3Scanner = new FolderScanner() {
        private long mFileSize;

        @Override
        protected void onDetectFile(File file) {
            mFileSize += file.length();
        }

        @Override
        protected void onCompleted(File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        mGoSongCache.setDesc(getFileSize(mFileSize));
                        if (mFileSize == 0)
                            mGoSongCache.setOnClickListener(mNoNeedCleanClickListener);
                        else
                            mGoSongCache.setOnClickListener(SettingsActivity.this);
                    }
                }
            });
        }
    };

    private FolderScanner mLyricScanner = new FolderScanner() {
        private long mFileSize;

        @Override
        protected void onDetectFile(File file) {
            mFileSize += file.length();
        }

        @Override
        protected void onCompleted(File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        mGoLyricCache.setDesc(getFileSize(mFileSize));
                        if (mFileSize == 0)
                            mGoLyricCache.setOnClickListener(mNoNeedCleanClickListener);
                        else
                            mGoLyricCache.setOnClickListener(SettingsActivity.this);
                    }
                }
            });
        }
    };

    private FolderScanner mObjectsScanner = new FolderScanner() {
        private long mFileSize;

        @Override
        protected void onDetectFile(File file) {
            mFileSize += file.length();
        }

        @Override
        protected void onCompleted(File file) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        mGoNetworkCache.setDesc(getFileSize(mFileSize));
                        if (mFileSize == 0)
                            mGoNetworkCache.setOnClickListener(mNoNeedCleanClickListener);
                        else
                            mGoNetworkCache.setOnClickListener(SettingsActivity.this);
                    }
                }
            });
        }
    };

    private void setViews() {
        mGoImageCache = (ListActionItem) findViewById(R.id.goImageCache);
        mGoSongCache = (ListActionItem) findViewById(R.id.goSongCache);
        mGoLyricCache = (ListActionItem) findViewById(R.id.goLyricCache);
        mGoNetworkCache = (ListActionItem) findViewById(R.id.goNetworkCache);
        mGoPlayListSortType = (ListActionItem) findViewById(R.id.goPlayListSortType);
        mGoSearchSortType = (ListActionItem) findViewById(R.id.goSearchSortType);
        mGoAbout = (ListActionItem) findViewById(R.id.goAbout);
        mImageScanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_IMAGE_CACHE));
        mImageScanner.start();
        mMP3Scanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_MUSIC_CACHE));
        mMP3Scanner.start();
        mLyricScanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_LYRIC_CACHE));
        mLyricScanner.start();
        mObjectsScanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_OBJECT_CACHE));
        mObjectsScanner.start();
        mTitleBarText.setText("设置");
        mTitleBarBack.setOnClickListener(this);
        mGoSearchSortType.setOnClickListener(this);
        mGoPlayListSortType.setOnClickListener(this);
        setPlayListSortType();
        setSearchSortType();
        mGoAbout.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setViews();
    }

    private static final String SORT_BY_ADD_TIME = "按添加时间排序";
    private static final String SORT_BY_ARTIST_NAME = "按歌手名排序";
    private static final String SORT_BY_SONG_NAME = "按歌曲名排序";

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mGoImageCache) {
            DoubleActionDialog dialog = new DoubleActionDialog(this);
            dialog.setMessageText("确定要清理图片缓存吗？");
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (action == DoubleActionDialog.ACTION_RIGHT) {
                        disableUserInteract();
                        showLoadingView();
                        FolderScanner folderScanner = new FolderScanner() {
                            @Override
                            protected void onDetectFile(File file) {
                                file.delete();
                            }

                            @Override
                            protected void onAllCompleted() {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingView();
                                        enableUserInteract();
                                        mGoImageCache.setDesc("0 B");
                                        mGoImageCache.setOnClickListener(mNoNeedCleanClickListener);
                                    }
                                }, 500);
                            }
                        };
                        folderScanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_IMAGE_CACHE));
                        folderScanner.start();
                    }
                }
            });
            dialog.setActionLeftText("取消");
            dialog.setActionRightText("确定");
            dialog.show();
        } else if (v == mGoSongCache) {
            DoubleActionDialog dialog = new DoubleActionDialog(this);
            dialog.setMessageText("确定要清理音乐缓存吗？");
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (action == DoubleActionDialog.ACTION_RIGHT) {
                        disableUserInteract();
                        showLoadingView();
                        FolderScanner folderScanner = new FolderScanner() {
                            @Override
                            protected void onDetectFile(File file) {
                                file.delete();
                            }

                            @Override
                            protected void onAllCompleted() {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingView();
                                        enableUserInteract();
                                        mGoSongCache.setDesc("0 B");
                                        mGoSongCache.setOnClickListener(mNoNeedCleanClickListener);
                                    }
                                }, 500);
                            }
                        };
                        folderScanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_MUSIC_CACHE));
                        folderScanner.start();
                    }
                }
            });
            dialog.setActionLeftText("取消");
            dialog.setActionRightText("确定");
            dialog.show();
        } else if (v == mGoLyricCache) {
            DoubleActionDialog dialog = new DoubleActionDialog(this);
            dialog.setMessageText("确定要清理歌词缓存吗？");
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (action == DoubleActionDialog.ACTION_RIGHT) {
                        disableUserInteract();
                        showLoadingView();
                        FolderScanner folderScanner = new FolderScanner() {
                            @Override
                            protected void onDetectFile(File file) {
                                file.delete();
                            }

                            @Override
                            protected void onAllCompleted() {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingView();
                                        enableUserInteract();
                                        mGoLyricCache.setDesc("0 B");
                                        mGoLyricCache.setOnClickListener(mNoNeedCleanClickListener);
                                    }
                                }, 300);
                            }
                        };
                        folderScanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_LYRIC_CACHE));
                        folderScanner.start();
                    }
                }
            });
            dialog.setActionLeftText("取消");
            dialog.setActionRightText("确定");
            dialog.show();
        } else if (v == mGoNetworkCache) {
            DoubleActionDialog dialog = new DoubleActionDialog(this);
            dialog.setMessageText("确定要清理网络缓存吗？");
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (action == DoubleActionDialog.ACTION_RIGHT) {
                        disableUserInteract();
                        showLoadingView();
                        FolderScanner folderScanner = new FolderScanner() {
                            @Override
                            protected void onDetectFile(File file) {
                                file.delete();
                            }

                            @Override
                            protected void onAllCompleted() {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingView();
                                        enableUserInteract();
                                        mGoNetworkCache.setDesc("0 B");
                                        mGoNetworkCache.setOnClickListener(mNoNeedCleanClickListener);
                                    }
                                }, 300);
                            }
                        };
                        folderScanner.addFolders(new File(getCacheDir(), Constants.DIR_NAME_OBJECT_CACHE));
                        folderScanner.start();
                    }
                }
            });
            dialog.setActionLeftText("取消");
            dialog.setActionRightText("确定");
            dialog.show();
        } else if (v == mGoPlayListSortType) {
            ListActionDialog dialog = new ListActionDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setActions(SORT_BY_ADD_TIME, SORT_BY_ARTIST_NAME, SORT_BY_SONG_NAME);
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (data == null) {
                        return;
                    }
                    switch ((String) data) {
                        case SORT_BY_ADD_TIME:
                            Settings.setPlayListSortType(Settings.SORT_BY_ADD_TIME);
                            break;
                        case SORT_BY_ARTIST_NAME:
                            Settings.setPlayListSortType(Settings.SORT_BY_ARTIST_NAME);
                            break;
                        case SORT_BY_SONG_NAME:
                            Settings.setPlayListSortType(Settings.SORT_BY_SONG_NAME);
                            break;
                    }
                    UIThread.event(Constants.EV_PLAY_LIST_SORT_TYPE_CHANGED);
                }
            });
            dialog.show();
        } else if (v == mGoSearchSortType) {
            ListActionDialog dialog = new ListActionDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setActions(SORT_BY_ADD_TIME, SORT_BY_ARTIST_NAME, SORT_BY_SONG_NAME);
            dialog.setOnUserActionListener(new OnUserActionListener() {
                @Override
                public void onUserAction(int action, Object data) {
                    if (data == null) {
                        return;
                    }
                    switch ((String) data) {
                        case SORT_BY_ADD_TIME:
                            Settings.setSearchSortType(Settings.SORT_BY_ADD_TIME);
                            break;
                        case SORT_BY_ARTIST_NAME:
                            Settings.setSearchSortType(Settings.SORT_BY_ARTIST_NAME);
                            break;
                        case SORT_BY_SONG_NAME:
                            Settings.setSearchSortType(Settings.SORT_BY_SONG_NAME);
                            break;
                    }
                    UIThread.event(Constants.EV_SEARCH_SORT_TYPE_CHANGED);
                }
            });
            dialog.show();
        } else if (v == mGoAbout) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_PLAY_LIST_SORT_TYPE_CHANGED:
                setPlayListSortType();
                break;
            case Constants.EV_SEARCH_SORT_TYPE_CHANGED:
                setSearchSortType();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
