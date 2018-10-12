package org.example.trongnguyen.cryptotracker.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class CryptoProvider extends ContentProvider {
    private CryptoDbHelper mCryptoDbHelper;
    public static final String LOG_TAG = CryptoProvider.class.getSimpleName();

    private static final int CRYPTO = 100;
    private static final int CRYPTO_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(CryptoContract.CONTENT_AUTHORITY, CryptoContract.PATH_CRYPTO, CRYPTO);
        sUriMatcher.addURI(CryptoContract.CONTENT_AUTHORITY, CryptoContract.PATH_CRYPTO + "/#", CRYPTO_ID);
    }

    @Override
    public boolean onCreate() {
        mCryptoDbHelper = new CryptoDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mCryptoDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case CRYPTO:
                cursor = database.query(CryptoContract.CryptoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null,null,sortOrder);
                break;
            case CRYPTO_ID:
                selection = CryptoContract.CryptoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(CryptoContract.CryptoEntry.TABLE_NAME, projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CRYPTO:
                return insertCrypto(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    public Uri insertCrypto(Uri uri, ContentValues values) {
        SQLiteDatabase database = mCryptoDbHelper.getWritableDatabase();

        long id = database.insert(CryptoContract.CryptoEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri );
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mCryptoDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CRYPTO:
                return database.delete(CryptoContract.CryptoEntry.TABLE_NAME, selection, selectionArgs);
            case CRYPTO_ID:
                selection = CryptoContract.CryptoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return database.delete(CryptoContract.CryptoEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CRYPTO_ID:
                selection = CryptoContract.CryptoEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri)) };
                return updateCrypto(uri, values, selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateCrypto(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME)) {
            String name = values.getAsString(CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Crypto name required");
            }
        }
        if (values.containsKey(CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER)) {
            String ticker = values.getAsString(CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER);
            if (ticker == null) {
                throw new IllegalArgumentException("Crypto ticker required");
            }
        }
        if (values.containsKey(CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE)) {
            String price = values.getAsString(CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Crypto Price required");
            }
        }
        if (values.containsKey(CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER)) {
            String number = values.getAsString(CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER);
            if (number == null) {
                throw new IllegalArgumentException("Crypto number required");
            }
        }

        if (values.size() ==0) {
            return 0;
        }
        SQLiteDatabase database = mCryptoDbHelper.getWritableDatabase();
            return database.update(CryptoContract.CryptoEntry.TABLE_NAME, values, selection,selectionArgs);
    }
}
