package site.iway.mymusic.user.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import site.iway.androidhelpers.UIThread;
import site.iway.androidhelpers.UIThread.UIEventHandler;
import site.iway.mymusic.user.activities.BaseActivity;

/**
 * Created by iWay on 8/4/15.
 */
public abstract class BaseFragment extends Fragment implements UIEventHandler {

    protected BaseActivity mActivity;
    protected Resources mResources;
    protected Handler mHandler;
    protected View mRootView;
    protected LayoutInflater mLayoutInflater;
    protected Intent mIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        mResources = mActivity.getResources();
        mIntent = mActivity.getIntent();
        mHandler = new Handler();
        mLayoutInflater = mActivity.getLayoutInflater();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UIThread.register(this);
        mRootView = view;
    }

    @Override
    public void onEvent(String event, Object data) {
        // nothing
    }

    @Override
    public void onDestroyView() {
        UIThread.unregister(this);
        super.onDestroyView();
    }

}
