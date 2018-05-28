package site.iway.mymusic.user.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import java.util.List;

import site.iway.androidhelpers.ExtendedEditText;
import site.iway.androidhelpers.ExtendedListView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ExtendedView;
import site.iway.androidhelpers.LoadingView;
import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.mymusic.models.ListSongsReq;
import site.iway.mymusic.net.mymusic.models.ListSongsRes;
import site.iway.mymusic.user.views.SongsAdapter;
import site.iway.mymusic.utils.Toaster;

/**
 * Created by iWay on 2017/12/25.
 */

public class ViewSongsActivity extends BaseActivity implements OnClickListener, RPCCallback {

    private ExtendedListView mListView;
    private ExtendedTextView mEmptyView;
    private LoadingView mLoadingView;
    private ExtendedEditText mSearchEditor;

    private SongsAdapter mSongsAdapter;

    private void setViews() {
        mListView = (ExtendedListView) findViewById(R.id.listView);
        mEmptyView = (ExtendedTextView) findViewById(R.id.emptyView);
        mLoadingView = (LoadingView) findViewById(R.id.loadingView);
        mSearchEditor = (ExtendedEditText) findViewById(R.id.searchEditor);
        mTitleBarBack.setOnClickListener(this);
        mTitleBarImage.setImageResource(R.drawable.icon_search);
        mTitleBarImage.setOnClickListener(this);

        if (WindowHelper.makeTranslucent(this, true, false)) {
            mTitlePad.setVisibility(View.VISIBLE);
            int statusBarHeight = WindowHelper.getStatusBarHeight(this);
            LayoutParams layoutParams = mTitlePad.getLayoutParams();
            layoutParams.height = statusBarHeight;
        }

        mSearchEditor.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick(mTitleBarImage);
                    return true;
                }
                return false;
            }
        });

        mSongsAdapter = new SongsAdapter(this);
        mListView.setAdapter(mSongsAdapter);
    }

    private ListSongsReq mListSongsReq;

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    private void showList(final List<String> fileNames, final List<String> playList, final String filter) {
        if (mSongsAdapter.isEmpty()) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(300);
            alphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mSongsAdapter.setData(fileNames);
                    mSongsAdapter.setPlayList(playList);
                    mSongsAdapter.setSearchFilter(filter);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mListView.startAnimation(alphaAnimation);
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(100);
            alphaAnimation.setRepeatMode(Animation.REVERSE);
            alphaAnimation.setRepeatCount(1);
            alphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    mSongsAdapter.setData(fileNames);
                    mSongsAdapter.setPlayList(playList);
                    mSongsAdapter.setSearchFilter(filter);
                    animation.setDuration(200);
                }
            });
            mListView.startAnimation(alphaAnimation);
        }
        if (fileNames.isEmpty()) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(300);
            if (mEmptyView.getVisibility() != View.VISIBLE) {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.startAnimation(alphaAnimation);
            }
        } else {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            alphaAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mEmptyView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (mEmptyView.getVisibility() != View.GONE) {
                mEmptyView.startAnimation(alphaAnimation);
            }
        }
    }

    private void loadFiles(String filter) {
        if (mListSongsReq != null) {
            mListSongsReq.cancel();
            mListSongsReq = null;
        }
        showLoading();
        mListSongsReq = new ListSongsReq();
        mListSongsReq.filter = filter;
        mListSongsReq.minDelayTime = 300;
        mListSongsReq.start(this);
    }

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mListSongsReq) {
            ListSongsReq listSongsReq = (ListSongsReq) req;
            ListSongsRes listSongsRes = (ListSongsRes) req.response;
            hideLoading();
            if (listSongsRes.resultCode == ListSongsRes.OK) {
                showList(listSongsRes.fileNames, listSongsRes.playList, listSongsReq.filter);
            } else {
                Toaster.show("搜索错误，请重试！");
            }
            mListSongsReq = null;
        }
    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        if (req == mListSongsReq) {
            hideLoading();
            Toaster.show("网络错误，请重试！");
            mListSongsReq = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_songs);
        setViews();
        loadFiles(null);
    }

    @Override
    public void onBackPressed() {
        if (mSongsAdapter.isSongsChanged()) {
            finish(RESULT_OK);
        } else {
            finish(RESULT_CANCELED);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mTitleBarBack) {
            onBackPressed();
        } else if (v == mTitleBarImage) {
            loadFiles(mSearchEditor.getText().toString());
        }
    }

}
