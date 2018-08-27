package com.udacity.bookcompanion.utils;

import android.util.Log;

import com.udacity.bookcompanion.model.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

    private static final String ITEMS = "items";
    private static final String VOLUME_INFO = "volumeInfo";
    private static final String TITLE = "title";
    private static final String AUTHORS = "authors";
    private static final String IMAGE_LINKS = "imageLinks";
    private static final String THUMBNAIL = "thumbnail";

    public static List<Book> parseBooksJson(String json) {

        if (json != null) {
            List<Book> books = new ArrayList<>();
            try {

                JSONObject jsonObj = new JSONObject(json);
                JSONArray results = jsonObj.getJSONArray(ITEMS);

                for (int i = 0; i < results.length(); i++) {
                    try {
                            JSONObject bookObj = results.getJSONObject(i);
                            JSONObject volumeObj = bookObj.getJSONObject(VOLUME_INFO);

                            Book book = new Book();
                            book.setTitle(volumeObj.getString(TITLE));

                            book.setAuthor("N/A");
                            if (volumeObj.has(AUTHORS)) {
                                JSONArray authors = volumeObj.getJSONArray(AUTHORS);
                                if (authors.length() > 0) {
                                    book.setAuthor(authors.getString(0));
                                }
                            }

                            if (volumeObj.has(IMAGE_LINKS)) {
                                JSONObject imageLinks = volumeObj.getJSONObject(IMAGE_LINKS);
                                String thumbnailUrl = imageLinks.getString(THUMBNAIL);
                                book.setThumbnailUrl(thumbnailUrl);
                            }

                            books.add(book);
                    }catch (Exception cannotParseItem) {
                        Log.e( LOG_TAG , cannotParseItem.getMessage());
                    }
                }
                return  books;
            }catch (Exception e) {
                Log.e( LOG_TAG , e.getMessage());
            }
        }

        return null;
    }

}