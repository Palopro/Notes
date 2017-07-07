package com.notes.notes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.notes.notes.R;
import com.notes.notes.database.DB;

import java.text.SimpleDateFormat;
import java.util.Date;


public class UpdateActivity extends AppCompatActivity implements OnClickListener {


    final String LOG_TAG = "UpdateActivity";

    NestedScrollView scrollView;
    ColorPickerDialog colorPickerDialog;

    EditText etTitle;
    EditText etText;

    Button btnNormal;
    Button btnBold;
    Button btnItalic;

    CheckBox checkBox;

    public TextView textView;

    public String dateNow;
    public long id;
    DB db;

    Date currentDate = new Date();


    // Colors for Color Picker dialog
    final int[] colors = new int[]{
            Color.parseColor("#FAFAFA"), Color.parseColor("#FF8A80"), Color.parseColor("#FFD180"), Color.parseColor("#FFFF8D"),
            Color.parseColor("#CCFF90"), Color.parseColor("#a7FFEB"), Color.parseColor("#80d8ff"), Color.parseColor("#cfd8dc")};

    int selectedColor;

    String bgColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppDefault); //Theme

        setContentView(R.layout.activity_update);

        initToolBar();

        Intent intent = getIntent();

        long id = Long.parseLong(intent.getStringExtra("id"));
        String color = intent.getStringExtra("background");

        selectedColor = Color.parseColor(color);

        bgColor = String.format("#%06X", (0xFFFFFF & selectedColor));

        Log.d(LOG_TAG, String.valueOf(id));
        Log.d(LOG_TAG, "BG = " + color);

        db = new DB(this);
        db.open();

        db.getItem(id);


        scrollView = (NestedScrollView) findViewById(R.id.UpdateScroll);
        scrollView.setBackgroundColor(Color.parseColor(color));

        etTitle = (EditText) findViewById(R.id.etTitle);
        etText = (EditText) findViewById(R.id.etText);

        textView = (TextView) findViewById(R.id.textView);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        ChangeTypeFace();
        ChangeMarked();

        etTitle.setText(db.Items[0], TextView.BufferType.EDITABLE);
        etText.setText(db.Items[1], TextView.BufferType.EDITABLE);
        etText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateNow = (simpleDateFormat.format(currentDate));

        btnNormal = (Button) findViewById(R.id.NormalButton);
        btnNormal.setOnClickListener(this);

        btnBold = (Button) findViewById(R.id.BoldButton);
        btnBold.setOnClickListener(this);

        btnItalic = (Button) findViewById(R.id.ItalicButton);
        btnItalic.setOnClickListener(this);

        db = new DB(this);

        colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.SelectColor, colors, selectedColor, 4, colors.length);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.updateColor:

                colorPickerDialog.show(getFragmentManager(), "Color");
                colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
                        bgColor = String.format("#%06X", (0xFFFFFF & color));
                        selectedColor = Color.parseColor(bgColor);
                        scrollView.setBackgroundColor(Color.parseColor(bgColor));

                        Log.d(LOG_TAG, String.format("#%06X", (0xFFFFFF & color)));
                    }

                });

                break;


            case R.id.action_save:

                Intent intent = getIntent();
                long id = Long.parseLong(intent.getStringExtra("id"));

                String title = etTitle.getText().toString();
                String text = etText.getText().toString();
                String Stype = textView.getText().toString();
                String date = dateNow;
                int mark;
                int image;

                // открываем подключение к БД
                db = new DB(this);
                db.open();

                Log.d(LOG_TAG, "---Insert in table---");


                //TODO: Test this
                if (checkBox.isChecked() == true) {
                    mark = 1;
                    image = R.drawable.fav_24x24_fill;
                } else {
                    mark = 0;
                    image = R.drawable.fav_24x24;
                }

                Log.d(LOG_TAG, "ID = " + id + " Title - " + title + " Text - " + text + " Date - " + date + " Style - " + Stype + " Marked - " + mark + " Image - " + image + " Color - " + bgColor);

                db.updRec(id, title, text, date, Stype, String.valueOf(mark), image, bgColor);

                intent = new Intent();
                setResult(RESULT_OK, intent);
                this.finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void ChangeMarked() {
        int mark = Integer.parseInt(db.Items[3]);


        //TODO: change If else statement
        if (mark == 0) {
            checkBox.setChecked(false);
        }

        if (mark == 1) {
            checkBox.setChecked(true);
        }
    }

    private void ChangeTypeFace() {
        int style = Integer.parseInt(db.Items[2]);

        if (style == 1) {
            etText.setTypeface(Typeface.DEFAULT); // обычный текст
        }

        if (style == 2) {
            etText.setTypeface(etText.getTypeface(), Typeface.BOLD); // жирный текст
        }

        if (style == 3) {
            etText.setTypeface(etText.getTypeface(), Typeface.ITALIC); // курсивный текст
        }
    }

    @Override
    public void onClick(View view) {

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        switch (view.getId()) {

            case R.id.NormalButton:
                int type = 1;
                textView.setText(String.valueOf(type));
                etText.setTypeface(Typeface.DEFAULT);
                break;

            case R.id.BoldButton:
                type = 2;
                textView.setText(String.valueOf(type));
                etText.setTypeface(etText.getTypeface(), Typeface.BOLD);
                break;

            case R.id.ItalicButton:
                type = 3;
                textView.setText(String.valueOf(type));
                etText.setTypeface(etText.getTypeface(), Typeface.ITALIC);
                break;
        }
    }
}