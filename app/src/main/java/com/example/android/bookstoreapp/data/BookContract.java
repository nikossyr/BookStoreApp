package com.example.android.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BookContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.bookstoreapp/books/ is a valid path for
     * looking at books data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";

    /* Inner class that defines the table contents of the location table */
    public static final class BookEntry implements BaseColumns {

        //The content URI to access the books data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * Uri that signals that the insert was not successful because the user needs to insert
         * the correct info and we SHOULD NOT finish the activity
         */

        public static final Uri STAY_HERE_URI = Uri.withAppendedPath(CONTENT_URI, "-500");

        // Table name
        public static final String TABLE_NAME = "books";

        // The ID of the book entry
        public static final String _ID = "_id";

        // The NAME of the book
        public static final String COLUMN_BOOK_TITLE = "title";

        // The AUTHOR of the book
        public static final String COLUMN_BOOK_AUTHOR = "author";

        // The PRICE of the book
        public static final String COLUMN_BOOK_PRICE = "price";

        // The QUANTITY of the book
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        // The SUPPLIER NAME of the book
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        // The PHONE NUMBER of the SUPPLIER  of the book
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
    }
}
