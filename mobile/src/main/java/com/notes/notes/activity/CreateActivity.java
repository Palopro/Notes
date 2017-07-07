package com.notes.notes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    // LOG
    final static String LOG_TAG = "CreateActivity";

    NestedScrollView scrollView;
    ColorPickerDialog colorPickerDialog;

    public EditText etTitle, etText;
    public TextView textView;
    public CheckBox checkBox;

    Button btnNormal, btnBold, btnItalic;

    public String dateNow;

    final int[] colors = new int[]{
            Color.parseColor("#FAFAFA"), Color.parseColor("#FF8A80"), Color.parseColor("#FFD180"), Color.parseColor("#FFFF8D"),
            Color.parseColor("#CCFF90"), Color.parseColor("#A7FFEB"), Color.parseColor("#80D8FF"), Color.parseColor("#CFD8DC")};

    int selectedColor = Color.parseColor("#FAFAFA");

    String bgColor = "#FAFAFA";

    DB db;

    CharacterStyle styleBold;

    //NotificationManager mNotificationManager;
    Date currentDate = new Date();

    public CreateActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        initToolBar();

        scrollView = (NestedScrollView) findViewById(R.id.NestedScroll);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etText = (EditText) findViewById(R.id.etText);

        textView = (TextView) findViewById(R.id.textView);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        etText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        btnNormal = (Button) findViewById(R.id.NormalButton);
        btnNormal.setOnClickListener(this);

        btnBold = (Button) findViewById(R.id.BoldButton);
        btnBold.setOnClickListener(this);

        btnItalic = (Button) findViewById(R.id.ItalicButton);
        btnItalic.setOnClickListener(this);

        styleBold = new StyleSpan(Typeface.BOLD);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateNow = (simpleDateFormat.format(currentDate));

        db = new DB(this);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Create);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_color:

                colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.initialize(R.string.SelectColor, colors, selectedColor, 4, colors.length);
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

            case R.id.action_create:

                String title = etTitle.getText().toString();
                String text = etText.getText().toString();
                String Stype = textView.getText().toString();
                String date = dateNow;
                int mark;
                int image;

                // открываем подключение к БД
                db = new DB(getApplication());
                db.open();

                Log.d(LOG_TAG, "---Insert in table---");

                if (checkBox.isChecked()) {
                    mark = 1;
                    image = R.drawable.fav_24x24_fill;
                } else {
                    mark = 0;
                    image = R.drawable.fav_24x24;
                }

                switch (item.getItemId()) {

                    case R.id.NormalButton:
                        int type = 1;
                        textView.setText(String.valueOf(type));
                        etText.setTypeface(Typeface.DEFAULT);
                        break;

                    case R.id.BoldButton:
                        type = 2;
                        textView.setText(String.valueOf(type));
                        Editable editable = etText.getText();
                        int startpos = etText.getSelectionStart();
                        int endpos = etText.getSelectionEnd();

                        int editStart = editable.getSpanStart(startpos);
                        int editEnd = editable.getSpanEnd(endpos);
                        Log.d(LOG_TAG, editStart + " " + editEnd);
                        if (startpos == editStart && endpos == editEnd) {
                            editable.setSpan(new StyleSpan(Typeface.BOLD), editStart, editEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        }

                        Spannable str = etText.getText();
                        str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startpos,
                                endpos, 0);

                        break;

                    case R.id.ItalicButton:
                        type = 3;
                        textView.setText(String.valueOf(type));
                        etText.setTypeface(etText.getTypeface(), Typeface.ITALIC);
                        break;
                }

                db.addRec(title, text, date, Stype, String.valueOf(mark), image, bgColor);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                this.finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.NormalButton:
                int type = 1;
                textView.setText(String.valueOf(type));
                etText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                break;

            case R.id.BoldButton:
                type = 2;
                textView.setText(String.valueOf(type));
                String text = etText.getText().toString();
                btnBold.setFocusable(true);
                int start = etText.getSelectionStart();
                int end = etText.getSelectionEnd();

                SpannableStringBuilder sb = new SpannableStringBuilder(text);

                sb.setSpan(styleBold, start, end, 0);
                etText.setText(sb);

               /* etText.setTypeface(etText.getTypeface(), Typeface.BOLD);*/
                break;

            case R.id.ItalicButton:
                type = 3;
                textView.setText(String.valueOf(type));
                etText.setTypeface(etText.getTypeface(), Typeface.ITALIC);
                break;
        }
    }
}