package com.example.android.booklistproject;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    // Declare base url as a constant.
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static final int BOOK_LOADER_ID = 1;

    private View mProgressSpinner;

    private TextView mEmptyTextView;

    private BookAdapter mAdapter;

    private String mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText search = (EditText) findViewById(R.id.search);

        final ImageButton button = (ImageButton) findViewById(R.id.imageButton);

        ListView booksListView = (ListView) findViewById(R.id.bookList);

        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyTextView);

        mProgressSpinner = findViewById(R.id.loading_spinner);
        mProgressSpinner.setVisibility(View.GONE);

        mEmptyTextView.setText(R.string.enter_search);

        // Create a new adapter that takes an empty list of books as input.
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        booksListView.setAdapter(mAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);

        button.setOnClickListener(new ImageButton.OnClickListener() {

            public void onClick(View view) {

                String searchString = search.getText().toString();

                mSearch = searchString.trim();
                mEmptyTextView.setText("");
                mProgressSpinner.setVisibility(View.VISIBLE);

                // Get a reference to the ConnectivityManager to check state of network connectivity.
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details.
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                // If there is a network connection, fetch the data.
                if (networkInfo != null && networkInfo.isConnected()) {

                    // Restart the loader.
                    getLoaderManager().restartLoader(0, null, MainActivity.this);
                } else {
                    // clear the adapter so user can tell if connectivity is lost between searches.
                    mAdapter.clear();

                    // Update empty state with no connection error message.
                    mEmptyTextView.setText(R.string.no_internet_connection);

                    // Display error. First, hide loading indicator so error message will be visible.
                    mProgressSpinner.setVisibility(View.GONE);

                }
            }
        });

        // Register to receive messages. We are registering an observer (MessageReceiver) to
        // receive Intents with actions named "parsing-error".
        LocalBroadcastManager.getInstance(this).registerReceiver(MessageReceiver,
                new IntentFilter("parsing-error"));
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(MessageReceiver);
        super.onDestroy();
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {

        return new BookLoader(this, BASE_URL, mSearch);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> listOfBooks) {

        // Hide progress spinner because the data has been loaded.
        mProgressSpinner.setVisibility(View.GONE);

        // Clear the adapter of previous books data.
        mAdapter.clear();

        if (listOfBooks != null && !listOfBooks.isEmpty()) {
            mAdapter.addAll(listOfBooks);
        } else if (mSearch != null)
            mEmptyTextView.setText(R.string.no_matches_found);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    private BroadcastReceiver MessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent.
            String message = intent.getStringExtra("message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
}