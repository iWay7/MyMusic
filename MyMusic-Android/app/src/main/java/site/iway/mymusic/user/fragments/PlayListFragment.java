package site.iway.mymusic.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import site.iway.androidhelpers.ExtendedImageView;
import site.iway.androidhelpers.ExtendedLinearLayout;
import site.iway.androidhelpers.ExtendedListView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.ExtendedView;
import site.iway.androidhelpers.UnitHelper;
import site.iway.androidhelpers.WindowHelper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.mymusic.MyMusicReq;
import site.iway.mymusic.net.mymusic.MyMusicRes;
import site.iway.mymusic.net.mymusic.PlayListReq;
import site.iway.mymusic.net.mymusic.PlayListRes;
import site.iway.mymusic.user.activities.ViewSongsActivity;
import site.iway.mymusic.user.dialogs.ActionDialog;
import site.iway.mymusic.user.dialogs.BaseDialog.OnUserActionListener;
import site.iway.mymusic.user.views.PlayListAdapter;
import site.iway.mymusic.utils.Constants;
import site.iway.mymusic.utils.PlayList;
import site.iway.mymusic.utils.PlayTask;
import site.iway.mymusic.utils.Player;

/**
 * Created by iWay on 2017/12/25.
 */

public class PlayListFragment extends PullRefreshFragment implements OnClickListener, OnItemLongClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_list, container, false);
    }

    private ExtendedView mTitleBarPad;
    private ExtendedTextView mTitleBarText;
    private ExtendedImageView mTitleBarImage;
    private ExtendedView mTitleBarSplitter;
    private ExtendedLinearLayout mPlayListAction;
    private ExtendedImageView mSelectAllImage;
    private ExtendedTextView mSelectAllText;
    private ExtendedTextView mDeleteSelectedItems;
    private ExtendedTextView mFinishActions;

    @Override
    protected void onSetFragmentView(View view) {
        super.onSetFragmentView(view);
        mTitleBarPad = (ExtendedView) mRootView.findViewById(R.id.titleBarPad);
        mTitleBarText = (ExtendedTextView) mRootView.findViewById(R.id.titleBarText);
        mTitleBarImage = (ExtendedImageView) mRootView.findViewById(R.id.titleBarImage);
        mTitleBarSplitter = (ExtendedView) mRootView.findViewById(R.id.titleBarSplitter);
        mPlayListAction = (ExtendedLinearLayout) mRootView.findViewById(R.id.playListAction);
        mSelectAllImage = (ExtendedImageView) mRootView.findViewById(R.id.selectAllImage);
        mSelectAllText = (ExtendedTextView) mRootView.findViewById(R.id.selectAllText);
        mDeleteSelectedItems = (ExtendedTextView) mRootView.findViewById(R.id.deleteSelectedItems);
        mFinishActions = (ExtendedTextView) mRootView.findViewById(R.id.finishActions);

        if (WindowHelper.makeTranslucent(mActivity, true, false)) {
            mTitleBarPad.setVisibility(View.VISIBLE);
            int statusBarHeight = WindowHelper.getStatusBarHeight(mActivity);
            LayoutParams layoutParams = mTitleBarPad.getLayoutParams();
            layoutParams.height = statusBarHeight;
        }
        mTitleBarText.setText(R.string.app_name);
        mTitleBarImage.setImageResource(R.drawable.icon_menu);
        mTitleBarImage.setOnClickListener(this);
        mSelectAllImage.setOnClickListener(this);
        mSelectAllText.setOnClickListener(this);
        mDeleteSelectedItems.setOnClickListener(this);
        mFinishActions.setOnClickListener(this);
    }

    @Override
    protected void onSetRootView(View view) {
        super.onSetRootView(view);
        view.setBackgroundColor(0xffffffff);
    }

    @Override
    protected void onSetListView(ExtendedListView listView) {
        super.onSetListView(listView);
        listView.setOnItemLongClickListener(this);
    }

    private View mFooterView;

    @Override
    protected void onSetFooterView(ExtendedListView listView) {
        super.onSetFooterView(listView);
        mFooterView = mLayoutInflater.inflate(R.layout.group_fav_list_footer, listView, false);
        listView.addFooterView(mFooterView);
    }

    @Override
    protected void onSetEmptyView(ViewGroup container, View imageView, View textView) {
        super.onSetEmptyView(container, imageView, textView);
        ((TextView) textView).setText("没有歌曲，点右上角添加");
    }

    @Override
    protected void onSetErrorView(ViewGroup container, View imageView, View textView) {
        super.onSetErrorView(container, imageView, textView);
        ((TextView) textView).setText("网络错误，点击刷新");
    }

    @Override
    protected boolean checkRPCResponse(Object data) {
        MyMusicRes myMusicRes = (MyMusicRes) data;
        return myMusicRes.resultCode == MyMusicRes.OK;
    }

    private PlayListAdapter mPlayListAdapter;

    @Override
    protected ListAdapter createAdapter() {
        mPlayListAdapter = new PlayListAdapter(mActivity);
        return mPlayListAdapter;
    }

    @Override
    protected MyMusicReq loadDataCreateRequest() {
        PlayListReq playListReq = new PlayListReq();
        playListReq.action = PlayListReq.ACTION_GET;
        playListReq.minDelayTime = 500;
        playListReq.cacheEnabled = true;
        return playListReq;
    }

    @Override
    protected void loadDataSetDataToAdapter(Object data) {
        PlayListRes playListRes = (PlayListRes) data;
        if (playListRes == null) {
            List<String> emptyList = new ArrayList<>();
            mPlayListAdapter.setData(emptyList);
        } else {
            mPlayListAdapter.setData(playListRes.fileNames);
        }
    }

    private void setSelectAllImage() {
        boolean allSelected = mPlayListAdapter.allSelected();
        mSelectAllImage.setImageResource(allSelected ? R.drawable.icon_song_select_selected : R.drawable.icon_song_select_normal);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        if (position < mPlayListAdapter.getCount()) {
            if (mPlayListAdapter.isInSelectionMode()) {
                ExtendedImageView itemSelection = (ExtendedImageView) view.findViewById(R.id.itemSelection);
                List<String> items = mPlayListAdapter.getSelectedItems();
                String item = mPlayListAdapter.getItem(position);
                if (items.contains(item)) {
                    mPlayListAdapter.removeSelectedItem(position);
                    itemSelection.setImageResource(R.drawable.icon_song_select_normal);
                } else {
                    mPlayListAdapter.addSelectedItem(position);
                    itemSelection.setImageResource(R.drawable.icon_song_select_selected);
                }
                setSelectAllImage();
            } else {
                String requestedItem = mPlayListAdapter.getItem(position);
                Player player = Player.getInstance();
                PlayTask playTask = player.getPlayTask();
                if (playTask == null) {
                    player.play(requestedItem);
                } else {
                    String musicFileName = playTask.getFileName();
                    if (!musicFileName.equals(mPlayListAdapter.getItem(position))) {
                        player.play(requestedItem);
                    }
                }
                mPlayListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPlayListAdapter.isInSelectionMode())
            return false;
        mPlayListAdapter.setInSelectionMode(true);
        setSelectAllImage();

        LayoutParams layoutParams = mFooterView.findViewById(R.id.footerPad).getLayoutParams();
        layoutParams.height = UnitHelper.dipToPxInt(112.5f);
        mFooterView.findViewById(R.id.footerPad).setLayoutParams(layoutParams);

        mPlayListAction.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.ABSOLUTE, 0
        );
        translateAnimation.setDuration(300);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        mPlayListAction.startAnimation(animationSet);
        return true;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        mPlayListAdapter.setFirstVisibleItem(view.getFirstVisiblePosition());
        mPlayListAdapter.setLastVisibleItem(view.getLastVisiblePosition());
    }

    @Override
    public void onEvent(String event, Object data) {
        super.onEvent(event, data);
        switch (event) {
            case Constants.EV_PLAYER_TASK_CHANGED:
                mPlayListAdapter.notifyDataSetChanged();
                break;
            case Constants.EV_REQUEST_REFRESH_PLAY_LIST:
            case Constants.EV_PLAY_LIST_SORT_TYPE_CHANGED:
                doRefresh();
                break;
        }
    }

    private static final int REQUEST_CODE_VIEW_SONGS = 0;

    @Override
    public void onClick(View v) {
        if (v == mTitleBarImage) {
            Intent intent = new Intent(mActivity, ViewSongsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_VIEW_SONGS);
        } else if (v == mSelectAllImage || v == mSelectAllText) {
            if (mPlayListAdapter.allSelected()) {
                mPlayListAdapter.selectAll(false);
            } else {
                mPlayListAdapter.selectAll(true);
            }
            setSelectAllImage();
        } else if (v == mDeleteSelectedItems) {
            List<String> selectedItems = mPlayListAdapter.getSelectedItems();
            final List<String> copiedSelectedItems = new ArrayList<>();
            copiedSelectedItems.addAll(selectedItems);
            if (!selectedItems.isEmpty()) {
                ActionDialog actionDialog = new ActionDialog(mActivity);
                actionDialog.setType(ActionDialog.TYPE_DOUBLE);
                actionDialog.setMessageText("确认删除所选歌曲吗？");
                actionDialog.setDescBottomText("一共选择了 " + selectedItems.size() + " 首歌曲");
                actionDialog.setActionLeftText("取消");
                actionDialog.setActionRightText("确定");
                actionDialog.setOnUserActionListener(new OnUserActionListener() {
                    @Override
                    public void onUserAction(int action, Object data) {
                        if (action == ActionDialog.ACTION_RIGHT) {
                            PlayListReq playListReq = new PlayListReq();
                            playListReq.action = PlayListReq.ACTION_REMOVE;
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String string : copiedSelectedItems) {
                                stringBuilder.append(';');
                                stringBuilder.append(string);
                            }
                            playListReq.fileNames = stringBuilder.substring(1);
                            playListReq.start();
                            Player player = Player.getInstance();
                            PlayList playList = player.getPlayList();
                            playList.removeAll(copiedSelectedItems);
                            mPlayListAdapter.removeItemsFromSelected();
                            onClick(mFinishActions);
                        }
                    }
                });
                actionDialog.show();
            }
        } else if (v == mFinishActions) {
            mPlayListAdapter.setInSelectionMode(false);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, 0,
                    Animation.ABSOLUTE, 0,
                    Animation.RELATIVE_TO_SELF, 1
            );
            translateAnimation.setDuration(300);
            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(translateAnimation);
            animationSet.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    LayoutParams layoutParams = mFooterView.findViewById(R.id.footerPad).getLayoutParams();
                    layoutParams.height = UnitHelper.dipToPxInt(80f);
                    mFooterView.findViewById(R.id.footerPad).setLayoutParams(layoutParams);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mPlayListAction.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mPlayListAction.startAnimation(animationSet);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_VIEW_SONGS:
                if (resultCode == ViewSongsActivity.RESULT_OK) {
                    doRefresh();
                }
                break;
        }
    }

}
