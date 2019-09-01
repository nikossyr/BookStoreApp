package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BookDbHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ViewItemActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * TextView field to enter the book's TITLE
     */
    private TextView mTitleTextView;

    /**
     * TextView field to enter the book's AUTHOR
     */
    private TextView mAuthorTextView;

    /**
     * TextView field to enter the book's PRICE
     */
    private TextView mPriceTextView;

    /**
     * TextView field to enter the book's QUANTITY
     */
    private TextView mQuantityTextView;

    /**
     * TextView field to enter the book's SUPPLIER NAME
     */
    private TextView mSupplierNameTextView;

    String mSupplierPhone;

    Uri mCurrentBookUri;

    BookDbHelper mDbHelper;

    double mPrice;

    /**
     * Auxiliary static variables to signal which button is pressed (INCREMENT or DECREMENT) in order
     * to change the quantity accordingly
     */
    final private static int QUANTITY_INCREMENT = 846;
    final private static int QUANTITY_DECREMENT = 648;

    public static final int BOOK_LOADER = 0;

    // Variable for formatting the currency values before showing them to the user
    NumberFormat numberFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewItemActivity.this, EditorActivity.class);
                // Set the URI on the data field of the intent
                intent.setData(mCurrentBookUri);
                startActivity(intent);
            }
        });

        mTitleTextView = (TextView) findViewById(R.id.view_item_title);
        mAuthorTextView = (TextView) findViewById(R.id.view_item_author);
        mPriceTextView = (TextView) findViewById(R.id.view_item_price);
        mQuantityTextView = (TextView) findViewById(R.id.view_item_quantity);
        mSupplierNameTextView = (TextView) findViewById(R.id.view_item_supplier_name);
        ImageView mPhoneIcon = (ImageView) findViewById(R.id.view_item_phone_icon);
        Button mQuantityIncrement = (Button) findViewById(R.id.view_item_quantity_increment);
        Button mQuantityDecrement = (Button) findViewById(R.id.view_item_quantity_decrement);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        mDbHelper = new BookDbHelper(this);

        mQuantityIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBookQuantity(QUANTITY_INCREMENT);
            }
        });

        mQuantityDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBookQuantity(QUANTITY_DECREMENT);
            }
        });

        mPhoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mSupplierPhone, null));
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
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

        CursorLoader cursorLoader = new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!(data == null || data.getCount() == 0)) {

            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            data.moveToFirst();
            mTitleTextView.setText(data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_TITLE)));
            mAuthorTextView.setText(data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_AUTHOR)));
            mPrice = data.getDouble(data.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE));
            String mPriceString = getResources().getString(R.string.view_item_price) + " " +
                    String.valueOf(numberFormat.format(mPrice)) + " €";
            mPriceTextView.setText(mPriceString);
            String mQuantity = String.valueOf(data.getInt(data.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY)));
            mQuantityTextView.setText(mQuantity);
            mSupplierNameTextView.setText(data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_NAME)));
            mSupplierPhone = data.getString(data.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleTextView.setText("");
        mAuthorTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_view_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_delete:
                // Open delete confirmation Dialogue
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
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
    private void deleteBook() {
        int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
        // Show a toast message depending on whether or not the insertion was successful
        if (rowsDeleted == 0) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                    Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();
    }

    private void updateBookQuantity(int updateDirection) {
        String mTitle = mTitleTextView.getText().toString().trim();
        String mAuthor = mAuthorTextView.getText().toString().trim();
        int mQuantity = Integer.parseInt(mQuantityTextView.getText().toString());
        String mSupplier = mSupplierNameTextView.getText().toString().trim();
        String mSupplierNumber = mSupplierPhone;

        if (updateDirection == QUANTITY_INCREMENT) {
            mQuantity += 1;
        } else if (updateDirection == QUANTITY_DECREMENT) {
            mQuantity -= 1;
        }
        // Edit an existing pet by calling the update method
        String selection = BookEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentBookUri))};
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, mTitle);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, mAuthor);
        values.put(BookEntry.COLUMN_BOOK_PRICE, mPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, mQuantity);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, mSupplier);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, mSupplierNumber);

        getContentResolver().update(BookEntry.CONTENT_URI, values, selection, selectionArgs);
    }
}
