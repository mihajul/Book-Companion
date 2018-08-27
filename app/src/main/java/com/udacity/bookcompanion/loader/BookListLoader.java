package com.udacity.bookcompanion.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.ProgressBar;

import com.udacity.bookcompanion.data.BookContract;
import com.udacity.bookcompanion.model.Book;
import com.udacity.bookcompanion.utils.PreferencesUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookListLoader extends AsyncTaskLoader<List<Book>> {
    List<Book> books = null;
    ProgressBar mLoadingIndicator;

    public BookListLoader(Context context, ProgressBar mLoadingIndicator) {
        super(context);
        this.mLoadingIndicator = mLoadingIndicator;
    }

    @Override
    protected void onStartLoading() {
        if (books != null) {
            deliverResult(books);
        } else {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }


    @Override
    public List<Book> loadInBackground() {

        try {
            List<Book> books = getBooks(PreferencesUtils.getSort(getContext()));
            return books;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Book> getBooks(int sorting) {
        String sortOrder = BookContract.BookEntry.COLUMN_CREATED_AT + " DESC";
        if(sorting == PreferencesUtils.SORT_BY_TITLE) {
            sortOrder = BookContract.BookEntry.COLUMN_TITLE + " ASC";
        }

        Cursor cursor = getContext().getContentResolver().query(BookContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                sortOrder);

        if(cursor == null) {
            return null;
        }
        List<Book> books= new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        while (cursor.moveToNext()) {
            Book book = new Book();

            try {
                book.setId(cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID)));
                book.setTitle(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR)));
                book.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_THUMBNAIL_URL)));
                book.setCreatedAt(sdf.parse(cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_CREATED_AT))));
            }catch(Exception e) {
                e.printStackTrace();
            }
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public void deliverResult(List<Book> data) {
        books = data;
        super.deliverResult(books);
    }
}