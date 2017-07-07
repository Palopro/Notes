package com.notes.notes.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.notes.notes.R;
import com.notes.notes.activity.CreateActivity;
import com.notes.notes.adapter.MainRecyclerViewAdapter;
import com.notes.notes.database.DB;
import com.notes.notes.entity.Information;

import java.util.ArrayList;
import java.util.List;

public class ScreenMarked extends Fragment implements View.OnClickListener {

    // LOG TAG
    final String LOG_TAG = "ScreenMarked";

    RecyclerView recyclerView;
    MainRecyclerViewAdapter adapter;

    DB db;

    public ScreenMarked() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.screen_marked, container, false);


        // открываем подключение к БД
        db = new DB(getActivity());
        db.open();

        //  INITIALIZE RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        //INITIALIZE Floating action Button
        FloatingActionButton fab = (android.support.design.widget.FloatingActionButton) rootView.findViewById(R.id.fab);

        //Set Click
        fab.setOnClickListener(this);

        // Properties RecyclerView
        //setMainRecyclerView();

        GetMarkedDataTask GetMarkedDataTask = new GetMarkedDataTask();
        GetMarkedDataTask.execute();

        return rootView;
    }


    public List<Information> getData() {
        List<Information> data = new ArrayList<>();
        Information mainInfo;
        Cursor c = db.getMarkedData();
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

                if (Mark == 0) {
                    mainInfo.setImage_id(R.drawable.fav_24x24);
                } else {
                    mainInfo.setImage_id(R.drawable.fav_24x24_fill);
                }
                data.add(mainInfo);
            }
        }
        return data;
    }

    public void setMainRecyclerView() {
        adapter = new MainRecyclerViewAdapter(getContext(), getData());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(getActivity(), CreateActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        setMainRecyclerView();

        if (resultCode == 0) {
            Log.d(LOG_TAG, "Result Code = " + resultCode);
        } else {
            Log.d(LOG_TAG, "Result Code = " + resultCode);
            Snackbar.make(recyclerView, "Запись сохранена", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setMainRecyclerView();
    }

    public void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    private class GetMarkedDataTask extends AsyncTask<Void, Void, Void> {

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
                getData();
            } finally {
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
