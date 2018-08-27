package com.udacity.bookcompanion.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.bookcompanion.BookDetailActivity;
import com.udacity.bookcompanion.BookDetailFragment;
import com.udacity.bookcompanion.BookListActivity;
import com.udacity.bookcompanion.R;
import com.udacity.bookcompanion.model.Book;

import java.util.List;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder> {

    private final BookListActivity mParentActivity;
    private List<Book> mValues;
    private final boolean mTwoPane;

    public BookRecyclerViewAdapter(BookListActivity parent,
                                    boolean twoPane) {
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Book book = mValues.get(position);
        holder.mTextView.setVisibility(View.VISIBLE);
        int width = (int) mParentActivity.getResources().getDimension(R.dimen.column_width);
        int height = (int) ((double)width * 1.5);
        Picasso.with(mParentActivity)
                .load(book.getThumbnailUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .resize(width, height)
                .centerInside()
                .into(holder.mContentView , new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.mTextView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.mTextView.setVisibility(View.VISIBLE);
                    }
                });
        holder.mContentView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        holder.mTextView.setText(book.getTitle());
        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   showDetailActivity(view, book);
                                               }
                                           }
        );
    }

    private void showDetailActivity(View view, Book book) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(BookDetailFragment.ARG_BOOK, book);
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.book_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra(BookDetailFragment.ARG_BOOK, book);
            mParentActivity.startActivityForResult(intent, 0);
        }
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public List<Book> getData() {
        return mValues;
    }

    public void setData(List<Book> data) {
        mValues = data;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mContentView;
        final TextView mTextView;

        ViewHolder(View view) {
            super(view);
            mContentView = (ImageView) view.findViewById(R.id.content);
            mTextView = (TextView) view.findViewById(R.id.title);
        }
    }


}