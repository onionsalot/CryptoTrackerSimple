package org.example.trongnguyen.cryptotracker;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements TickerAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<List<Ticker>>{
    Toast mToast;
    ArrayList<Ticker> tickerList = new ArrayList<>();
    private EditText mEditText;
    private Button mSearchButton;
    private RecyclerView mRecyclerView;
    TickerAdapter adapter;
    private static String OPERATION_ADD = "add";
    private static String GET_URL = "url";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = (RecyclerView) findViewById(R.id.search_rv);

        adapter = new TickerAdapter(tickerList, this,true);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        mEditText = (EditText) findViewById(R.id.search_text);
        mSearchButton = (Button) findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = mEditText.getText().toString();
                QueryForCoins(searchText);
            }
        });
    }

    @Override
    public void onListItemClick(int clickedIndex) {
        mToast = Toast.makeText(this, "Item clicked " + clickedIndex, Toast.LENGTH_SHORT);
        mToast.show();
        Ticker ticker = tickerList.get(clickedIndex);
        Intent intent = new Intent();
        intent.putExtra("addedCrypto", ticker.getTicker());
        intent.putExtra("addedCryptoName", ticker.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void QueryForCoins(String searchText) {
        tickerList.clear();
        adapter.notifyDataSetChanged();
        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(22);
        Bundle bundle = new Bundle();
        //TODO fix this. Temp solution. Need to split words user types into an array to search.
        String[] searchItems = new String[]{searchText};
        bundle.putStringArray(OPERATION_ADD, searchItems);
        String currencyURL = NetworkUtils.buildUri();
        bundle.putString(GET_URL, currencyURL);
        if (loader == null) {
            loaderManager.initLoader(20,bundle,this);
        } else {
            loaderManager.restartLoader(20,bundle,this);
        }
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
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Ticker>> loader) {
    }
}
