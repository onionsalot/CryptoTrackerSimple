package org.example.trongnguyen.cryptotracker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.data.CandleEntry;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CryptoGraphLoader extends AsyncTaskLoader<List<CandleEntry>> {
    String mURL;
    String[] mStrings;
    private static String OPERATION_ADD = "add";
    private static String GET_URL = "url";

    public CryptoGraphLoader(Context context, Bundle bundle) {
        super(context);
        Log.d(TAG, "CryptoGraphLoader: stated");
        mURL = bundle.getString(GET_URL);
        mStrings = bundle.getStringArray(OPERATION_ADD);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<CandleEntry> loadInBackground() {
        if (mURL == null) {
            return null;
        }
        try {
            URL searchURL = NetworkUtils.buildURL(mURL);
            String currencyResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
            ArrayList<CandleEntry> list = NetworkUtils.extractFeatureFromJson(currencyResults, mStrings, "USD");

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
