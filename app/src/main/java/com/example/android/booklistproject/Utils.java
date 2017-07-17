package com.example.android.booklistproject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kasia on 2017-07-11.
 */

// Utility class with methods to help perform the HTTP request and parse the response.
public final class Utils {

    // Tag for the log messages.
    public static final String LOG_TAG = Utils.class.getSimpleName();

    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_VOLUME_INFO = "volumeInfo";
    private static Context context;


    // Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

             //If the request was successful (response code 200), then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null)
                inputStream.close();
        }

        return jsonResponse;
    }

     //Convert the {@link InputStream} into a String which contains the whole JSON response from th server.
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that have been built up from parsing a JSON response.
     */

    private static List<Book> extractFeatureFromJson(String bookJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON))
            return null;

        //Create an empty ArrayList that we can start adding books to.
        List<Book> books = new ArrayList<>();
        try {
            // Create a JSONObject from the JSON response string.
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            JSONArray bookArray = baseJsonResponse.getJSONArray(KEY_ITEMS);

            for (int i = 0; i < bookArray.length(); i++) {

                // Get book JSONObject at position i.
                JSONObject currentBook = bookArray.getJSONObject(i);

                JSONObject volumeInfo = currentBook.getJSONObject(KEY_VOLUME_INFO);

                // Extract "authors" under "volumeInfo" for author.
                String authors = "";
                if (volumeInfo.has(KEY_AUTHORS)) {
                    JSONArray authorsArray = volumeInfo.getJSONArray(KEY_AUTHORS);
                    authors = authorsArray.toString().replace("[", "").replace("]", "");
                }else
                    sendAuthorsMessage();

                // Extract "title" under "volumeInfo" for title.
                String title = "";
                if (volumeInfo.has(KEY_TITLE)) {
                    title = volumeInfo.getString(KEY_TITLE);
                }else
                    sendTitleMessage();

                // Extract "subtitle" under "volumeInfo" for subtitle.
                String subtitle = "";
                if (volumeInfo.has(KEY_SUBTITLE)){
                    subtitle = volumeInfo.getString(KEY_SUBTITLE);
                }else
                    sendSubtitleMessage();

                double rating;

                if (volumeInfo.has("averageRating")) {
                    rating = volumeInfo.getDouble("averageRating");
                } else {
                    rating = 0;
                }

                /*
                  Create a new {@link Book} object with the title, subtitle, author
                  from the JSON response.
                 */
                Book book = new Book(authors, title, subtitle, rating);

                // Add book to list of books
                books.add(book);

            }

        } catch (JSONException e) {
            /*
             * If an error is thrown when executing any of the above statements in the "try" block,
             * catch the exception here, so the app doesn't crash. Print a log message with the
             * message from the exception.
             */
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);



        }

        // Return the list of books
        return books;

    }

     //Query the Google dataset and return a list of {@link Book} objects.
    public static List<Book> fetchBookData(String requestUrl) {

        Log.i(LOG_TAG, "TEST: fetchBookData() called...");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object.
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        /*
          Extract relevant fields from the JSON response and create a list of {@link Book}s.
         */

        /*
          Return the list of {@link Book}s.
         */
        return extractFeatureFromJson(jsonResponse);
    }

    public static void context(Context context){
        Utils.context = context;
    }

    private static void sendTitleMessage(){
        Intent intent = new Intent("parsing-error");
        intent.putExtra("message", "Could not display book title.");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private static void sendSubtitleMessage(){
        Intent intent = new Intent("parsing-error");
        intent.putExtra("message", "Could not display book subtitle.");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    private static void sendAuthorsMessage(){
        Intent intent = new Intent("parsing-error");
        intent.putExtra("message", "Could not display book authors.");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

}