package com.notes.notes.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.notes.notes.R;
import com.notes.notes.adapter.TrashRecyclerViewAdapter;
import com.notes.notes.database.DB;
import com.notes.notes.entity.Information;

import java.util.ArrayList;
import java.util.List;

public class ScreenTrash extends Fragment {

    final String LOG_TAG = "ScreenTrash";

    DB db;
    AlertDialog.Builder alertDialog;
    RecyclerView recyclerView;
    TrashRecyclerViewAdapter adapter;


    public ScreenTrash() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.screen_trash, container, false);

        db = new DB(getActivity());
        db.open();

        //  INITIALIZE RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // Properties RecyclerView
        //setMainRecyclerView();

        //GetTrashDataTask getTrashDataTask = new GetTrashDataTask();
        //getTrashDataTask.execute();

        //TODO: WARNING !!!
        new GetTrashDataTask().execute();

        // создаем диалог очищения коризины
        alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.Dialog_Title);
        alertDialog.setMessage(R.string.Dialog_Text);

        // позитивный ответ
        alertDialog.setPositiveButton(R.string.Dialog_BtnOK, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //LOG
                Log.d(LOG_TAG, "---Delete all in table Trash---");

                db.deleteAll();

                db.open();
                setMainRecyclerView();

                // Message
                Snackbar.make(recyclerView, R.string.NotifEmpty, Snackbar.LENGTH_LONG).show();
            }
        });

        // негативный ответ
        alertDialog.setNegativeButton(R.string.Dialog_BtnCancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        setHasOptionsMenu(true);

        return rootView;
    }

    private void setMainRecyclerView() {
        adapter = new TrashRecyclerViewAdapter(getContext(), getTrashData());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.trash, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                alertDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<Information> getTrashData() {
        List<Information> data = new ArrayList<>();
        Information mainInfo;
        Cursor c = db.getTrashData();
        if (c != null) {
            while (c.moveToNext()) {
                int column_id = c.getColumnIndex(DB.getColumnId());
                String Index = c.getString(column_id);

                int column_title = c.getColumnIndex(DB.getColumnTitle());
                String Title = c.getString(column_title);

                int column_text = c.getColumnIndex(DB.getColumnText());
                String Text = c.getString(column_text);

                int column_date = c.getColumnIndex(DB.getColumnDate());
                String Date = c.getString(column_date);

                int column_type = c.getColumnIndex(DB.getColumnType());
                String Type = c.getString(column_type);

                int column_mark = c.getColumnIndex(DB.getColumnMark());
                int Mark = c.getInt(column_mark);

                int column_color = c.getColumnIndex(DB.getColumnColor());
                String Color = c.getString(column_color);

                mainInfo = new Information();
                mainInfo.setId(Index);
                mainInfo.setTitle(Title);
                mainInfo.setText(Text);
                mainInfo.setDate(Date);
                mainInfo.setType(Type);
                mainInfo.setColor(Color);

                if (Mark == 1) {
                    mainInfo.setImage_id(R.drawable.fav_24x24_fill);
                } else {
                    mainInfo.setColor_id(R.drawable.fav_24x24);
                }

                data.add(mainInfo);
            }
        }
        Log.d(LOG_TAG, "Size of trash = " + String.valueOf(data.size()));
        return data;
    }

    @Override
    public void onResume() {
        super.onResume();
        //GetTrashDataTask getTrashDataTask = new GetTrashDataTask();
        //getTrashDataTask.execute();
        // setMainRecyclerView();

        //TODO: WARNING !!!
        new GetTrashDataTask().execute();
    }

    public void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    private class GetTrashDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(LOG_TAG, "AsyncTask - onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(LOG_TAG, "AsyncTask - doInBackground");

            db.open();
            try {
                getTrashData();
            } finally {
                //TODO: WARNING !!!
                //db.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setMainRecyclerView();
            adapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "AsyncTask - onPostExecute");
        }
    }

}