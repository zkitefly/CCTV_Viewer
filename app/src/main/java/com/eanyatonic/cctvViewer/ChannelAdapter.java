package com.eanyatonic.cctvViewer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
    private List<Channel> channelList;

    public ChannelAdapter(List<Channel> channelList) {
        this.channelList = channelList;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {
        Channel channel = channelList.get(position);
        holder.bind(channel);
        Log.d("ChannelAdapter", "Bound channel: " + channel.getName());

    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder {
        private TextView channelNameTextView;

        public ChannelViewHolder(View itemView) {
            super(itemView);
            channelNameTextView = itemView.findViewById(R.id.channelNameTextView);
        }

        public void bind(Channel channel) {
            channelNameTextView.setText(channel.getName());
        }
    }
}
