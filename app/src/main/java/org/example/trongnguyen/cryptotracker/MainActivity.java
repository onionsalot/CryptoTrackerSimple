package org.example.trongnguyen.cryptotracker;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TickerAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Ticker>> {

    ArrayList<Ticker> tickerList = new ArrayList<>();
    private Toast mToast;
    private TextView mDataPrint;
    private Button addButton;
    private Button searchButton;
    TickerAdapter adapter;
    URL currencyURL;
    String[] searchItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvTickers = (RecyclerView) findViewById(R.id.main_recycler);


        // Create the Adapter
        adapter = new TickerAdapter(tickerList, this, false);
        // Attach the adapter to the recycler view
        rvTickers.setAdapter(adapter);
        // Set layout manager to the position of the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTickers.setLayoutManager(layoutManager);

        // Temporary Text View
        mDataPrint = (TextView) findViewById(R.id.print_data);
        // Temporary Button
        addButton = (Button) findViewById(R.id.add_test);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] testArray = {"VEN"};
                makeCurrencyQuery(testArray);
            }
        });
        searchButton = (Button) findViewById(R.id.search_test);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
            }
        });
        searchItems = new String[]{"BTC", "ETH"};
        makeCurrencyQuery(searchItems);
        // initialize the ticker;
//        Ticker ticker = new Ticker("Bitcoin", "1000");
//        tickerList.add(ticker);
//        ticker = new Ticker("Ethereum", "10000");
//        tickerList.add(ticker);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);



    }

    public void makeCurrencyQuery(String[] currency) {
        currencyURL = NetworkUtils.buildUrl(currency);
    }
    @Override
    public void onListItemClick(int clickedIndex) {
        mToast = Toast.makeText(this, "Item clicked " + clickedIndex, Toast.LENGTH_SHORT);
        mToast.show();
        Ticker ticker = tickerList.get(clickedIndex);
        mDataPrint.setText(ticker.getName());
//        if (clickedIndex == 0) {
//            makeCurrencyQuery("BTC");
//        } else {
//            makeCurrencyQuery("ETH");
//        }
    }

    @Override
    public Loader<List<Ticker>> onCreateLoader(int i, Bundle bundle) {
        return new CryptoLoader(this,currencyURL,searchItems);
    }

    @Override
    public void onLoadFinished(Loader<List<Ticker>> loader, List<Ticker> tickers) {
        if (tickers != null) {
            tickerList.addAll(tickers);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Ticker>> loader) {
    }


    // AsyncTask method used to fetch the data.
    public class FetchCurrencyData extends AsyncTask<String, Void, ArrayList<Ticker>> {

        @Override
        protected ArrayList<Ticker> doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            //String currencyQuery = strings[0];
            URL currencyURL = NetworkUtils.buildUrl(strings);
            try {
                String currencyResults = NetworkUtils.getResponseFromHttpUrl(currencyURL);
                ArrayList<Ticker> list = NetworkUtils.extractFeatureFromJson(currencyResults,strings,"USD", 0);

                return list;
            } catch ( Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Ticker> s) {
            if (s != null) {
                tickerList.addAll(s);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
