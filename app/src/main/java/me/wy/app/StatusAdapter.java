package me.wy.app;

import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: WangYi
 * created on: 2018/1/11 20:54
 * description:
 */

public abstract class StatusAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    private String TAG = "StatusAdapter";
    //加载中
    private static final int STATUS_LOADING = 100;
    //空数据
    private static final int STATUS_EMPTY = 200;
    //加载错误
    private static final int STATUS_ERROR = 300;
    //正常状态
    private static final int STATUS_NORMAL = 400;
    //加载更多
    private static final int STATUS_LOAD_MORE = 500;

    @IntDef({STATUS_LOADING, STATUS_EMPTY, STATUS_ERROR,
            STATUS_NORMAL, STATUS_LOAD_MORE})
    public @interface AdapterStatus {
    }

    @AdapterStatus
    private int mStatus = STATUS_LOADING;

    private OnScrollListener mOnScrollListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnStatusViewClickListener mOnStatusViewListener;
    private RecyclerView mRecyclerView;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case STATUS_LOADING:
                return createLoadingViewHolder(parent);
            case STATUS_EMPTY:
                return createEmptyViewHolder(parent);
            case STATUS_ERROR:
                return createErrorViewHolder(parent);
            case STATUS_LOAD_MORE:
                return createLoadMoreHolder(parent);
            case STATUS_NORMAL:
            default:
                return onCreateDataViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mStatus == STATUS_EMPTY) {
            bindEmptyViewHolder(holder);
        } else if (mStatus == STATUS_ERROR) {
            bindErrorViewHolder(holder);
        } else if (mStatus == STATUS_NORMAL) {
            onBindDataViewHolder((VH) holder, position);
        } else if (mStatus == STATUS_LOAD_MORE) {
            if (position < getItemCount() - 1) {
                onBindDataViewHolder((VH) holder, position);
            }
        }
    }

    @Override
    public int getItemCount() {
        switch (mStatus) {
            case STATUS_LOADING:
            case STATUS_EMPTY:
            case STATUS_ERROR:
                return 1;
            case STATUS_LOAD_MORE:
                return getDataItemCount() + 1;
            case STATUS_NORMAL:
            default:
                return getDataItemCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mStatus == STATUS_NORMAL) {
            return getDataItemType(position);
        }

        if (mStatus == STATUS_LOAD_MORE && position < getItemCount() - 1) {
            return getDataItemType(position);
        }
        return mStatus;
    }

    public void showLoading() {
        this.mStatus = STATUS_LOADING;
        notifyDataSetChanged();
    }

    public void showEmpty() {
        this.mStatus = STATUS_EMPTY;
        notifyDataSetChanged();
    }

    public void showError() {
        this.mStatus = STATUS_ERROR;
        notifyDataSetChanged();
    }

    public void showNormal() {
        this.mStatus = STATUS_NORMAL;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        registerAdapterDataObserver(observer);
        if (mOnLoadMoreListener != null) {
            mOnScrollListener = new OnScrollListener() {
                @Override
                protected void onLoadMore() {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    showLoadMoreView();
                }
            };
            recyclerView.addOnScrollListener(mOnScrollListener);
        }
    }

    private final RecyclerView.AdapterDataObserver observer =
            new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    //数据为空时自动显示空数据状态
                    if (getDataItemCount() == 0 && (mStatus == STATUS_NORMAL
                            || mStatus == STATUS_LOAD_MORE)) {
                        showEmpty();
                    }
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                }
            };

    public class StatusViewHolder extends RecyclerView.ViewHolder {
        public StatusViewHolder(View itemView) {
            super(itemView);
        }
    }

    @LayoutRes
    protected int getLoadingLayout() {
        return R.layout.layout_loading_view;
    }

    @LayoutRes
    protected int getEmptyLayout() {
        return R.layout.layout_empty_view;
    }

    @LayoutRes
    protected int getErrorLayout() {
        return R.layout.layout_error_view;
    }

    @LayoutRes
    protected int getLoadMoreLayout() {
        return R.layout.layout_load_more;
    }

    protected RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(getLoadingLayout(), parent, false);
        return new StatusViewHolder(itemView);
    }

    protected RecyclerView.ViewHolder createEmptyViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(getEmptyLayout(), parent, false);
        return new StatusViewHolder(itemView);
    }

    protected RecyclerView.ViewHolder createErrorViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(getErrorLayout(), parent, false);
        return new StatusViewHolder(itemView);
    }

    protected RecyclerView.ViewHolder createLoadMoreHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(getLoadMoreLayout(), parent, false);
        return new StatusViewHolder(itemView);
    }

    protected void bindEmptyViewHolder(RecyclerView.ViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnStatusViewListener != null) {
                    mOnStatusViewListener.onEmptyViewClick(v);
                }
            }
        });
    }

    protected void bindErrorViewHolder(RecyclerView.ViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnStatusViewListener != null) {
                    mOnStatusViewListener.onErrorViewClick(v);
                }
            }
        });
    }

    public abstract RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindDataViewHolder(VH holder, int position);

    public abstract int getDataItemCount();

    public int getDataItemType(int position) {
        return 0;
    }

    public void showLoadMoreView() {
        mStatus = STATUS_LOAD_MORE;
        notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(getItemCount() - 1);
    }

    public void onLoadingFinish() {
        if (mOnScrollListener != null) {
            mOnScrollListener.onLoadingFinish();
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    public void setOnStatusViewClickListener(OnStatusViewClickListener listener) {
        this.mOnStatusViewListener = listener;
    }

    public OnStatusViewClickListener getOnStatusViewClickListener() {
        return mOnStatusViewListener;
    }
}
