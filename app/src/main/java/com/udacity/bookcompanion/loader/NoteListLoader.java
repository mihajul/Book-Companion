package com.udacity.bookcompanion.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.ProgressBar;

import com.udacity.bookcompanion.data.BookContract;
import com.udacity.bookcompanion.model.Note;
import com.udacity.bookcompanion.model.NoteType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteListLoader extends AsyncTaskLoader<List<Note>> {
    List<Note> notes = null;
    ProgressBar mLoadingIndicator;
    int bookId;

    public NoteListLoader(int bookId, Context context, ProgressBar mLoadingIndicator) {
        super(context);
        this.bookId = bookId;
        this.mLoadingIndicator = mLoadingIndicator;
    }

    @Override
    protected void onStartLoading() {
        if (notes != null) {
            deliverResult(notes);
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }


    @Override
    public List<Note> loadInBackground() {

        try {
            List<Note> notes = getNotes();
            return notes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Note> getNotes() {
        String sortOrder = BookContract.NoteEntry._ID + " ASC";

        String selection = "book_id = ?";
        String[] selectionArgs = new String[] { String.valueOf(bookId) };

        Cursor cursor = getContext().getContentResolver().query(BookContract.NoteEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                sortOrder);

        if(cursor == null) {
            return null;
        }
        List<Note> notes= new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        while (cursor.moveToNext()) {
            Note note = new Note();

            try {
                note.setId(cursor.getInt(cursor.getColumnIndex(BookContract.NoteEntry._ID)));
                int noteType = cursor.getInt(cursor.getColumnIndex(BookContract.NoteEntry.COLUMN_NOTE_TYPE));
                note.setNoteType(NoteType.parseInt(noteType));
                note.setText(cursor.getString(cursor.getColumnIndex(BookContract.NoteEntry.COLUMN_TEXT)));
                note.setBookId(cursor.getInt(cursor.getColumnIndex(BookContract.NoteEntry.COLUMN_BOOK_ID)));
                note.setCreatedAt(sdf.parse(cursor.getString(cursor.getColumnIndex(BookContract.NoteEntry.COLUMN_CREATED_AT))));
            }catch(Exception e) {
                e.printStackTrace();
            }
            notes.add(note);
        }
        cursor.close();
        return notes;
    }

    public void deliverResult(List<Note> data) {
        notes = data;
        super.deliverResult(notes);
    }
}