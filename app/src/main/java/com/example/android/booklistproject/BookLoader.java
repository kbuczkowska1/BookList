package com.example.android.booklistproject;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Kasia on 2017-07-11.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private String Url;
    private String Search;

    // Constructs a new {@link BookLoader}
    public BookLoader(Context context, String url, String search) {
        super(context);
        Url = url;
        Search = search;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (Url == null || Search == null) return null;
        return Utils.fetchBookData(Url + Search);
    }

}