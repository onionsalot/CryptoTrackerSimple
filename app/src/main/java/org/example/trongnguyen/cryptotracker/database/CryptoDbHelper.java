package org.example.trongnguyen.cryptotracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CryptoDbHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "crypto.db";
    private static final int DATABASE_VERSION = 201;


    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_CRYPTO_TABLE = "CREATE TABLE " + CryptoContract.CryptoEntry.TABLE_NAME + " ("
                + CryptoContract.CryptoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME + " TEXT NOT NULL, "
                + CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER + " TEXT NOT NULL, "
                + CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE + " TEXT NOT NULL, "
                + CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_CRYPTO_TABLE);
    }

    public CryptoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
