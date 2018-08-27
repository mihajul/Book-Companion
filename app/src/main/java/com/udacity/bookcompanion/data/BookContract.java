package com.udacity.bookcompanion.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {

    public static final String AUTHORITY = "com.udacity.bookcompanion";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_BOOKS = "books";
    public static final String PATH_NOTES = "notes";

    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build();

        public static final String TABLE_NAME = "books";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_CREATED_AT = "created_at";

    }

    public static final class NoteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTES).build();

        public static final String TABLE_NAME = "notes";

        public static final String COLUMN_BOOK_ID = "book_id";
        public static final String COLUMN_NOTE_TYPE = "note_type";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_CREATED_AT = "created_at";

    }
}
