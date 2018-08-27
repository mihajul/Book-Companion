package com.udacity.bookcompanion;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.udacity.bookcompanion.model.Book;

/**
 * Implementation of App Widget functionality.
 */
public class BookCompanionWidget extends AppWidgetProvider {

    public static final String BOOK_SELECTED = "com.udacity.bookcompanion.action.APPWIDGET_BOOK_SELECTED";
    public static final String BOOK_KEY = "book";
    public static final String WIDGET_IDS_KEY = "widgetIds";
    public static final String LOG_TAG = BookCompanionWidget.class.getName();
    public static Book currentBook;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "onUpdate " + appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = updateAppWidget(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateAppWidget(Context context, int appWidgetId) {


        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),  R.layout.app_widget);

        if(currentBook == null) {
            remoteViews.setViewVisibility(R.id.appwidget_empty, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.appwidget_text, View.GONE);
        }else {
            remoteViews.setViewVisibility(R.id.appwidget_empty, View.GONE);
            remoteViews.setViewVisibility(R.id.appwidget_text, View.VISIBLE);
            remoteViews.setTextViewText(R.id.appwidget_text, currentBook.getTitle());

            Intent intent = new Intent(context, BookDetailActivity.class);
            Book book = new Book();
            book.setId(currentBook.getId());
            book.setTitle(currentBook.getTitle());
            intent.putExtra(BookDetailFragment.ARG_BOOK, book);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
        }

        Intent intent = new Intent(context, BookListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_empty, pendingIntent);


        Log.d(LOG_TAG, "updateWidgetListView " + appWidgetId);
        return remoteViews;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(LOG_TAG, "onReceive " + intent.getAction());
        if (intent.getAction().equals(BOOK_SELECTED)) {
            currentBook = intent.getParcelableExtra(BOOK_KEY);
            Log.d(LOG_TAG, "currentBook " + currentBook.getTitle());

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);

            onUpdate(context, appWidgetManager, ids);
            appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.appwidget_text);
        }
        Log.d(LOG_TAG, "onReceive finished " + intent.getAction());
    }


}
