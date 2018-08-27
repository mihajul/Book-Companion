package com.udacity.bookcompanion.loader;

import android.os.AsyncTask;

import com.udacity.bookcompanion.model.Book;
import com.udacity.bookcompanion.utils.JsonUtils;
import com.udacity.bookcompanion.utils.NetworkUtils;

import java.util.List;

public class BookQueryAsyncTask extends AsyncTask<String, Void, List<Book>> {

    public BookQueryAsyncTask() {

    }

    @Override
    protected List<Book> doInBackground(String... strings) {
        String json = NetworkUtils.getBooksJson(strings[0]);
        List<Book> books = JsonUtils.parseBooksJson(json);
        return books;
    }

    @Override
    protected void onPostExecute(List<Book> result) {

    }
}
