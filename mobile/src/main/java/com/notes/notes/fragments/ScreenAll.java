package com.notes.notes.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.database.DatabaseException;
import com.notes.notes.R;
import com.notes.notes.activity.CreateActivity;
import com.notes.notes.adapter.MainRecyclerViewAdapter;
import com.notes.notes.database.DB;
import com.notes.notes.entity.Information;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class ScreenAll extends Fragment implements OnClickListener {

    /*
    TODO: AsyncTask for load data from DB
    */

    // LOG TAG
    final String LOG_TAG = "ScreenAll";

    SharedPreferences preferences;

    static String key = "first_launch";
    boolean val = true;

    List<Information> data;

    RecyclerView recyclerView;
    MainRecyclerViewAdapter adapter;

    DB db;

    Timer timer;
    MyTimerTask myTimerTask;

    public ScreenAll() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.screen_all, container, false);

        // открываем подключение к БД
        db = new DB(getActivity());
        db.open();

        //  INITIALIZE RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // INITIALIZE Floating action Button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        //Set Click
        fab.setOnClickListener(this);

        preferences = getActivity().getSharedPreferences(key, Context.MODE_APPEND);

        MaterialTapTargetPrompt prompt = new MaterialTapTargetPrompt.Builder(this.getActivity())
                .setTarget(rootView.findViewById(R.id.fab))
                .setPrimaryText("Create your first note")
                .setSecondaryText("Tap the plus to start composing your first note")
                .setBackgroundColourFromRes(R.color.colorPrimary)
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                        //Do something such as storing a value so that this prompt is never shown again
                        Editor editor = preferences.edit();
                        editor.putBoolean(key, false);
                        editor.apply();
                        Log.d(LOG_TAG, "onHidePrompt " + val);
                    }

                    @Override
                    public void onHidePromptComplete() {
                        Editor editor = preferences.edit();
                        editor.putBoolean(key, false);
                        editor.apply();
                        Log.d(LOG_TAG, "onHidePromptComplete " + val);
                    }
                })
                .create();

        if (preferences.getBoolean(key, Boolean.parseBoolean("true"))) {
            prompt.show();
        }

        // Properties RecyclerView
        //setMainRecyclerView();

        //GetDataTask GetDataTask = new GetDataTask();
        //GetDataTask.execute();

        //TODO: WARNING !!!
        new GetDataTask().execute();

        setHasOptionsMenu(true);


        return rootView;
    }

    public void setMainRecyclerView() {
        adapter = new MainRecyclerViewAdapter(getContext(), getData());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setVerticalScrollBarEnabled(true);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setSearch(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setMainRecyclerView();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void setSearch(String value) {
        adapter = new MainRecyclerViewAdapter(getContext(), searchData(value));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    private List<Information> searchData(String value) {
        List<Information> search = new ArrayList<>();
        Information info;
        Cursor c = db.searchItem(value);
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

                info = new Information();
                info.setId(Index);
                info.setTitle(Title);
                info.setText(Text);
                info.setDate(Date);
                info.setType(Type);
                info.setMark(String.valueOf(Mark));
                info.setColor(Color);

                if (Mark == 0) {
                    info.setImage_id(R.drawable.fav_24x24);

                } else {
                    info.setImage_id(R.drawable.fav_24x24_fill);
                }
                //.d(LOG_TAG, "ID= " + Index + " Title= " + Title + " Text= " + Text + " Date= " + Date + " Type= " + Type + " Image= " + Mark);
                search.add(info);
            }
        }
        return search;
    }

    private List<Information> getData() {
        data = new ArrayList<>();
        Information mainInfo;
        Cursor c = db.getAllData();
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
                }

                data.add(mainInfo);
                Log.d(LOG_TAG, Index + " " + Title + ", " + Text);
            }
        }
        return data;
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
            timer = new Timer();
            myTimerTask = new MyTimerTask();
            timer.schedule(myTimerTask, 550);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (preferences.contains(key)) {
            val = preferences.getBoolean(key, false);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    @Override
    public void onPause() {
        super.onPause();

        //TODO: DEBUG this
        Log.d(LOG_TAG, "onPause");

        Editor editor = preferences.edit();
        editor.putBoolean(key, false);
        editor.apply();
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Snackbar.make(recyclerView, R.string.NotifCreated, Snackbar.LENGTH_LONG).show();
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

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
            } catch (DatabaseException e){
                e.printStackTrace();
                db.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setMainRecyclerView();
            //adapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "AsyncTask - onPostExecute");
        }
    }
}