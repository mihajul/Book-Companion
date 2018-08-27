package com.udacity.bookcompanion.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class BookContentProvider extends ContentProvider {

    public static final int BOOKS = 100;
    public static final int NOTES = 101;
    public static final int NOTE_WITH_ID = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.PATH_NOTES, NOTES);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.PATH_NOTES + "/#", NOTES);

        return uriMatcher;
    }

    private BookDbHelper mBookDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mBookDbHelper = new BookDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mBookDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case BOOKS:
                long id = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case NOTES:
                long noteId = db.insert(BookContract.NoteEntry.TABLE_NAME, null, values);
                if ( noteId > 0 ) {
                    returnUri = ContentUris.withAppendedId(BookContract.NoteEntry.CONTENT_URI, noteId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mBookDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case BOOKS:
                retCursor =  db.query(BookContract.BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case NOTES: {
                retCursor =  db.query(BookContract.NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mBookDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted;

        switch (match) {
            case NOTE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(BookContract.NoteEntry.TABLE_NAME, BookContract.NoteEntry._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}