package me.wy.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;

    private List<String> mDataList;
    private OnLoadMoreListener mOnLoadMoreListener;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this
                , DividerItemDecoration.VERTICAL));
        mOnLoadMoreListener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOnLoadMoreListener.onLoadingFinish();
                    }
                }, 1000);
            }
        };
        mRecyclerView.addOnScrollListener(mOnLoadMoreListener);

        mDataList = new ArrayList<>();
        mAdapter = new MyAdapter(mDataList);
        //添加点击事件
        mAdapter.setOnStatusViewClickListener(new StatusAdapter.OnStatusViewClickListener() {
            @Override
            public void onEmptyViewClick(View view) {
                Toast.makeText(view.getContext(), "OnEmptyViewClick",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorViewClick(View view) {
                Toast.makeText(view.getContext(), "OnErrorViewClick",
                        Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_loading:
                mAdapter.showLoading();
                break;
            case R.id.menu_show_empty:
                mAdapter.showEmpty();
                break;
            case R.id.menu_show_error:
                mAdapter.showError();
                break;
            case R.id.menu_show_normal:
                for (int i = 0; i < 4; i++) {
                    mDataList.add("");
                }
                mAdapter.showNormal();
                break;
            case R.id.menu_remove_data:
                mDataList.clear();
                mAdapter.notifyDataSetChanged();
                break;
        }
        return false;
    }

    private class MyAdapter extends StatusAdapter<RecyclerView.ViewHolder> {
        private final int VIEW_TYPE_TEXT = 1;
        private final int VIEW_TYPE_IMAGE = 2;

        private List<String> mDataList;

        public MyAdapter(List<String> dataList) {
            this.mDataList = dataList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_TEXT) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_text, parent, false);
                return new TextDataHolder(itemView);
            } else {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_image, parent, false);
                return new ImageDataHolder(itemView);
            }
        }

        @Override
        public void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TextDataHolder) {
                TextDataHolder textDataHolder = (TextDataHolder) holder;
                textDataHolder.textView.setText("Hello World! " + position);
            } else if (holder instanceof ImageDataHolder) {
                //todo bindImageDataViewHolder
            }
        }

        @Override
        public int getDataItemCount() {
            return mDataList.size();
        }

        @Override
        public int getDataItemType(int position) {
            if (position % 2 == 0) {
                return VIEW_TYPE_TEXT;
            } else {
                return VIEW_TYPE_IMAGE;
            }
        }

        @Override
        protected void bindEmptyViewHolder(RecyclerView.ViewHolder holder) {
            super.bindEmptyViewHolder(holder);
            //可以在这里实现自定义操作
        }

        @Override
        protected void bindErrorViewHolder(RecyclerView.ViewHolder holder) {
            super.bindErrorViewHolder(holder);
            //可以在这里实现自定义操作
        }

        @Override
        protected int getLoadingLayout() {
            //重写此方法或者createLoadingViewHolder方法修改LoadingView
            return super.getLoadingLayout();
        }

        @Override
        protected int getEmptyLayout() {
            //重写此方法或者createEmptyViewHolder方法修改EmptyView
            return super.getEmptyLayout();
        }

        @Override
        protected int getErrorLayout() {
            //重写此方法或者createErrorViewHolder方法修改ErrorView
            return super.getErrorLayout();
        }
    }

    private class TextDataHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TextDataHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    private class ImageDataHolder extends RecyclerView.ViewHolder {
        public ImageDataHolder(View itemView) {
            super(itemView);
        }
    }
}
