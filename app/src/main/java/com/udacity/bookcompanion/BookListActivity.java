package com.udacity.bookcompanion;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.udacity.bookcompanion.adapter.BookRecyclerViewAdapter;
import com.udacity.bookcompanion.loader.BookListLoader;
import com.udacity.bookcompanion.model.Book;
import com.udacity.bookcompanion.utils.PreferencesUtils;

import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;

    private static final int LOADER_ID = 0;
    public static final String BOOK_RESULT_KEY = "book";
    private static final String LOG_TAG = BookListActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        recyclerView = (RecyclerView) findViewById(R.id.book_list);

        setupRecyclerView((RecyclerView) recyclerView);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        FloatingActionButton fab = findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookListActivity.this, BookAddActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        bookRecyclerViewAdapter = new BookRecyclerViewAdapter(this, mTwoPane);
        recyclerView.setAdapter(bookRecyclerViewAdapter);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, final Bundle loaderArgs) {
        return new BookListLoader(this, mLoadingIndicator);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        ((BookRecyclerViewAdapter) recyclerView.getAdapter()).setData(data);

        if (null == data) {
            showErrorMessage();
        } else {
            showGridView();
        }
    }

    private void showGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, BookListActivity.this);
            return true;
        } else if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.sortby_dialog);
        dialog.show();

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        int radioId = R.id.sort_by_created_date;
        if (PreferencesUtils.getSort(this) == PreferencesUtils.SORT_BY_TITLE) {
            radioId = R.id.sort_by_title;
        }
        rg.check(radioId);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int sort = PreferencesUtils.SORT_BY_DATE_CREATED;
                if (checkedId == R.id.sort_by_title) {
                    sort = PreferencesUtils.SORT_BY_TITLE;
                }
                PreferencesUtils.setSort(BookListActivity.this, sort);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        getSupportLoaderManager().restartLoader(LOADER_ID, null, BookListActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            getSupportLoaderManager().restartLoader(LOADER_ID, null, BookListActivity.this);
        }
    }




}
