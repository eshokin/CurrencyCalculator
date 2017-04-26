package com.example.calculator.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.calculator.CalculatorApplication;
import com.example.calculator.models.Valute;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "valutes.db";
    private static final String TABLE_NAME = "valutes";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NUM_CODE = "NumCode";
    private static final String COLUMN_CHAR_CODE = "CharCode";
    private static final String COLUMN_NOMINAL = "Nominal";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_VALUE = "Value";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_NUM_CODE + " TEXT, " + COLUMN_CHAR_CODE + " TEXT, " +
            COLUMN_NOMINAL + " INTEGER DEFAULT 0, " + COLUMN_NAME + " TEXT, " + COLUMN_VALUE + " TEXT)";
    private static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String SELECT_QUERY = "SELECT  * FROM " + TABLE_NAME;

    private static DBHelper instance;

    public static synchronized DBHelper instance() {
        return instance == null ? instance = new DBHelper(CalculatorApplication.getApplication()) : instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public synchronized List<Valute> getValuteList() {
        List<Valute> mValuteList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        if (cursor.moveToFirst()) {
            do {
                Valute valute = new Valute();
                valute.setNumCode(cursor.getString(cursor.getColumnIndex(COLUMN_NUM_CODE)));
                valute.setCharCode(cursor.getString(cursor.getColumnIndex(COLUMN_CHAR_CODE)));
                valute.setNominal(cursor.getInt(cursor.getColumnIndex(COLUMN_NOMINAL)));
                valute.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                valute.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
                mValuteList.add(valute);
                cursor.moveToNext();
            } while (cursor.moveToNext());
        }
        return mValuteList;
    }

    public synchronized void updateValutes(List<Valute> valutes) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DROP_TABLE_QUERY);
        db.execSQL(CREATE_TABLE_QUERY);
        if (valutes != null && valutes.size() > 0) {
            for (Valute valute : valutes) {
                addValute(valute);
            }
        }
    }

    private synchronized void addValute(Valute valute) {
        if (valute != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValute = new ContentValues();
            contentValute.put(COLUMN_NUM_CODE, valute.getNumCode());
            contentValute.put(COLUMN_CHAR_CODE, valute.getCharCode());
            contentValute.put(COLUMN_NOMINAL, valute.getNominal());
            contentValute.put(COLUMN_NAME, valute.getName());
            contentValute.put(COLUMN_VALUE, valute.getValue());
            db.insert(TABLE_NAME, null, contentValute);
        }
    }
}
