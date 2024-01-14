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

    private List<EpgInfo> channelList;
    private OnItemClickListener onItemClickListener;
    private RecyclerView recyclerView;
    private com.tencent.smtt.sdk.WebView view;

    public ChannelAdapter(List<EpgInfo> channelList, OnItemClickListener onItemClickListener,RecyclerView recyclerView,com.tencent.smtt.sdk.WebView view) {
        this.channelList = channelList;
        this.onItemClickListener = onItemClickListener;
        this.recyclerView = recyclerView;
        this.view = view;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        recyclerView.requestFocus();
        Log.d("onCreateViewHolder","onCreateViewHolder");
        return new ChannelViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        EpgInfo channel = channelList.get(position);
        holder.bind(channel);
        Log.d("ChannelAdapterxx", "Bound channel: " + channel.getName());
        holder.channelNameTextView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN){
                switch (keyCode) {
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
                onItemClickListener.onItemClick(v, getAbsoluteAdapterPosition());
            });


            channelNameTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    int position = recyclerView.getLayoutManager().getPosition(v);
                    Log.d("xxxx","xxxx"+position);
                    recyclerView.scrollToPosition(position+1);
                    channelNameTextView.setBackgroundResource(R.drawable.channel_background_unselected);
                } else {
                    channelNameTextView.setBackgroundResource(R.drawable.channel_background_selected);

                }
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





}
