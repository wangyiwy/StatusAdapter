package me.wy.statusadapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * author: WangYi
 * created on: 2018/1/20 14:44
 * description:
 */

public abstract class OnScrollListener extends RecyclerView.OnScrollListener {
    private boolean mIsScrollToBottom;
    private boolean mLoading;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mIsScrollToBottom = dy > 0;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState != RecyclerView.SCROLL_STATE_IDLE) {
            return;
        }

        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            return;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount = layoutManager.getItemCount();
        if (!mLoading && itemCount > 0 && mIsScrollToBottom && lastPosition == itemCount - 1) {
            onLoadMore();
            mLoading = true;
        }
    }

    protected abstract void onLoadMore();

    public void onLoadingFinish() {
        this.mLoading = false;
    }

    public boolean isLoading() {
        return mLoading;
    }
}