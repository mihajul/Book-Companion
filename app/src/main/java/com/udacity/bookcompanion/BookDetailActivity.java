package com.udacity.bookcompanion;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.udacity.bookcompanion.adapter.BookRecyclerViewAdapter;
import com.udacity.bookcompanion.adapter.NotesRecyclerViewAdapter;
import com.udacity.bookcompanion.loader.BookListLoader;
import com.udacity.bookcompanion.loader.NoteListLoader;
import com.udacity.bookcompanion.model.Book;
import com.udacity.bookcompanion.model.Note;

import java.util.List;

/**
 * An activity representing a single Book detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BookListActivity}.
 */
public class BookDetailActivity extends AppCompatActivity{

    private Book book;
    Tracker mTracker;
    private static final String TAG = BookDetailActivity.class.getName();

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + TAG);
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        BookCompanionApplication application = (BookCompanionApplication) getApplication();
        mTracker = application.getDefaultTracker();

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            book = getIntent().getParcelableExtra(BookDetailFragment.ARG_BOOK);


            Bundle arguments = new Bundle();
            arguments.putParcelable(BookDetailFragment.ARG_BOOK, book);
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();

            populateWidgets();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setEnterTransition(new Explode());
                getWindow().setExitTransition(new Explode());
            }

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, BookListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateWidgets() {

        Log.d(TAG, "Setting widget book: " + String.valueOf(book));
        BookCompanionWidget.currentBook = book;
        AppWidgetManager man = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = man.getAppWidgetIds(new ComponentName(getApplicationContext(),BookCompanionWidget.class));

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setExtrasClassLoader(Book.class.getClassLoader());
        widgetUpdateIntent.setAction(BookCompanionWidget.BOOK_SELECTED);
        widgetUpdateIntent.putExtra(BookCompanionWidget.BOOK_KEY, book);
        widgetUpdateIntent.putExtra(BookCompanionWidget.WIDGET_IDS_KEY, ids);
        sendBroadcastCompat(this, widgetUpdateIntent);

    }
    public static void sendBroadcastCompat(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            context.sendBroadcast(intent);
            return;
        }

        Intent broadcastIntent = new Intent(intent);
        PackageManager pm = context.getPackageManager();

        List<ResolveInfo> broadcastReceivers  = pm.queryBroadcastReceivers(broadcastIntent, 0);
        for(ResolveInfo info : broadcastReceivers) {
            broadcastIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            context.sendBroadcast(broadcastIntent);
        }
    }
}
