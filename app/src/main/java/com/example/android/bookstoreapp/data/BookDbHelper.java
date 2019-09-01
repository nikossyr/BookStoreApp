package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bookshelf.db";

    private static final int DB_VERSION_NUMBER = 1;

    public BookDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CONSTRUCTOR = "CREATE TABLE books ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BookEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL,"
                + BookEntry.COLUMN_BOOK_AUTHOR + " TEXT NOT NULL,"
                + BookEntry.COLUMN_BOOK_PRICE + " REAL NOT NULL DEFAULT 0.0,"
                + BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT,"
                + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT"
                + ")";

        Log.v("BookBDHelper", "Constructor" + CONSTRUCTOR);

        db.execSQL(CONSTRUCTOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BookDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS books");
        onCreate(db);
    }
}
