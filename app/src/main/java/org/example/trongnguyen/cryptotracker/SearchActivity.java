package org.example.trongnguyen.cryptotracker;

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

public class SearchActivity extends AppCompatActivity implements TickerAdapter.ListItemClickListener{
    Toast mToast;
    ArrayList<Ticker> tickerList = new ArrayList<>();
    private EditText mEditText;
    private Button mSearchButton;
    private RecyclerView mRecyclerView;
    TickerAdapter adapter;
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
    }

    private void QueryForCoins(String searchText) {
        tickerList.clear();
        adapter.notifyDataSetChanged();
        new FetchCurrencyData().execute(searchText);
    }

    // AsyncTask method used to fetch the data.
    public class FetchCurrencyData extends AsyncTask<String, Void, ArrayList<Ticker>> {

        @Override
        protected ArrayList<Ticker> doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            URL currencyURL = NetworkUtils.buildUrl();
            try {
                String currencyResults = NetworkUtils.getResponseFromHttpUrl(currencyURL);
                ArrayList<Ticker> list = NetworkUtils.extractFeatureFromJson(currencyResults,strings,"USD",1);

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
