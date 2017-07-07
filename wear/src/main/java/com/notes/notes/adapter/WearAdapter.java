package com.notes.notes.adapter;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.notes.notes.R;
import com.notes.notes.entity.Information;

import java.util.List;


public class WearAdapter extends WearableListView.Adapter {

    private List<Information> mainInfo;
    Context mContext;
    LayoutInflater inflater;

    public WearAdapter(Context context, List<Information> mainInfo) {
        this.mContext = context;
        this.mainInfo = mainInfo;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new WearableListView.ViewHolder(inflater.inflate(R.layout.item_wear, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        TextView Title = itemViewHolder.Title;
        TextView Text = itemViewHolder.Text;

        holder.itemView.setTag(mainInfo.get(position).getId());

        Title.setText(mainInfo.get(position).getTitle());
        Text.setText(mainInfo.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {

        TextView Title;
        TextView Text;

        public ItemViewHolder(View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.WtvTitle);
            Text = (TextView) itemView.findViewById(R.id.WtvText);
        }
    }
}

