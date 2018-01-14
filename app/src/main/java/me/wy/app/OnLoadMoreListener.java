package me.wy.app;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * author: WangYi
 * created on: 2018/1/8 19:14
 * description:
 */

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {
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

    public abstract void onLoadMore();

    public void onLoadingFinish() {
        this.mLoading = false;
    }
}
