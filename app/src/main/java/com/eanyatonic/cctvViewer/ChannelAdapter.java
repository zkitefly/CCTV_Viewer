package com.eanyatonic.cctvViewer;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eanyatonic.cctvViewer.bean.EpgInfo;

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
    private int selectedItem = 0; // 用于跟踪选中的项

    private List<EpgInfo> channelList;
    private OnItemClickListener onItemClickListener;
    private OnFocusChangeListener onFocusChangeListener;
    private RecyclerView recyclerView;
    private com.tencent.smtt.sdk.WebView view;

    public ChannelAdapter(List<EpgInfo> channelList, OnItemClickListener onItemClickListener,OnFocusChangeListener onFocusChangeListener,RecyclerView recyclerView,com.tencent.smtt.sdk.WebView view) {
        this.channelList = channelList;
        this.onItemClickListener = onItemClickListener;
        this.onFocusChangeListener = onFocusChangeListener;
        this.recyclerView = recyclerView;
        this.view = view;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        // 设置选中状态的背景和字体颜色
        if (position == selectedItem) {
            holder.channelNameTextView.setBackgroundResource(R.drawable.channel_background_unselected);
            holder.channelNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.unselected_color));
        } else {
            holder.channelNameTextView.setBackgroundResource(R.drawable.channel_background_selected);
            holder.channelNameTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_color));
        }
        recyclerView.requestFocus(); // 将焦点设置到 RecyclerView

        EpgInfo channel = channelList.get(position);
        holder.bind(channel);
        Log.d("ChannelAdapterxx", "Bound channel: " + channel.getName());
        holder.channelNameTextView.setOnKeyListener((v, keyCode, event) -> {
            int action = event.getAction();
            if (event.getAction() == KeyEvent.ACTION_DOWN){
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        int positionUp = position;
                        if (action == KeyEvent.ACTION_DOWN) {
                            if (positionUp <= 0) {
                                recyclerView.smoothScrollToPosition(getItemCount() - 1);
                                return true;
                            }
                        }
                        break;

                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        int positionDown = position;
                        if (action == KeyEvent.ACTION_DOWN) {
                            if (positionDown >= getItemCount() - 1) {
                                recyclerView.smoothScrollToPosition(0);
                                return true;
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_ENTER:
                        EpgInfo epgInfo = channelList.get(position);
                        Log.d(epgInfo.getName(),epgInfo.getName()+"xxx");
                        view.evaluateJavascript("async function xx(){document.querySelector('#play_or_plause_player').click();await sleep(3000);"
                                + "document.querySelector('#" + epgInfo.getId() + "')" + ".click();"+"}"+"xx()"
                                , null);
                        return true;

                }
            }

            return false;
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            //向上或者向下滚动
            boolean toLast = false;
            boolean toFirst = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scroolChange(recyclerView, toFirst, toLast);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    toLast = true;
                } else {
                    toFirst = true;
                }
            }
        });

    }

    public void scroolChange(RecyclerView recyclerView, boolean toFirst, boolean toLast) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
        int firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition();
        int totalItemCount = manager.getItemCount();

        //向下滚动,到底部
        if (lastVisibleItem == (totalItemCount - 1) && toLast) {
            View view = recyclerView.getChildAt(lastVisibleItem);
            LinearLayoutManager llM = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (view != null) {
                view.requestFocus();
            } else if (llM.findViewByPosition(lastVisibleItem) != null) {
                llM.findViewByPosition(lastVisibleItem).requestFocus();
            } else {
                recyclerView.requestFocus();
            }
        }

        //向上滚动,到顶部
        if (firstVisibleItem == 0 && toFirst) {
            View view =recyclerView.getChildAt(firstVisibleItem);
            LinearLayoutManager llM = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (view != null) {
                view.requestFocus();
            } else if (llM.findViewByPosition(firstVisibleItem) != null) {
                llM.findViewByPosition(firstVisibleItem).requestFocus();
            } else {
                recyclerView.requestFocus();
            }
        }
    }


    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder {
        public TextView channelNameTextView;


        public ChannelViewHolder(View itemView) {
            super(itemView);
            channelNameTextView = itemView.findViewById(R.id.channelNameTextView);

            channelNameTextView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(v,getAbsoluteAdapterPosition());
            });


            channelNameTextView.setOnFocusChangeListener((v, hasFocus) -> {

                onFocusChangeListener.onFocusChangeListener(v,hasFocus);
            });





        }

        public void bind(EpgInfo channel) {
            channelNameTextView.setText(channel.getName());
        }

    }

    // 接口定义
    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }

    public interface OnFocusChangeListener {
        void onFocusChangeListener(View view,boolean hasFocus);
    }




}
