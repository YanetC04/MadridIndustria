package com.proyectointegrador.madridindustria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class localDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "patrimonio";
    private static final int DATABASE_VERSION = 2;

    private static final String CREATE_TABLE_FAVORITES =
            "CREATE TABLE favorites " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT, " +
            "love TEXT, " +
            "inaguracion TEXT, " +
            "patrimonio TEXT, " +
            "metro TEXT, " +
            "direccion TEXT, " +
            "distrito TEXT, " +
            "imagen TEXT);";

    public localDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}