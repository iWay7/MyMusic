package site.iway.mymusic.user.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import site.iway.helpers.EventPoster;
import site.iway.helpers.EventPoster.EventListener;
import site.iway.mymusic.user.activities.BaseActivity;

/**
 * Created by iWay on 8/4/15.
 */
public abstract class BaseFragment extends Fragment implements EventListener {

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
        EventPoster.register(this);
        mRootView = view;
    }

    @Override
    public void onEvent(int event, Object data) {
        // nothing
    }

    @Override
    public void onDestroyView() {
        EventPoster.unregister(this);
        super.onDestroyView();
    }

}
