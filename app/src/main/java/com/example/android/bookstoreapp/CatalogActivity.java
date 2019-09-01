package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    BookDbHelper mDbHelper;

    public static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        setTitle(R.string.catalog_activity_label);

        // Find the ListView which will be populated with the pet data
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }

        });

        mDbHelper = new BookDbHelper(this);

        mCursorAdapter = new BookCursorAdapter(this, null);

        //Start loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        bookListView.setAdapter(mCursorAdapter);
        ImageView sellImageView = (ImageView) findViewById(R.id.sell_icon);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent editorIntent = new Intent(CatalogActivity.this, ViewItemActivity.class);
                // Form the current URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // (@link BookEntry#CONTENT_URI).
                // For example, the URI would be "content://com.example.android.bookstoreapp/books/2"
                // if the book with ID 2 was clicked on.
                Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                editorIntent.setData(currentUri);
                startActivity(editorIntent);
            }
        });
    }

    private void insertDummyBook() {
        // Create and/or open a database to write in it
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "1984");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Orwell George");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 9.90);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 7);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Untitled");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "2101234567");

        // Insert a new row for 1984 using ContentResolver
        // Use the {@link bookEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access book's data in the future.
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        //Create a new map of values, where column names are the keys
        values.clear();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "Animal Farm");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Orwell George");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 6.40);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 1);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Untitled Company");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "2101234567");

        // Insert a new row for Animal Farm using ContentResolver
        // Use the {@link bookEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access book's data in the future.
        newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        //Create a new map of values, where column names are the keys
        values.clear();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "La peste");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Camus Albert");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 7.60);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 15);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Untitled Company");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "2101234567");

        // Insert a new row for La peste using ContentResolver
        // Use the {@link bookEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access book's data in the future.
        newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        //Create a new map of values, where column names are the keys
        values.clear();
        values.put(BookEntry.COLUMN_BOOK_TITLE, "La chute");
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, "Camus Albert");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 7.90);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 12);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Untitled Company");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "2101234567");

        /// Insert a new row for La chute using ContentResolver
        // Use the {@link bookEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access book's data in the future.
        newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyBook();
                //displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Open delete confirmation Dialogue
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_books_dialog_msg);
        builder.setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllBooks();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        // Show a toast message depending on whether or not the insertion was successful
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Perform this raw SQL query "SELECT * FROM books"
        // to get a Cursor that contains all rows from the books table.
        //Cursor cursor = db.rawQuery("SELECT * FROM " + BookEntry.TABLE_NAME, null);
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}

