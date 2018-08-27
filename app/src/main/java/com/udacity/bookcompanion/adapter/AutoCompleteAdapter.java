package com.udacity.bookcompanion.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.udacity.bookcompanion.loader.BookQueryAsyncTask;
import com.udacity.bookcompanion.model.Book;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<Book> implements Filterable {
    private List<Book> mData;

    public AutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mData = new ArrayList<Book>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Book getItem(int index) {
        return mData.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    try {
                        mData = new BookQueryAsyncTask().execute(constraint.toString()).get();
                        if(mData == null) {
                            mData = new ArrayList<>();
                        }
                        filterResults.values = mData;
                        filterResults.count = mData.size();
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
}