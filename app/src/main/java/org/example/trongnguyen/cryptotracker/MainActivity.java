package org.example.trongnguyen.cryptotracker;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private static final String TAG = "MainActivity";
    ArrayList<Ticker> tickerList = new ArrayList<>();
    private Toast mToast;
    private TextView mDataPrint;
    private Button addButton;
    private Button searchButton;
    CryptoCursorAdapter mCryptoCursorAdapter;
    String currencyURL;
    String[] searchItems;
    private static String OPERATION_ADD = "add";
    private static String GET_URL = "url";
    // Used to get the FULL name from searchActivity. For some reason base API does not contain
    // coin title, only coin ticker symbol.
    private String addedName;
    private boolean addOrNah;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Test", "onCreate: called");

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
//        if (savedInstanceState == null) {
//            searchItems = new String[]{"BTC", "ETH"};
//            makeCurrencyQuery(searchItems);
//        } else {
//            getLoaderManager().initLoader(22,null,this);
//        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
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
        mCryptoCursorAdapter.notifyDataSetChanged();
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
                Log.d(TAG, "onLoadFinished: tickerlist contains" + tickerList.toString() + tickerList.size());
                Ticker ticker = list.get(0);
                Log.d(TAG, "onLoadFinished: ticker = " + ticker.getName() + ticker.getPrice() + ticker.getTicker());
                String loadTicker = ticker.getName();
                String loadPrice = ticker.getPrice();
                getLoaderManager().destroyLoader(TICKER_LOADER);
                if (addOrNah) {
                    addResults(addedName,loadTicker,loadPrice);
                    addOrNah = false;
                }
            } else if (id == CURSOR_LOADER) {
                Log.d(TAG, "onLoadFinished for cursor called");
                Cursor cursor = (Cursor) data;

                mCryptoCursorAdapter.changeCursor(cursor);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        int id = loader.getId();
        if (id == TICKER_LOADER) {
        } else if (id == CURSOR_LOADER){
            mCryptoCursorAdapter.changeCursor(null);
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


}
