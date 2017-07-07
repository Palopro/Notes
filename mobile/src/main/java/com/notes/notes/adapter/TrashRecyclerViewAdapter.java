package com.notes.notes.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.notes.notes.R;
import com.notes.notes.database.DB;
import com.notes.notes.entity.Information;

import java.util.List;

public class TrashRecyclerViewAdapter extends RecyclerView.Adapter<TrashRecyclerViewAdapter.TrashViewHolder> {

    // LOG TAG
    final String LOG_TAG = "TrashRecyclerViewAdapter";

    public List<Information> mainInfo;
    private Context mContext;

    public TrashRecyclerViewAdapter(Context context, List<Information> mainInfo) {
        this.mContext = context;
        this.mainInfo = mainInfo;
    }

    @Override
    public TrashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TrashViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TrashViewHolder holder, int position) {
        holder.itemView.setLongClickable(true);

        holder.Title.setText(mainInfo.get(position).getTitle());
        holder.Text.setText(mainInfo.get(position).getText());
        holder.Date.setText(mainInfo.get(position).getDate());
        holder.Type.setText(mainInfo.get(position).getType());
        holder.Image.setImageResource(mainInfo.get(position).getImage_id());

        holder.itemView.setTag(mainInfo.get(position).getId());

        holder.card.setCardBackgroundColor(Color.parseColor(mainInfo.get(position).getColor()));

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, R.string.Restore).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        long id = Long.parseLong((String) holder.itemView.getTag());

                        int position = holder.getLayoutPosition();
                        DB db = new DB(mContext);
                        db.open();

                        Log.d(LOG_TAG, "Context Menu Click Delete ID = " + id);

                        db.restoreData(id);
                        mainInfo.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        Snackbar.make(holder.itemView, "Note restored", Snackbar.LENGTH_LONG).show();
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mainInfo.size();
    }

    public class TrashViewHolder extends RecyclerView.ViewHolder {

        CardView card;

        TextView Title;
        TextView Text;
        TextView Date;
        TextView Type;
        ImageView Image;

        public TrashViewHolder(View itemView) {
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.cardView);

            Title = (TextView) itemView.findViewById(R.id.tvTitle);
            Text = (TextView) itemView.findViewById(R.id.tvText);
            Date = (TextView) itemView.findViewById(R.id.tvDate);
            Type = (TextView) itemView.findViewById(R.id.tvStyle);
            Image = (ImageView) itemView.findViewById(R.id.imageView);
        }

    }
}


