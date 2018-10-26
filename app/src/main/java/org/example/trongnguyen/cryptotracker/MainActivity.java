package org.example.trongnguyen.cryptotracker;


import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.example.trongnguyen.cryptotracker.database.CryptoContract;
import org.example.trongnguyen.cryptotracker.database.CryptoCursorAdapter;
import org.example.trongnguyen.cryptotracker.database.CryptoDbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CryptoCursorAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks {

    private int TICKER_LOADER = 0;
    private int CURSOR_LOADER = 1;
    private int CLICKED_ITEM_LOADER = 2;

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
    CandleStickChart chart;
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


        chart = (CandleStickChart) findViewById(R.id.chart);

    }


    private CandleDataSet getDataSet() {
        ArrayList<CandleEntry> entries = new ArrayList<>();
        entries.add(new CandleEntry(0, 4.62f, 2.02f, 2.70f, 4.13f));
        entries.add(new CandleEntry(1, 5.50f, 2.70f, 3.35f, 4.96f));
        entries.add(new CandleEntry(2, 5.25f, 3.02f, 3.50f, 1.50f));
        entries.add(new CandleEntry(3, 6f,    3.25f, 4.40f, 5.0f));
        entries.add(new CandleEntry(4, 5.57f, 2f,    2.80f, 4.5f));
        CandleDataSet dataSet = new CandleDataSet(entries, "# of Calls");
        dataSet.setColor(Color.rgb(80, 80, 80));
        dataSet.setShadowColor(Color.DKGRAY);
        dataSet.setShadowWidth(0.7f);
        dataSet.setDecreasingColor(Color.RED);
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingColor(Color.rgb(122, 242, 84));
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        dataSet.setNeutralColor(Color.BLUE);
        dataSet.setValueTextColor(Color.RED);

        return dataSet;
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
        int nameColumnIndex = cursor.getColumnIndex(CryptoContract.CryptoEntry.COLUMN_CRYPTO_TICKER);
        String name = cursor.getString(nameColumnIndex);
        mDataPrint.setText(name);

        searchItems = new String[] {name};
        currencyURL = NetworkUtils.buildUri(name);
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(CLICKED_ITEM_LOADER);
        Bundle bundle = new Bundle();
        bundle.putStringArray(OPERATION_ADD, searchItems);
        bundle.putString(GET_URL, currencyURL);
        loaderManager.initLoader(CLICKED_ITEM_LOADER,bundle,this);
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
        } else if (i == CLICKED_ITEM_LOADER) {
            return new CryptoGraphLoader(this, bundle);
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
            } else if (id == CLICKED_ITEM_LOADER) {
                Log.d(TAG, "onLoadFinished: Item clicked. Loader finished");



                ArrayList<CandleEntry> entries = (ArrayList<CandleEntry>) data;


                CandleDataSet dataSet = new CandleDataSet(entries, "# of Calls");
                dataSet.setColor(Color.rgb(80, 80, 80));
                dataSet.setShadowColor(Color.DKGRAY);
                dataSet.setShadowWidth(0.7f);
                dataSet.setDecreasingColor(Color.RED);
                dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
                dataSet.setIncreasingColor(Color.rgb(122, 242, 84));
                dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
                dataSet.setNeutralColor(Color.BLUE);
                dataSet.setValueTextColor(Color.RED);

                getLoaderManager().destroyLoader(CLICKED_ITEM_LOADER);

                CandleData candleData = new CandleData(dataSet);
                chart.setData(candleData);
                chart.invalidate();

                int count = entries.size();
                ArrayList<String> timeArray = new ArrayList<String>();
                // the labels that should be drawn on the XAxis
                for (int i = 0; i < count; i++) {
                    timeArray.add("Time"+i);
                }
                final String[] quarters = timeArray.toArray(new String[0]);
                IAxisValueFormatter formatter = new IAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return quarters[(int) value];
                    }


                };

                XAxis xAxis = chart.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);
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