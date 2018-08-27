package com.udacity.bookcompanion.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "book.db";
    private static final int VERSION = 3;

    BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_BOOKS_TABLE = "CREATE TABLE "  + BookContract.BookEntry.TABLE_NAME + " (" +
                BookContract.BookEntry._ID                    + " INTEGER PRIMARY KEY, " +
                BookContract.BookEntry.COLUMN_TITLE           + " TEXT NOT NULL, " +
                BookContract.BookEntry.COLUMN_AUTHOR          + " TEXT NOT NULL, " +
                BookContract.BookEntry.COLUMN_THUMBNAIL_URL   + " TEXT , " +
                BookContract.BookEntry.COLUMN_CREATED_AT      + " DATETIME  NOT NULL " +
                " );";

        db.execSQL(CREATE_BOOKS_TABLE);

        final String CREATE_NOTES_TABLE = "CREATE TABLE "  + BookContract.NoteEntry.TABLE_NAME + " (" +
                BookContract.BookEntry._ID                    + " INTEGER PRIMARY KEY, " +
                BookContract.NoteEntry.COLUMN_BOOK_ID         + " INTEGER NOT NULL, " +
                BookContract.NoteEntry.COLUMN_NOTE_TYPE        + " INTEGER NOT NULL, " +
                BookContract.NoteEntry.COLUMN_TEXT             + " TEXT NOT NULL , " +
                BookContract.NoteEntry.COLUMN_CREATED_AT      + " DATETIME  NOT NULL " +
                " );";

        db.execSQL(CREATE_NOTES_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BookContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}