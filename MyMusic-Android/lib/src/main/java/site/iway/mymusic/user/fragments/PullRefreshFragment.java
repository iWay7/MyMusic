package site.iway.mymusic.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import site.iway.androidhelpers.ExtendedListView;
import site.iway.androidhelpers.ExtendedTextView;
import site.iway.androidhelpers.PullRefreshLayout;
import site.iway.androidhelpers.PullRefreshLayout.OnRefreshListener;
import site.iway.androidhelpers.ViewSwapper;
import site.iway.mymusic.R;
import site.iway.mymusic.net.RPCBaseReq;
import site.iway.mymusic.net.RPCCallback;
import site.iway.mymusic.net.req.MMReq;
import site.iway.mymusic.net.res.MMRes;

/**
 * Created by iWay on 2015/12/9.
 */
public abstract class PullRefreshFragment extends BaseFragment implements OnRefreshListener, RPCCallback, OnItemClickListener, OnScrollListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pull_refresh, container, false);
    }

    protected ViewSwapper mPullRefreshRoot;
    protected PullRefreshLayout mPullRefreshLayout;
    protected ExtendedListView mPullRefreshListView;
    protected ViewSwapper mEmptyOrErrorView;
    protected ListAdapter mListAdapter;

    protected abstract ListAdapter createAdapterForListView();

    protected abstract MMReq createLoadAdapterDataRPCRequest();

    protected boolean willLoadDataAfterViewCreated() {
        return true;
    }

    protected RPCBaseReq mRPCLoadData;

    protected void loadAdapterDataByRPCRequest() {
        mRPCLoadData = createLoadAdapterDataRPCRequest();
        mRPCLoadData.start(this);
    }

    protected abstract void setAdapterDataFromRPCResponse(Object data);

    public void doRefresh(boolean setToTop) {
        if (setToTop) {
            mPullRefreshListView.setSelection(0);
        }
        mPullRefreshLayout.setRefreshing(true);
        loadAdapterDataByRPCRequest();
    }

    public void doRefresh() {
        doRefresh(true);
    }

    protected void onSetFragmentView(View view) {
        // nothing
    }

    protected void onSetRootView(View view) {
        // nothing
    }

    protected void onSetListView(ExtendedListView listView) {
        // nothing
    }

    protected void onSetHeaderView(ExtendedListView listView) {
        // nothing
    }

    protected void onSetFooterView(ExtendedListView listView) {
        // nothing
    }

    protected void onSetLoadingView(ViewGroup container, View progressView, View textView) {
        // nothing
    }

    protected void setLoadingView() {
        ViewGroup loadingViewContainer = (ViewGroup) mPullRefreshRoot.getChildAt(0);
        View progressView = loadingViewContainer.findViewById(R.id.loadingProgress);
        View textView = loadingViewContainer.findViewById(R.id.loadingText);
        onSetLoadingView(loadingViewContainer, progressView, textView);
    }

    protected void onSetEmptyView(ViewGroup container, View imageView, View textView) {
        // nothing
    }

    protected void setEmptyView() {
        ViewGroup emptyViewContainer = (ViewGroup) mEmptyOrErrorView.getChildAt(0);
        View imageView = emptyViewContainer.findViewById(R.id.emptyImage);
        View textView = emptyViewContainer.findViewById(R.id.emptyText);
        onSetEmptyView(emptyViewContainer, imageView, textView);
    }

    protected void onSetErrorView(ViewGroup container, View imageView, View textView) {
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doRefresh();
            }
        });
    }

    protected void setErrorView() {
        ViewGroup errorViewContainer = (ViewGroup) mEmptyOrErrorView.getChildAt(1);
        View imageView = errorViewContainer.findViewById(R.id.errorImage);
        View textView = errorViewContainer.findViewById(R.id.errorText);
        onSetErrorView(errorViewContainer, imageView, textView);
    }

    protected void onSetErrorViewNetwork(ViewGroup container, View imageView, View textView) {
        onSetErrorView(container, imageView, textView);
    }

    protected void setErrorViewNetwork() {
        ViewGroup errorViewContainer = (ViewGroup) mEmptyOrErrorView.getChildAt(1);
        View imageView = errorViewContainer.findViewById(R.id.errorImage);
        View textView = errorViewContainer.findViewById(R.id.errorText);
        onSetErrorViewNetwork(errorViewContainer, imageView, textView);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onSetFragmentView(mRootView);
        mPullRefreshRoot = mRootView.findViewById(R.id.pullRefreshRoot);
        onSetRootView(mPullRefreshRoot);
        mPullRefreshLayout = (PullRefreshLayout) mRootView.findViewById(R.id.pullRefreshLayout);
        mPullRefreshListView = (ExtendedListView) mRootView.findViewById(R.id.pullRefreshListView);
        mEmptyOrErrorView = (ViewSwapper) mRootView.findViewById(R.id.emptyOrErrorView);
        mPullRefreshLayout.setOnRefreshListener(this);
        mListAdapter = createAdapterForListView();
        onSetListView(mPullRefreshListView);
        onSetHeaderView(mPullRefreshListView);
        onSetFooterView(mPullRefreshListView);
        setLoadingView();
        if (loadMoreIsEnabled()) {
            mLoadMoreView = mLayoutInflater.inflate(R.layout.group_load_more, null);
            loadMoreInitialize();
            mPullRefreshListView.addFooterView(mLoadMoreView, null, false);
            mPullRefreshListView.setOnScrollListener(this);
        }
        mPullRefreshListView.setAdapter(mListAdapter);
        mPullRefreshListView.setOnItemClickListener(this);
        mPullRefreshListView.setEmptyView(mEmptyOrErrorView);
        if (willLoadDataAfterViewCreated()) {
            loadAdapterDataByRPCRequest();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            loadMoreDo(true);
        }
    }

    protected void onLoadFinish() {
        mPullRefreshLayout.setRefreshing(false);
        mPullRefreshRoot.setDisplayedChild(1);
    }

    protected void setEmptyViewAsEmpty() {
        setEmptyView();
        mEmptyOrErrorView.setDisplayedChild(0);
    }

    protected void setEmptyViewAsError() {
        setErrorView();
        mEmptyOrErrorView.setDisplayedChild(1);
    }

    protected void setEmptyViewAsErrorNetwork() {
        setErrorViewNetwork();
        mEmptyOrErrorView.setDisplayedChild(1);
    }

    @Override
    public void onRefresh(PullRefreshLayout pullRefreshLayout) {
        if (mRPCLoadData != null)
            mRPCLoadData.cancel();
        if (loadMoreIsEnabled())
            loadMoreCancel();
        loadAdapterDataByRPCRequest();
    }

    private void onLoadAdapterDataRPCOK(RPCBaseReq req) {
        onLoadFinish();
        MMRes res = (MMRes) req.response;
        if (res.resultCode == MMRes.OK) {
            setEmptyViewAsEmpty();
            setAdapterDataFromRPCResponse(res);
            if (loadMoreIsEnabled()) {
                if (!loadMoreHasMoreData(req)) {
                    loadMoreNoMore();
                } else {
                    mCanLoadMore = true;
                }
            }
        } else {
            setEmptyViewAsError();
        }
    }

    private void onLoadAdapterDataRPCER(RPCBaseReq req) {
        onLoadFinish();
        setEmptyViewAsErrorNetwork();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // nothing
    }

    protected ViewSwapper mLoadMorePageFlipper;
    protected ExtendedTextView mLoadMoreLoadingView;
    protected ExtendedTextView mLoadMoreErrorView;
    protected ExtendedTextView mLoadMoreEmptyView;

    protected View mLoadMoreView;

    protected boolean loadMoreIsEnabled() {
        return false;
    }

    protected void loadMoreInitialize() {
        mLoadMorePageFlipper = (ViewSwapper) mLoadMoreView.findViewById(R.id.pageFlipper);
        mLoadMoreLoadingView = (ExtendedTextView) mLoadMoreView.findViewById(R.id.loadingView);
        mLoadMoreErrorView = (ExtendedTextView) mLoadMoreView.findViewById(R.id.errorView);
        mLoadMoreErrorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRPCLoadMore = null;
                loadMoreDo(false);
            }
        });
        mLoadMoreEmptyView = (ExtendedTextView) mLoadMoreView.findViewById(R.id.emptyView);
    }

    protected int mLoadMorePage = 1;
    protected RPCBaseReq mRPCLoadMore;
    protected boolean mCanLoadMore;

    protected MMReq loadMoreCreateRequest(int pageIndexFromOne) {
        throw new RuntimeException("If load more is enabled, this method should be implemented");
    }

    protected boolean loadMoreHasMoreData(RPCBaseReq req) {
        throw new RuntimeException("If load more is enabled, this method should be implemented");
    }

    protected void loadMoreAddDataToAdapter(RPCBaseReq req) {
        throw new RuntimeException("If load more is enabled, this method should be implemented");
    }

    protected void loadMoreDo(boolean nextPage) {
        if (mRPCLoadMore != null) {
            return;
        }
        if (mCanLoadMore == false) {
            return;
        }
        loadMoreLoading();
        mLoadMorePage += nextPage ? 1 : 0;
        mRPCLoadMore = loadMoreCreateRequest(mLoadMorePage);
        mRPCLoadMore.start(this);
    }

    protected void loadMoreCancel() {
        mLoadMorePage = 1;
        if (mRPCLoadMore != null) {
            mRPCLoadMore.cancel();
            mRPCLoadMore = null;
        }
        mRPCLoadMore = null;
        mCanLoadMore = false;
    }

    protected void loadMoreLoading() {
        mLoadMorePageFlipper.setDisplayedChild(0);
    }

    protected void loadMoreNoMore() {
        mLoadMorePageFlipper.setDisplayedChild(2);
        mCanLoadMore = false;
    }

    protected void loadMoreError() {
        mLoadMorePageFlipper.setDisplayedChild(1);
    }

    protected void loadMoreOnRPCOK(RPCBaseReq req) {
        MMRes basicRes = (MMRes) req.response;
        if (basicRes.resultCode == MMRes.OK) {
            if (!loadMoreHasMoreData(req)) {
                loadMoreNoMore();
                mCanLoadMore = false;
            }
            loadMoreAddDataToAdapter(req);
            mRPCLoadMore = null;
        } else {
            loadMoreError();
        }
    }

    protected void loadMoreOnRPCER(RPCBaseReq req) {
        loadMoreError();
    }

    @Override
    public void onRequestOK(RPCBaseReq req) {
        if (req == mRPCLoadData)
            onLoadAdapterDataRPCOK(req);
        if (req == mRPCLoadMore)
            loadMoreOnRPCOK(req);
    }

    @Override
    public void onRequestER(RPCBaseReq req) {
        if (req == mRPCLoadData)
            onLoadAdapterDataRPCER(req);
        if (req == mRPCLoadMore)
            loadMoreOnRPCER(req);
    }

}
