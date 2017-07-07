package com.notes.notes.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.notes.notes.R;
import com.notes.notes.adapter.WearAdapter;
import com.notes.notes.entity.Information;

import java.util.ArrayList;
import java.util.List;

public class WearActivity extends WearableActivity {

    WearableListView wearableListView;

    WearAdapter wearAdapter;

    List<Information> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        Log.d("MyTag", "onCreate WearActivity");

        DataReceiver dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(dataReceiver, filter);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                wearableListView = (WearableListView) stub.findViewById(R.id.wearListView);
                Log.d("MyTag","OnLayoutInflated");
                wearAdapter = new WearAdapter(getApplicationContext(), items);
                wearableListView.setAdapter(wearAdapter);
            }
        });

        setAmbientEnabled();

    }


    private class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("myTag", "BroadcastReceiver Start");

            items = intent.getParcelableArrayListExtra("items");

            wearAdapter = new WearAdapter(getApplicationContext(), items);

            wearAdapter.notifyDataSetChanged();
        }
    }
}

