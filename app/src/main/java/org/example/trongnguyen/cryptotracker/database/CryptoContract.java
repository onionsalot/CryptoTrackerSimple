package org.example.trongnguyen.cryptotracker.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CryptoContract {
    public static final String CONTENT_AUTHORITY = "com.example.trongnguyen.cryptotracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CRYPTO = "crypto";

    public static final class CryptoEntry implements BaseColumns {
        public static final String TABLE_NAME = "crypto";
        public static final Uri CONTENT_URL = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_CRYPTO);


        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_CRYPTO_NAME = "name";
        public static final String COLUMN_CRYPTO_TICKER = "ticker";
        public static final String COLUMN_CRYPTO_PRICE = "price";
        public static final String COLUMN_INTERNAL_NUMBER = "number";
    }
}
