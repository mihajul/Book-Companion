package com.udacity.bookcompanion;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.bookcompanion.adapter.NotesRecyclerViewAdapter;
import com.udacity.bookcompanion.data.BookContract;
import com.udacity.bookcompanion.loader.NoteListLoader;
import com.udacity.bookcompanion.model.Book;
import com.udacity.bookcompanion.model.Note;
import com.udacity.bookcompanion.model.NoteType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a single Book detail screen.
 * This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity}
 * on handsets.
 */
public class BookDetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<List<Note>>{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_BOOK = "book";
    static final int REQUEST_TAKE_PHOTO  = 1;
    private static final String TAG = BookDetailFragment.class.getName();
    private static final String RECYCLER_VIEW_POSITION_KEY = "position";

    /**
     * The dummy content this fragment is presenting.
     */
    private Book mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    private RecyclerView recyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private NotesRecyclerViewAdapter bookRecyclerViewAdapter;
    private ImageButton saveNoteButton;
    private ImageButton takePhotoButton;
    private int recyclerViewPosition = -1;
    private static final int LOADER_ID = 1;
    String mCurrentPhotoPath;

    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_BOOK)) {
            mItem = getArguments().getParcelable(ARG_BOOK);

            Activity activity = this.getActivity();
            Toolbar appBarLayout= (Toolbar) activity.findViewById(R.id.detail_toolbar);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }

            ActionBar supportActionBar = ((AppCompatActivity)activity).getSupportActionBar();
            if(supportActionBar != null) {
                supportActionBar.setTitle(mItem.getTitle());
            }
        }



        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getActivity().getWindow().setBackgroundDrawable(bitmapDrawable);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_list, container, false);

        saveNoteButton = rootView.findViewById(R.id.saveNoteButton);
        final TextView text = rootView.findViewById(R.id.text);
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.note_type_spinner);


        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
                    @Override
                    protected void onDeleteComplete(int token, Object cookie, int result) {
                        super.onDeleteComplete(token, cookie, result);
                    }

                    @Override
                    protected void onInsertComplete(int token, Object cookie, Uri uri) {
                        super.onInsertComplete(token, cookie, uri);
                        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, BookDetailFragment.this);
                    }
                };

                ContentValues contentValues = new ContentValues();

                contentValues.put(BookContract.NoteEntry.COLUMN_TEXT, text.getText().toString());
                contentValues.put(BookContract.NoteEntry.COLUMN_BOOK_ID, mItem.getId());

                NoteType type = NoteType.TEXT;
                int position = spinner.getSelectedItemPosition();
                if(position == 0) {
                    type = NoteType.CHAPTER;
                }
                contentValues.put(BookContract.NoteEntry.COLUMN_NOTE_TYPE, type.ordinal());
                contentValues.put(BookContract.NoteEntry.COLUMN_CREATED_AT, BookAddActivity.CREATED_DATE_FORMAT.format(new Date()));

                queryHandler.startInsert(0, null, BookContract.NoteEntry.CONTENT_URI, contentValues);

                spinner.setSelection(1);
                text.setText("");
            }
        });
        saveNoteButton.setClickable(false);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && s.length()>0) {
                    saveNoteButton.setClickable(true);
                }else {
                    saveNoteButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        takePhotoButton = rootView.findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.note_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1) {
                    takePhotoButton.setVisibility(View.VISIBLE);
                    text.setHint(getString(R.string.note_hint));
                }else {
                    takePhotoButton.setVisibility(View.GONE);
                    text.setHint(getString(R.string.chapter_hint));
                }

           }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                takePhotoButton.setVisibility(View.GONE);
            }
        });


        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) rootView.findViewById(R.id.tv_error_message_display);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.note_list);

        setupRecyclerView((RecyclerView) recyclerView);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }


    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {
        bookRecyclerViewAdapter = new NotesRecyclerViewAdapter(getActivity());
        ((LinearLayoutManager)recyclerView.getLayoutManager()).setStackFromEnd(true);
        recyclerView.setAdapter(bookRecyclerViewAdapter);
    }

    @Override
    public Loader<List<Note>> onCreateLoader(int id, final Bundle loaderArgs) {
        return new NoteListLoader(mItem.getId(), getActivity(), mLoadingIndicator);
    }

    @Override
    public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        ((NotesRecyclerViewAdapter) recyclerView.getAdapter()).setData(data);

        if (null == data) {
            showErrorMessage();
        } else {
            showGridView();
        }

        if(recyclerViewPosition != -1) {
            Log.d(TAG, "Scrolling to " + recyclerViewPosition);
            ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(recyclerViewPosition, 0);
        }
        else if(!data.isEmpty()) {
            Log.d(TAG, "Scrolling to " + (data.size() - 1));
            (recyclerView).scrollToPosition(data.size() - 1);
        }else {
            Log.d(TAG, "Scrolling to first ");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Note>> loader) {

    }


    private void showGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        recyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO  && resultCode == RESULT_OK) {

            int width = (int) getResources().getDimension(R.dimen.column_width);
            int height = (int) ((double)width * 1.5);

            String thumbnailUrl = null;

            if (mCurrentPhotoPath!=null) {
                File file = new File(mCurrentPhotoPath);
                if(file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    thumbnailUrl = uri.toString();
                }
            }
            AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
                @Override
                protected void onDeleteComplete(int token, Object cookie, int result) {
                    super.onDeleteComplete(token, cookie, result);
                }

                @Override
                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                    super.onInsertComplete(token, cookie, uri);
                    getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, BookDetailFragment.this);
                }
            };

            ContentValues contentValues = new ContentValues();

            contentValues.put(BookContract.NoteEntry.COLUMN_TEXT, thumbnailUrl);
            contentValues.put(BookContract.NoteEntry.COLUMN_BOOK_ID, mItem.getId());
            contentValues.put(BookContract.NoteEntry.COLUMN_NOTE_TYPE, NoteType.IMAGE.ordinal());
            contentValues.put(BookContract.NoteEntry.COLUMN_CREATED_AT, BookAddActivity.CREATED_DATE_FORMAT.format(new Date()));

            queryHandler.startInsert(0, null, BookContract.NoteEntry.CONTENT_URI, contentValues);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            recyclerViewPosition = savedInstanceState.getInt(RECYCLER_VIEW_POSITION_KEY);
            Log.d(TAG, "Restoring recyclerview position " + recyclerViewPosition);
            if(recyclerView!=null) {
                Log.d(TAG, "Scrolling to " + recyclerViewPosition + " in onActivityCreated");
                ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(recyclerViewPosition, 0);
            }

        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        if(position == recyclerView.getAdapter().getItemCount()) {
            position = -1;
        }
        outState.putInt(RECYCLER_VIEW_POSITION_KEY, position );
        Log.d(TAG, "Saving recyclerview position " + position);

    }


}
