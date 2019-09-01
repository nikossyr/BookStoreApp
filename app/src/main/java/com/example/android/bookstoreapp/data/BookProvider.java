package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bookstoreapp.R;
import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BookProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 566;

    /**
     * URI matcher code for the content URI for a single books in the books table
     */
    private static final int BOOK_ID = 567;

    private Context mContext;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.bookstoreapp/books" will map to the
        // integer code (@link #BOOKS). This URI is used to provide access to all the rows of the
        // BOOKS table.
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        // The content URI of the form "content://com.example.android.bookstoreapp/books/#" will map to the
        // integer code (@link #BOOK_ID). This URI is used to provide access ONE single row of the
        // BOOKS table.

        // In this case the "#" wildcard is used and can be substituted for an integer. URI without
        // a number at the end does not match.
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Database helper Object
     */
    private BookDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Create and/or open a database to read from
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                // For the BOOKS code, query the pets directly with the given projection,
                // selection, selection arguments, and sort order. The cursor could contain
                // multiple rows of the books table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // For the BOOKS code, query the pets directly with the given projection,
                // selection, selection arguments, and sort order. The cursor could contain
                // ONE SINGLE row of the books table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        // Set notification URI on the cursor
        // so we know what content URI was created for.
        // If the data at thisURI changes, then we know we ned to update the Cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the TITLE is not null,
        // if the PRICE is provided and if it's greater than or equal to 0
        // and if the QUANTITY is provided and if it's greater than or equal to 0.
        String title = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
        String price = values.getAsString(BookEntry.COLUMN_BOOK_PRICE);
        String quantity = values.getAsString(BookEntry.COLUMN_BOOK_QUANTITY);
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        String supplierNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        if (title == null || title.equals("")) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_no_title),
                    Toast.LENGTH_SHORT).show();
            return BookEntry.STAY_HERE_URI;
        } else if (price == null || price.equals("") || Double.parseDouble(price) < 0) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_price_invalid),
                    Toast.LENGTH_SHORT).show();
            return BookEntry.STAY_HERE_URI;
        } else if (quantity == null || quantity.equals("") || Integer.parseInt(quantity) < 0) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_quantity_invalid),
                    Toast.LENGTH_SHORT).show();
            return BookEntry.STAY_HERE_URI;
        }  else if(supplierName == null|| supplierName.equals("")){
            Toast.makeText(mContext, mContext.getString(R.string.provider_no_supplier_name),
                    Toast.LENGTH_SHORT).show();
            return BookEntry.STAY_HERE_URI;
        }else if(supplierNumber== null|| supplierNumber.equals("")) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_no_supplier_number),
                    Toast.LENGTH_SHORT).show();
            return BookEntry.STAY_HERE_URI;
        }else{

            // Create and/or open a database to write in it
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Insert a new row returning the id of the row
            long id = database.insert(BookEntry.TABLE_NAME, null, values);

            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }

            Log.v(LOG_TAG, "New row ID " + id);

            // Notify all listeners that the data has changed for the pet content URI
            // URI: content://com.example.android.bookstoreapp/books
            getContext().getContentResolver().notifyChange(uri, null);

            // Once we know the ID of the new row in the table,
            // return the new URI with the ID appended to the end of it
            return ContentUris.withAppendedId(uri, id);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of rows deleted
                return rowsDeleted;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of rows deleted
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check that the TITLE is not null,
        // if the PRICE is provided and if it's greater than or equal to 0
        // and if the QUANTITY is provided and if it's greater than or equal to 0.
        String title = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
        String price = values.getAsString(BookEntry.COLUMN_BOOK_PRICE);
        String quantity = values.getAsString(BookEntry.COLUMN_BOOK_QUANTITY);
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        String supplierNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        if (title == null || title.equals("")) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_no_title),
                    Toast.LENGTH_SHORT).show();
            return -1;
        } else if (price == null || price.equals("") || Double.parseDouble(price) < 0) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_price_invalid),
                    Toast.LENGTH_SHORT).show();
            return -1;

        } else if (quantity == null || quantity.equals("")) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_quantity_invalid),
                    Toast.LENGTH_SHORT).show();
            return -1;
        } else if (Integer.parseInt(quantity) < 0) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_quantity_negative),
                    Toast.LENGTH_SHORT).show();
            return -1;
        } else if(supplierName== null|| supplierName.equals("")){
            Toast.makeText(mContext, mContext.getString(R.string.provider_no_supplier_name),
                    Toast.LENGTH_SHORT).show();
            return -1;
        }else if(supplierNumber== null|| supplierNumber.equals("")) {
            Toast.makeText(mContext, mContext.getString(R.string.provider_no_supplier_number),
                    Toast.LENGTH_SHORT).show();
            return -1;
        }else if (values.size() == 0) {
            //If there are no values to update, then don 't try to update the database
            return 0;
        } else {

            // Create and/or open a database to write in it
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Notify all listeners that the data has changed for the pet content URI
            // URI: content://com.example.android.pets/pets
            getContext().getContentResolver().notifyChange(uri, null);

            // Perform the update on the database and get the number of rows affected
            int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows updated
            return rowsUpdated;
        }
    }
}
