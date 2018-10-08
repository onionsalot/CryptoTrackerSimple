package org.example.trongnguyen.cryptotracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CryptoLoader extends AsyncTaskLoader<List<Ticker>> {
    URL mURL;
    String[] mStrings;
    public CryptoLoader(@NonNull Context context, URL currencyURL, String[] searchItems) {
        super(context);
        mURL = currencyURL;
        mStrings = searchItems;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Ticker> loadInBackground() {
    if (mURL == null) {
        return null;
    }
        try {
            String currencyResults = NetworkUtils.getResponseFromHttpUrl(mURL);
            ArrayList<Ticker> list = NetworkUtils.extractFeatureFromJson(currencyResults,mStrings,"USD", 0);

            return list;
        } catch ( Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
