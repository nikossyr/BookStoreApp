package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BookCursorAdapter extends CursorAdapter {

    // Variable for formatting the currency values before showing them to the user
    private NumberFormat numberFormat = new DecimalFormat("0.00");

    private Context mContext;

    private View.OnClickListener clickListener;

    private LayoutInflater mInflater;
    //private Holder mHolder;

    static private class Holder {
        TextView mQuantityTextView;
        ImageView mSellImageView;

        public Holder(View view) {
            mQuantityTextView = (TextView) view.findViewById(R.id.quantity);
            mSellImageView = (ImageView) view.findViewById(R.id.sell_icon);
        }
    }

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        mContext = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        Holder holder = new Holder(view);
        view.setTag(holder);
        return view;
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("Position " + cursor.getPosition() + ":", " bindView() has been called.");

        final Holder holder = (Holder) view.getTag();
        final Cursor mCursor = cursor;
        // Find fields to populate in inflated template
        TextView bookTitleTextView = (TextView) view.findViewById(R.id.title);
        TextView bookAuthorTextView = (TextView) view.findViewById(R.id.author);
        TextView bookPriceTextView = (TextView) view.findViewById(R.id.price);
        holder.mQuantityTextView = (TextView) view.findViewById(R.id.quantity);


        // Extract properties from cursor
        String title = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_TITLE));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_AUTHOR));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY));

        // Populate fields with extracted properties
        bookTitleTextView.setText(title);
        if (TextUtils.isEmpty(author)) {
            bookAuthorTextView.setText(R.string.author_unknown);
        } else {
            bookAuthorTextView.setText(String.valueOf(author));
        }
        String priceCurrency = String.valueOf(numberFormat.format(price)) + " €";
        bookPriceTextView.setText(priceCurrency);
        String quantityPhrase = String.valueOf(quantity) + " books left";
        holder.mQuantityTextView.setText(quantityPhrase);

        final int position = cursor.getPosition();
        holder.mSellImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCursor.moveToPosition(position);

                String currenttitle = mCursor.getString(mCursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_TITLE));
                String currentAuthor = mCursor.getString(mCursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_AUTHOR));
                double currentPrice = mCursor.getDouble(mCursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE));
                int currentQuantity = mCursor.getInt(mCursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_QUANTITY));
                ;
                String currentsupplierName = mCursor.getString(mCursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_NAME));
                String currentSupplierNumber = mCursor.getString(mCursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER));

                if (currentQuantity == 0) {
                    Toast.makeText(view.getContext(), "No product available \n Please place order", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentQuantity -= 1;
                holder.mQuantityTextView.setText(String.valueOf(currentQuantity));

                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_BOOK_TITLE, currenttitle);
                values.put(BookEntry.COLUMN_BOOK_AUTHOR, currentAuthor);
                values.put(BookEntry.COLUMN_BOOK_PRICE, currentPrice);
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, currentQuantity);
                values.put(BookEntry.COLUMN_SUPPLIER_NAME, currentsupplierName);
                values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, currentSupplierNumber);

                String selection = BookEntry._ID + "=?";

                long id = mCursor.getLong(mCursor.getColumnIndex(BookEntry._ID));
                String[] selectionArgs = new String[]{String.valueOf(id)};

                int rowsAffected = mContext.getContentResolver().update(BookEntry.CONTENT_URI, values, selection, selectionArgs);

                // Show a toast message depending on whether or not the insertion was successful
                if (rowsAffected == 0) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(mContext, mContext.getString(R.string.cursor_book_sale_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(mContext, mContext.getString(R.string.cursor_book_sale_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}