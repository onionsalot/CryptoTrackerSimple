package org.example.trongnguyen.cryptotracker;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.example.trongnguyen.cryptotracker.database.CryptoContract;
import org.example.trongnguyen.cryptotracker.database.CryptoCursorAdapter;
import org.example.trongnguyen.cryptotracker.database.CryptoDbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CryptoCursorAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks {

    private int TICKER_LOADER = 0;
    private int CURSOR_LOADER = 1;
    private int DATABASE_UPDATE_LOADER = 2;

    private static final String TAG = "MainActivity";
    ArrayList<Ticker> tickerList = new ArrayList<>();
    private Toast mToast;
    private TextView mDataPrint;
    private Button addButton;
    private Button searchButton;
    private Button updateButton;
    CryptoCursorAdapter mCryptoCursorAdapter;
    String currencyURL;
    String[] searchItems;
    private static String OPERATION_ADD = "add";
    private static String OPERATION_UPDATE = "update";
    private static String GET_URL = "url";
    // Used to get the FULL name from searchActivity. For some reason base API does not contain
    // coin title, only coin ticker symbol.
    private String addedName;
    private boolean addOrNah;
    private boolean updateData;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        if(!SP.getBoolean("firstLaunch", false)) {
            tempAdd();
            SharedPreferences.Editor editor = SP.edit();
            editor.putBoolean("firstLaunch", true);
            editor.apply();
        }

        updateData = true;

        // Temporary Text View
        mDataPrint = (TextView) findViewById(R.id.print_data);
        // Temporary Button
        addButton = (Button) findViewById(R.id.add_test);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempAdd();
            }
        });
        searchButton = (Button) findViewById(R.id.search_test);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        updateButton = (Button) findViewById(R.id.update_test);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData = true;
                getLoaderManager().initLoader(CURSOR_LOADER,null,MainActivity.this);
            }
        });
//        if (savedInstanceState == null) {
//            searchItems = new String[]{"BTC", "ETH"};
//            makeCurrencyQuery(searchItems);
//        } else {
//            getLoaderManager().initLoader(22,null,this);
//        }
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        mCryptoCursorAdapter = new CryptoCursorAdapter(this, null, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mCryptoCursorAdapter);



    }

    public void tempAdd() {
        ContentValues values = new ContentValues();
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME, "Bitcoin");
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER, "BTC");
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE, "20");
        values.put(CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER, "0");

        Uri newUri = getContentResolver().insert(CryptoContract.CryptoEntry.CONTENT_URL,values);

    }



    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(CURSOR_LOADER,null,this);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                searchItems = new String[] {data.getStringExtra("addedCrypto")};
                addedName = data.getStringExtra("addedCryptoName");
                addOrNah =  true;
                makeCurrencyQuery(searchItems);
            }
        }
    }

    public void makeCurrencyQuery(String[] currency) {
        currencyURL = NetworkUtils.buildUri(currency);
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(TICKER_LOADER);
        Bundle bundle = new Bundle();
        bundle.putStringArray(OPERATION_ADD, searchItems);
        bundle.putString(GET_URL, currencyURL);
        if (loader == null) {
            Log.d(TAG, "makeCurrencyQuery: null");
            loaderManager.initLoader(TICKER_LOADER,bundle,this);
        } else {
            Log.d(TAG, "makeCurrencyQuery: not null");
            loaderManager.restartLoader(TICKER_LOADER,bundle,this);
        }
    }
//    public void makeCurrencyQuery(String[] currency) {
//        currencyURL = NetworkUtils.buildUri(currency);
//        LoaderManager loaderManager = getLoaderManager();
//        Loader<String> loader = loaderManager.getLoader(22);
//        Bundle bundle = new Bundle();
//        bundle.putStringArray(OPERATION_ADD, searchItems);
//        bundle.putString(GET_URL, currencyURL);
//        if (loader == null) {
//            Log.d(TAG, "makeCurrencyQuery: null");
//            loaderManager.initLoader(22,bundle,this);
//        } else {
//            Log.d(TAG, "makeCurrencyQuery: not null");
//            loaderManager.restartLoader(22,bundle,this);
//        }
//    }
    @Override
    public void onListItemClick(int clickedIndex) {
        mToast = Toast.makeText(this, "Item clicked " + clickedIndex, Toast.LENGTH_SHORT);
        mToast.show();
        Cursor cursor = mCryptoCursorAdapter.getCursor();
        cursor.moveToPosition(clickedIndex);
        int nameColumnIndex = cursor.getColumnIndex(CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME);
        String name = cursor.getString(nameColumnIndex);
        mDataPrint.setText(name);
//        if (clickedIndex == 0) {
//            makeCurrencyQuery("BTC");
//        } else {
//            makeCurrencyQuery("ETH");
//        }
    }


    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        if (i == TICKER_LOADER) {
            return new CryptoLoader(this, bundle);
        } else if (i == CURSOR_LOADER) {
            Log.d(TAG, "onCreateLoader: CALLED");
            String [] projection = {
                    CryptoContract.CryptoEntry._ID,
                    CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME,
                    CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE,
                    CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER,
                    CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER

            };
            return new CursorLoader(this,
                    CryptoContract.CryptoEntry.CONTENT_URL,
                    projection,
                    null,
                    null,
                    null);
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        if (data != null) {
            if (id == TICKER_LOADER) {
                ArrayList<Ticker> list = (ArrayList<Ticker>) data;
                tickerList.clear();
                tickerList.addAll(list);
                Ticker ticker = list.get(0);
                String loadTicker = ticker.getName();
                String loadPrice = ticker.getPrice();
                getLoaderManager().destroyLoader(TICKER_LOADER);
                if (addOrNah) {
                    addResults(addedName,loadTicker,loadPrice);
                    addOrNah = false;
                } else if (updateData) {
                    updateData = false;
                    updateItem(list);
                }
            } else if (id == CURSOR_LOADER) {
                Log.d(TAG, "onLoadFinished for cursor called");
                Cursor cursor = (Cursor) data;

                mCryptoCursorAdapter.swapCursor(cursor);
                if (updateData) {
                    ArrayList<String> fetchList = new ArrayList<String>();
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        fetchList.add(cursor.getString(3));
                    }
                    searchItems = fetchList.toArray(new String[0]);
                    makeCurrencyQuery(searchItems);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        int id = loader.getId();
        if (id == TICKER_LOADER) {
        } else if (id == CURSOR_LOADER){
            mCryptoCursorAdapter.swapCursor(null);
        }
    }

    public void addResults(String name, String ticker, String price) {

        ContentValues values = new ContentValues();
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME, name);
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER, ticker);
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE, price);
        values.put(CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER, "0");

        Uri newUri = getContentResolver().insert(CryptoContract.CryptoEntry.CONTENT_URL,values);
        mCryptoCursorAdapter.notifyDataSetChanged();
    }

    private void updateItem(ArrayList<Ticker> list) {
        ContentValues values = new ContentValues();
        String[] args = new String[list.size()];
        for (int i=0; i< list.size(); i++) {
            Ticker ticker = list.get(i);
            values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE, ticker.getPrice());
            args[i] = ticker.getName();
            String selection = CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER + "='" + args[i]+"'";
            Log.d(TAG, "updateItem: Updating items = " + selection);
            getContentResolver().update(CryptoContract.CryptoEntry.CONTENT_URL, values, selection,null);
            mCryptoCursorAdapter.notifyDataSetChanged();
        }

        mToast = Toast.makeText(this, " Update done ", Toast.LENGTH_SHORT);
        mToast.show();

        recyclerView.setAdapter(mCryptoCursorAdapter);
    }

}