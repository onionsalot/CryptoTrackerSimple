package org.example.trongnguyen.cryptotracker;

public class Ticker {
    private String mName;
    private String mPrice;
    private String mTicker;
    private String mPicture;

    public String getName() {
        return mName;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getTicker() {
        return mTicker;
    }

    public String getPicture() {
        return mPicture;
    }

    public Ticker() {
    }

    public Ticker(String name, String price) {
        mName = name;
        mPrice = price;
    }

    public Ticker(String name, String ticker, String picture) {
        mName = name;
        mTicker = ticker;
        mPicture = picture;
    }
}
