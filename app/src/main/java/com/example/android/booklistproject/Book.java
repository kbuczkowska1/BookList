package com.example.android.booklistproject;

/**
 * Created by Kasia on 2017-07-11.
 */

//An {@link Book} object contains information related to a single book
public class Book {

    // Author of the book.
    private String mAuthors;

    // Title of the book.
    private String mTitles;

    //Subtitle of the book.
    private String mSubtitles;

    // Book evaluation.
    private double mRatings;


    //Create a new {@link Book} object.
    public Book (String authors, String titles, String subtitles, double ratings) {

        mAuthors = authors;
        mTitles = titles;
        mSubtitles = subtitles;
        mRatings = ratings;
    }

    public String getmAuthors() {
        return mAuthors;
    }

    // Getter methods to return.
    public String getmTitles() {
        return mTitles;
    }

    public String getmSubtitles() {
        return mSubtitles;
    }

    public double getmRatings() {
        return mRatings;
    }
}