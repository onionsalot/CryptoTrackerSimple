package org.example.trongnguyen.cryptotracker;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity implements CryptoCursorAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Ticker>> {

    private static final String TAG = "MainActivity";
    ArrayList<Ticker> tickerList = new ArrayList<>();
    private Toast mToast;
    private TextView mDataPrint;
    private Button addButton;
    private Button searchButton;
    TickerAdapter adapter;
    String currencyURL;
    String[] searchItems;
    private static String OPERATION_ADD = "add";
    private static String GET_URL = "url";
    private CryptoDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Test", "onCreate: called");
        //RecyclerView rvTickers = (RecyclerView) findViewById(R.id.main_recycler);


        // Create the Adapter
        //adapter = new TickerAdapter(tickerList, this, false);
        // Attach the adapter to the recycler view
        //rvTickers.setAdapter(adapter);
        // Set layout manager to the position of the items
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //rvTickers.setLayoutManager(layoutManager);

        // Temporary Text View
        mDataPrint = (TextView) findViewById(R.id.print_data);
        // Temporary Button
        addButton = (Button) findViewById(R.id.add_test);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchItems = new String[]{"VEN"};
                makeCurrencyQuery(searchItems);
                Log.d("TESTING", "onClick: Current items in adapter " + adapter.getItemCount() );
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


        mDbHelper = new CryptoDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME, "Bitcoin");
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER, "BTC");
        values.put(CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE, "20");
        values.put(CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER, "0");

        Uri newUri = getContentResolver().insert(CryptoContract.CryptoEntry.CONTENT_URL,values);
        sqlStuff();
    }




    public void sqlStuff() {
        String[] projection = {
                CryptoContract.CryptoEntry._ID,
                CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER,
                CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE,
                CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME,
                CryptoContract.CryptoEntry.COLUMN_INTERNAL_NUMBER
        };

        Cursor cursor = getContentResolver().query(
                CryptoContract.CryptoEntry.CONTENT_URL,
                projection,
                null,
                null,
                null,
                null
        );

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        CryptoCursorAdapter adapter = new CryptoCursorAdapter(this, cursor, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                searchItems = new String[] {data.getStringExtra("addedCrypto")};
                makeCurrencyQuery(searchItems);
            }
        }
    }


    public void makeCurrencyQuery(String[] currency) {
        currencyURL = NetworkUtils.buildUri(currency);
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(22);
        Bundle bundle = new Bundle();
        bundle.putStringArray(OPERATION_ADD, searchItems);
        bundle.putString(GET_URL, currencyURL);
        if (loader == null) {
            Log.d(TAG, "makeCurrencyQuery: null");
            loaderManager.initLoader(22,bundle,this);
        } else {
            Log.d(TAG, "makeCurrencyQuery: not null");
            loaderManager.restartLoader(22,bundle,this);
        }
}
    @Override
    public void onListItemClick(int clickedIndex) {
        mToast = Toast.makeText(this, "Item clicked " + clickedIndex, Toast.LENGTH_SHORT);
        mToast.show();
//        if (clickedIndex == 0) {
//            makeCurrencyQuery("BTC");
//        } else {
//            makeCurrencyQuery("ETH");
//        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    public Loader<List<Ticker>> onCreateLoader(int i, Bundle bundle) {

        return new CryptoLoader(this,bundle);
    }

    @Override
    public void onLoadFinished(Loader<List<Ticker>> loader, List<Ticker> tickers) {
        if (tickers != null) {
            tickerList.addAll(tickers);
            adapter.notifyDataSetChanged();
            //getLoaderManager().destroyLoader(22);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Ticker>> loader) {
    }

}
