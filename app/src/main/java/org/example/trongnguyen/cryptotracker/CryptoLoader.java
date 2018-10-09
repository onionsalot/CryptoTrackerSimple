package org.example.trongnguyen.cryptotracker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CryptoLoader extends AsyncTaskLoader<List<Ticker>> {
    String mURL;
    String[] mStrings;
    private static String OPERATION_ADD = "add";
    private static String GET_URL = "url";
    public CryptoLoader(@NonNull Context context, Bundle bundle) {
        super(context);
        Log.d(TAG, "CryptoLoader: stated");
        mURL = bundle.getString(GET_URL);
        mStrings = bundle.getStringArray(OPERATION_ADD);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Ticker> loadInBackground() {
        Log.d(TAG, "loadInBackground: started");
    if (mURL == null) {
        return null;
    }
        try {
            URL searchURL = NetworkUtils.buildURL(mURL);
            String currencyResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
            ArrayList<Ticker> list = NetworkUtils.extractFeatureFromJson(currencyResults,mStrings,"USD", 0);

            return list;
        } catch ( Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
