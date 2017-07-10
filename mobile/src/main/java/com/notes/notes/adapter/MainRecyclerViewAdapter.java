package com.notes.notes.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.notes.notes.activity.UpdateActivity;
import com.notes.notes.database.DB;
import com.notes.notes.entity.Item;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolder> {

    // LOG TAG
    private static final String LOG_TAG = "MainAdapter";

    // List
    private List<Item> mainInfo;
    private Context mContext;

    // Constructor with parameters
    public MainRecyclerViewAdapter(Context context, List<Item> mainInfo) {
        this.mContext = context;
        this.mainInfo = mainInfo;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {

        holder.Title.setText(mainInfo.get(position).getTitle());
        holder.Text.setText(mainInfo.get(position).getText());
        holder.Date.setText(mainInfo.get(position).getDate());
        holder.Type.setText(mainInfo.get(position).getType());
        holder.Image.setImageResource(mainInfo.get(position).getImage_id());

        holder.itemView.setTag(mainInfo.get(position).getId());
        holder.card.setCardBackgroundColor(Color.parseColor(mainInfo.get(position).getColor()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getId();

                Object id = holder.itemView.getTag();

                String bg = mainInfo.get(holder.getAdapterPosition()).getColor();

                Intent intent = new Intent(mContext, UpdateActivity.class);
                intent.putExtra("id", (String) id);
                intent.putExtra("background", bg);

                Log.d(LOG_TAG, "ID = " + String.valueOf(id));
                Log.d(LOG_TAG, "Color = " + bg);

                mContext.startActivity(intent);
            }
        });

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, R.string.delete_record).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        long id = Integer.parseInt((String) holder.itemView.getTag());
                        int position = holder.getLayoutPosition();
                        DB db = new DB(mContext);
                        db.open();

                        Log.d(LOG_TAG, "Context Menu Click Delete ID = " + id);

                        db.delRec(id);
                        mainInfo.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        Snackbar.make(holder.itemView, R.string.NotifMove, Snackbar.LENGTH_LONG).show();
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


    class MainViewHolder extends RecyclerView.ViewHolder {

        CardView card;

        TextView Title;
        TextView Text;
        TextView Date;
        TextView Type;
        ImageView Image;

        private MainViewHolder(final View itemView) {

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


