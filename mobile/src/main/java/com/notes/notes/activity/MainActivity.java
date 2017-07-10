package com.notes.notes.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.notes.notes.R;
import com.notes.notes.database.DB;
import com.notes.notes.entity.Item;
import com.notes.notes.fragments.ScreenAll;
import com.notes.notes.fragments.ScreenMarked;
import com.notes.notes.fragments.ScreenTrash;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "MainActivity";

    GoogleApiClient googleApiClient;

    private final String Note_List = "/noteList";
    List<Item> data;
    DataMap dataMap;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppDefault); //Theme

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar(); // ToolBar
        initNavigationView(); // Navigation Drawer
        initScreenAll(); // Fragment ScreenAll

        // Google Api for wear
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        Log.d(LOG_TAG, "onStart Connecting: " + String.valueOf(googleApiClient.isConnecting()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected(): connected to Google API client " + googleApiClient.isConnected());
        new SendToWear(Note_List, dataMap).start();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed(): Failed to connect, with result: " + connectionResult);
    }

    @Override
    public void onBackPressed() {
        ScreenAll fragment_List = new ScreenAll();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment_List).commit();

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), R.string.Exit, Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.left_drawer);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                Fragment fragment = null;
                switch (menuItem.getItemId()) {

                    case R.id.actionNotes:
                        fragment = new ScreenAll();
                        toolbar.setTitle(R.string.Notes);
                        break;

                    case R.id.actionMarked:
                        fragment = new ScreenMarked();
                        toolbar.setTitle(R.string.Marked);
                        break;

                    case R.id.actionTrash:
                        fragment = new ScreenTrash();
                        toolbar.setTitle(R.string.Trash);
                        break;

                    case R.id.actionAbout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(R.string.action_about)
                                .setMessage(R.string.About_Text)
                                .setIcon(R.mipmap.ic_launcher)
                                .setCancelable(false)
                                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                } else {
                    Log.d(LOG_TAG, "ERROR, Fragment not created");
                }
                return true;
            }
        });
    }

    private void initScreenAll() {
        Fragment ScreenOne = new ScreenAll();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, ScreenOne).commit();
    }

    private List<Item> fetchData() {
        data = new ArrayList<>();

        // Database connect
        DB db = new DB(getApplicationContext());
        db.open();

        Item item;
        Cursor c = db.getAllData();

        // send data from DB to ArrayList
        if (c != null) {
            while (c.moveToNext()) {
                int column_id = c.getColumnIndex(DB.getColumnId());
                String Index = c.getString(column_id);
                int column_title = c.getColumnIndex(DB.getColumnTitle());
                String Title = c.getString(column_title);
                int column_text = c.getColumnIndex(DB.getColumnText());
                String Text = c.getString(column_text);

                item = new Item();
                item.setId(Index);
                item.setTitle(Title);
                item.setText(Text);

                data.add(item);

            }
        }
        return data;
    }

    private DataMap ArrayToDataMap() {
        dataMap = new DataMap();
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Note_List).setUrgent();
        dataMap = putDataMapRequest.getDataMap();

        for (Item item : data) {
            DataMap noteMap = new DataMap();
            noteMap.putLong("Id", Long.parseLong(item.getId()));
            noteMap.putString("Title", item.getTitle());
            noteMap.putString("Text", item.getText());
            dataMap.putDataMap(item.getId(), noteMap);
        }

        Log.d(LOG_TAG, "Send " + dataMap);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleApiClient, request).await();
        Log.d(LOG_TAG, result.getStatus().getStatusMessage());
        return dataMap;
    }

    private class SendToWear extends Thread {
        DataMap dataMap;
        String path;

        SendToWear(String path, DataMap dataMap) {
            this.path = path;
            this.dataMap = dataMap;
        }

        @Override
        public void run() {
            Log.d(LOG_TAG, "Working SendToWear Thread");

            //Fetch data from DataBase
            fetchData();
            Log.v(LOG_TAG, "List<Information> data Size = " + String.valueOf(data.size()));

            // ArrayList to DataMap
            ArrayToDataMap();
        }
    }

}