package com.example.android.booklistproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    // Constructs a new {@link BookAdapter}
    BookAdapter(Context context, List<Book> book) {
        super(context, 0, book);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView authors;
        TextView titles;
        TextView subtitles;
        RatingBar ratingBar;
    }

    //Returns a list item view that displays information about the book at the given position
    //in the list of books.
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.books_list_item, parent, false);
            holder = new ViewHolder();
            holder.authors = (TextView) convertView.findViewById(R.id.author);
            holder.titles = (TextView) convertView.findViewById(R.id.title);
            holder.subtitles = (TextView) convertView.findViewById(R.id.subTitle);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Book currentBook = getItem(position);

        assert currentBook != null;
        if (!currentBook.getmTitles().isEmpty())
            holder.titles.setText(currentBook.getmTitles());

        if (!currentBook.getmAuthors().isEmpty())
            holder.authors.setText(currentBook.getmTitles());

        if (!currentBook.getmSubtitles().isEmpty())
            holder.subtitles.setText(currentBook.getmSubtitles());

        float averageRating = (float) currentBook.getmRatings();
        holder.ratingBar.setRating(averageRating);

        return convertView;
    }
}

