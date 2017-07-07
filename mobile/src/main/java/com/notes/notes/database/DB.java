package com.notes.notes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

    // LOG TAG
    private static final String LOG_TAG = "DataBase";

    // Array
    public String[] Items = new String[6];

    private static final String DB_NAME = "myDB";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "notes";
    private static final String TRASH_TABLE = "trash";

    // DataFields
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "typeface";
    private static final String COLUMN_MARK = "mark";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_COLOR = "color";

    public static String getColumnId() {
        return COLUMN_ID;
    }

    public static String getColumnTitle() {
        return COLUMN_TITLE;
    }

    public static String getColumnText() {
        return COLUMN_TEXT;
    }

    public static String getColumnDate() {
        return COLUMN_DATE;
    }

    public static String getColumnType() {
        return COLUMN_TYPE;
    }

    public static String getColumnMark() {
        return COLUMN_MARK;
    }

    public static String getColumnImage() {
        return COLUMN_IMAGE;
    }

    public static String getColumnColor() {
        return COLUMN_COLOR;
    }

    // Create Table
    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " text, " +
                    COLUMN_TEXT + " text, " +
                    COLUMN_DATE + " text, " +
                    COLUMN_TYPE + " integer, " +
                    COLUMN_MARK + " integer, " +
                    COLUMN_IMAGE + " integer, " +
                    COLUMN_COLOR + " text" +
                    ");";

    private static final String TRASH_CREATE =
            "create table if not exists " + TRASH_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " text, " +
                    COLUMN_TEXT + " text, " +
                    COLUMN_DATE + " text, " +
                    COLUMN_TYPE + " integer, " +
                    COLUMN_MARK + " integer, " +
                    COLUMN_IMAGE + " integer, " +
                    COLUMN_COLOR + " text" +
                    ");";

    private final Context mCtx;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null)
            mDBHelper.close();
    }

    // Получение данных по Id
    public void getItem(long id) {
        mDB = mDBHelper.getWritableDatabase();
        //выставляем курсор на запись
        Cursor cursor = mDB.query(DB_TABLE, new String[]{COLUMN_ID,
                        COLUMN_TITLE, COLUMN_TEXT, COLUMN_TYPE, COLUMN_MARK, COLUMN_IMAGE, COLUMN_COLOR},
                COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        Items[0] = cursor.getString(1);
        Items[1] = cursor.getString(2);
        Items[2] = cursor.getString(3);
        Items[3] = cursor.getString(4);
        Items[4] = cursor.getString(5);
        Items[5] = cursor.getString(6);
        cursor.moveToNext();
        //TODO: TEST
        cursor.close();
    }

    // Поиск данных
    public Cursor searchItem(String value) {
        Log.d(LOG_TAG, "DB.searchItem = " + value);
        return mDB.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE " + COLUMN_TEXT + " LIKE '%" + value + "%' ORDER BY _id DESC", null);
    }

    // Получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, "date DESC, _id DESC");
    }

    // Получить все данные из таблицы DB_TABLE с отмеченным CheckBox
    public Cursor getMarkedData() {
        return mDB.query(DB_TABLE, null, "mark = 1", null, null, null, "date DESC,_id DESC");
    }

    // Получить все данные из таблицы TRASH_TABLE
    public Cursor getTrashData() {
        Cursor cursor;
        cursor = mDB.query(TRASH_TABLE, null, null, null, null, null, "date DESC,_id DESC");
        return cursor;
    }

    // Добавить запись в DB_TABLE
    public void addRec(String title, String txt, String date, String style, String mark, int image, String color) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TEXT, txt);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TYPE, style);
        cv.put(COLUMN_MARK, mark);
        cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_COLOR, color);

        mDB.insert(DB_TABLE, null, cv);
        mDB.close();

        Log.d(LOG_TAG, "Insert: " + "Title: " + title + ", Text: " + txt + ", Date: " + date +
                ", Style: " + style + ", Mark: " + mark + ", Image URI: " + image + ", Color: " + color);
    }

    // Обновить запись в DB_TABLE
    public void updRec(long id, String title, String txt, String date, String style, String mark, int image, String color) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TEXT, txt);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TYPE, style);
        cv.put(COLUMN_MARK, mark);
        cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_COLOR, color);

        mDB.update(DB_TABLE, cv, COLUMN_ID + "=" + id, null);
    }

    // Удалить запись из DB_TABLE
    public void delRec(long id) {

        Cursor cursor = mDB.query(DB_TABLE, new String[]{COLUMN_ID,
                        COLUMN_TITLE, COLUMN_TEXT, COLUMN_DATE, COLUMN_TYPE, COLUMN_MARK, COLUMN_IMAGE, COLUMN_COLOR},
                COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(1);
        String text = cursor.getString(2);
        String date = cursor.getString(3);
        String type = cursor.getString(4);
        String mark = cursor.getString(5);
        String image = cursor.getString(6);
        String color = cursor.getString(7);
        cursor.moveToNext();

        Log.d(LOG_TAG, "GET Id = " + id + " title = " + title + " text = " + text + " Date = " + date + " Type = " + type + " Mark = " + mark + " Color = " + color);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TEXT, text);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_MARK, mark);
        cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_COLOR, color);
        mDB.insert(TRASH_TABLE, null, cv);

        Log.d(LOG_TAG, "INSERT TRASH Title = " + title + " Text = " + text +
                " Date = " + date + " Type = " + type +
                " Mark = " + mark + " Image = " + image +
                " Color = " + color);

        mDB.delete(DB_TABLE, COLUMN_ID + "=" + id, null);

        //TODO: TEST
        cursor.close();
    }

    // Восстановить запись из TRASH_TABLE
    public void restoreData(long id) {

        Cursor cursor = mDB.query(TRASH_TABLE, new String[]{COLUMN_ID,
                        COLUMN_TITLE, COLUMN_TEXT, COLUMN_DATE, COLUMN_TYPE, COLUMN_MARK, COLUMN_IMAGE, COLUMN_COLOR},
                COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(1);
        String text = cursor.getString(2);
        String date = cursor.getString(3);
        String type = cursor.getString(4);
        String mark = cursor.getString(5);
        String image = cursor.getString(6);
        String color = cursor.getString(7);
        cursor.moveToNext();

        Log.d(LOG_TAG, "GET Title = " + title + " Text = " + text +
                " Date = " + date + " Type = " + type +
                " Mark = " + mark + " Image = " + image +
                " Color = " + color);

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TEXT, text);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_MARK, mark);
        cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_COLOR, color);
        mDB.insert(DB_TABLE, null, cv);

        Log.d(LOG_TAG, " INSERT TRASH Title = " + title + " Text = " + text +
                " Date = " + date + " Type = " + type +
                " Mark = " + mark + " Image = " + image +
                " Color = " + color);

        mDB.delete(TRASH_TABLE, COLUMN_ID + "=" + id, null);

        //TODO: Attention !!! test this !!!
        cursor.close();

        mDB.close();
    }

    // Очистить таблицу Trash
    public void deleteAll() {
        mDB.delete(TRASH_TABLE, null, null);
        mDB.close();
    }

    // Класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, CursorFactory factory,
                 int version) {
            super(context, name, factory, version);
        }

        // Создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            db.execSQL(TRASH_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}