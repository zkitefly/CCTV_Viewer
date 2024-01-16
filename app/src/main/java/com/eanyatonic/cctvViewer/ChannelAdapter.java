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
    private RecyclerView recyclerView;
    private com.tencent.smtt.sdk.WebView view;

    public ChannelAdapter(List<EpgInfo> channelList, RecyclerView recyclerView, com.tencent.smtt.sdk.WebView view) {
        this.channelList = channelList;
        this.recyclerView = recyclerView;
        this.view = view;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        Log.d("onCreateViewHolder", "onCreateViewHolder");
        return new ChannelViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        EpgInfo channel = channelList.get(position);
        holder.bind(channel);


    }


    @Override
    public int getItemCount() {
        return channelList.size();
    }



    private int getCurrentFocusPosition() {
        // 获取当前焦点位置
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else {
            // 如果使用其他布局管理器，根据需要进行适配
            return 0;
        }
    }


    public class ChannelViewHolder extends RecyclerView.ViewHolder {
        public TextView channelNameTextView;
        public Integer pos = 0;


        public ChannelViewHolder(View itemView) {
            super(itemView);
            channelNameTextView = itemView.findViewById(R.id.channelNameTextView);

//            channelNameTextView.setOnKeyListener((v, keyCode, event) -> {
//                switch (keyCode) {
//                    case KeyEvent.KEYCODE_ENTER:
//                        EpgInfo epgInfo = channelList.get(recyclerView.getLayoutManager().getPosition(v));
//                        Log.d(epgInfo.getName(), epgInfo.getName() + "xxx");
//                        view.evaluateJavascript("async function xx(){document.querySelector('#play_or_plause_player').click();await sleep(3000);"
//                                        + "document.querySelector('#" + epgInfo.getId() + "')" + ".click();" + "}" + "xx()"
//                                , null);
//                        return true;
//                    case KeyEvent.ACTION_UP:
//                        moveFocusUp();
//                        return true;
//                    case KeyEvent.ACTION_DOWN:
//                        moveFocusDown();
//                        return true;
//                }
//
//                return false;
//            });

            channelNameTextView.setOnClickListener(v -> {
                EpgInfo epgInfo = channelList.get(recyclerView.getLayoutManager().getPosition(v));
                Log.d(epgInfo.getName(), epgInfo.getName() + "xxx");
                view.evaluateJavascript("async function xx(){document.querySelector('#play_or_plause_player').click();await sleep(3000);"
                                + "document.querySelector('#" + epgInfo.getId() + "')" + ".click();" + "}" + "xx()"
                        , null);
            });


            channelNameTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    int position = recyclerView.getLayoutManager().getPosition(v);
                    Log.d("xxxx","xxxx"+position);
                    if (pos>=position){
                        pos = position;
                        recyclerView.scrollToPosition(position-1);
                    }else {
                        pos = position;
                        recyclerView.scrollToPosition(position+1);
                    }
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


    private void moveFocusUp() {

        // 获取当前焦点位置
        int currentPos = getCurrentFocusPosition();

        // 计算上一个焦点位置
        int newPos = Math.max(0, currentPos - 1);

        Log.d("moveFocusUp",newPos+"xxxx"+currentPos+"xxxxxxx");
        // 平滑滚动到上一个位置
        recyclerView.scrollToPosition(newPos);
    }

    private void moveFocusDown() {
        // 获取当前焦点位置
        int currentPos = getCurrentFocusPosition();

        // 计算下一个焦点位置
        int newPos = Math.min(getItemCount() - 1, currentPos + 1);

        Log.d("moveFocusDown",newPos+"xxxx"+currentPos+"xxxxxxx");
        // 平滑滚动到下一个位置
        recyclerView.scrollToPosition(newPos);
    }


}
