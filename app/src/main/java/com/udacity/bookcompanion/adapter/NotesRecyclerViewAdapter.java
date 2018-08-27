package com.udacity.bookcompanion.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.bookcompanion.R;
import com.udacity.bookcompanion.model.Note;
import com.udacity.bookcompanion.model.NoteType;

import java.util.List;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private List<Note> mValues;

    public NotesRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Note note = mValues.get(position);

        holder.text.setVisibility(View.GONE);
        holder.chapter.setVisibility(View.GONE);
        holder.image.setVisibility(View.GONE);

        holder.cardView.setCardBackgroundColor(Color.WHITE);

        if(note.getNoteType() == NoteType.CHAPTER) {
            holder.chapter.setVisibility(View.VISIBLE);
            holder.chapter.setText(note.getText());
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.chapter));
        }

        if(note.getNoteType() == NoteType.TEXT) {
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText(note.getText());
        }

        if(note.getNoteType() == NoteType.IMAGE) {
            holder.image.setVisibility(View.VISIBLE);
            int width = (int) context.getResources().getDimension(R.dimen.column_width);
            int height = (int) ((double)width * 1.5);
            Picasso.with(context)
                    .load(note.getText())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .resize(width, height)
                    .centerInside()
                    .into(holder.image);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }



        holder.itemView.setTag(mValues.get(position));
    }


    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public List<Note> getData() {
        return mValues;
    }

    public void setData(List<Note> data) {
        mValues = data;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView text;
        final TextView chapter;
        final CardView cardView;

       ViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            text = (TextView) view.findViewById(R.id.text);
            chapter = (TextView) view.findViewById(R.id.chapter);
            cardView = (CardView) view.findViewById(R.id.card_view);

        }
    }


}