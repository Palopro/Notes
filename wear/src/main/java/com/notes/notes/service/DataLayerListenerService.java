package com.notes.notes.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.notes.notes.entity.Information;

import java.util.ArrayList;
import java.util.Set;

public class DataLayerListenerService extends WearableListenerService {

    private final String Note_List = "/noteList";
    DataMap dataMap;


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("MyTag", "Service Start");

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(Note_List)) {
                }
                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Log.d("MyTag", "Data on watch: " + String.valueOf(dataMap));

                //rehydrateList(dataMap);

                ArrayList<Information> items = rehydrateList(dataMap);

                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("items", items);

                intent.setAction(intent.ACTION_SEND);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }

    }

    private ArrayList<Information> rehydrateList(DataMap dataMap) {
       ArrayList<Information> items = new ArrayList<>();
        Set<String> trackingIds = dataMap.keySet();

        for (String noteId : trackingIds) {

            DataMap noteMap = dataMap.getDataMap(noteId);
            if (noteMap != null) {
                long id = noteMap.getLong("Id");
                String title = noteMap.getString("Title");
                String text = noteMap.getString("Text");
                Information note = new Information(id, title, text);
                items.add(note);

                Log.v("MyTag", "ID = " + id + "\nTitle = " + title + "\nText = " + text);
            }
        }

        return items;
    }

}
