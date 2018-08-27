package com.udacity.bookcompanion;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.udacity.bookcompanion.adapter.AutoCompleteAdapter;
import com.udacity.bookcompanion.data.BookContract;
import com.udacity.bookcompanion.model.Book;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookAddActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO  = 1;
    private static final String TAG = BookAddActivity.class.getName();
    private static final String CURRENT_PHOTO_PATH_KEY = "current_photo";
    public static final SimpleDateFormat CREATED_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ImageView coverPhotoImageView;
    EditText title;
    EditText author;

    String mCurrentPhotoPath;
    Tracker mTracker;

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
        setContentView(R.layout.activity_book_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button saveButton = findViewById(R.id.save);

        title = findViewById(R.id.title);
        author = findViewById(R.id.author);

        BookCompanionApplication application = (BookCompanionApplication) getApplication();
        mTracker = application.getDefaultTracker();

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                Book book  = adapter.getItem(position);
                title.setText(book.getTitle());
                author.setText(book.getAuthor());
                mCurrentPhotoPath = book.getThumbnailUrl();

                int width = (int) getResources().getDimension(R.dimen.column_width);
                int height = (int) ((double)width * 1.5);

                Picasso.with(BookAddActivity.this)
                        .load(book.getThumbnailUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .resize(width,height)
                        .centerCrop()
                        .into(coverPhotoImageView);

                coverPhotoImageView.setVisibility(View.VISIBLE);
                saveButton.requestFocus();
                hideKeyboardFrom(BookAddActivity.this, autoCompleteTextView);
            }
        });

        coverPhotoImageView = findViewById(R.id.cover_photo);
        Button takePhotoButton = findViewById(R.id.take_photo);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = BookAddActivity.this.title.getText().toString();
                String author = BookAddActivity.this.author.getText().toString();

                clearError(BookAddActivity.this.title);
                clearError(BookAddActivity.this.author);

                if(isEmpty(BookAddActivity.this.title)) {
                    setError(BookAddActivity.this.title, "Must not be empty");
                    return;
                }
                if(isEmpty(BookAddActivity.this.author)) {
                    setError(BookAddActivity.this.author, "Must not be empty");
                    return;
                }

                String thumbnailUrl = mCurrentPhotoPath;



                 Book book = new Book();
                 book.setTitle(title);
                 book.setAuthor(author);
                 book.setThumbnailUrl(thumbnailUrl);
                 book.setCreatedAt(new Date());

                AsyncQueryHandler queryHandler = new AsyncQueryHandler(getContentResolver()) {
                    @Override
                    protected void onDeleteComplete(int token, Object cookie, int result) {
                        super.onDeleteComplete(token, cookie, result);
                    }

                    @Override
                    protected void onInsertComplete(int token, Object cookie, Uri uri) {
                        super.onInsertComplete(token, cookie, uri);
                        Toast.makeText(BookAddActivity.this, "Book saved", Toast.LENGTH_LONG).show();
                        Intent returnIntent = new Intent();
                        BookAddActivity.this.setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                };

                ContentValues contentValues = new ContentValues();

                contentValues.put(BookContract.BookEntry.COLUMN_TITLE, book.getTitle());
                contentValues.put(BookContract.BookEntry.COLUMN_AUTHOR, book.getAuthor());

                contentValues.put(BookContract.BookEntry.COLUMN_THUMBNAIL_URL, book.getThumbnailUrl());
                contentValues.put(BookContract.BookEntry.COLUMN_CREATED_AT, CREATED_DATE_FORMAT.format(book.getCreatedAt()));

                queryHandler.startInsert(0, null, BookContract.BookEntry.CONTENT_URI, contentValues);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_PHOTO_PATH_KEY, mCurrentPhotoPath);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPhotoPath = savedInstanceState.getString(CURRENT_PHOTO_PATH_KEY);

        if(mCurrentPhotoPath!=null) {
            int width = (int) getResources().getDimension(R.dimen.column_width);
            int height = (int) ((double) width * 1.5);

            Picasso.with(BookAddActivity.this)
                    .load(mCurrentPhotoPath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .resize(width, height)
                    .centerCrop()
                    .into(coverPhotoImageView);

            coverPhotoImageView.setVisibility(View.VISIBLE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO  && resultCode == RESULT_OK) {

            int width = (int) getResources().getDimension(R.dimen.column_width);
            int height = (int) ((double)width * 1.5);

            File file = new File(mCurrentPhotoPath);
            Uri uri =  Uri.fromFile(file);
            Picasso.with(this)
                    .load(uri.toString())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .resize(width,height)
                    .centerCrop()
                    .into(coverPhotoImageView);

            coverPhotoImageView.setVisibility(View.VISIBLE);
            mCurrentPhotoPath = uri.toString();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static boolean isEmpty(EditText editText) {
        String input = editText.getText().toString().trim();
        return input.length() == 0;

    }
    public static void setError(EditText editText, String errorString) {
        editText.setError(errorString);

    }

    public static void clearError(EditText editText) {
        editText.setError(null);
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
