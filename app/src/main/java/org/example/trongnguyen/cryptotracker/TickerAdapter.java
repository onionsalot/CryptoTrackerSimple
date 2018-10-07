package org.example.trongnguyen.cryptotracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static android.content.ContentValues.TAG;

// Basic adapter with ViewHolder defined.
public class TickerAdapter
        extends RecyclerView.Adapter<TickerAdapter.ViewHolder>{
    // Member variable for the tickers
    private List<Ticker> mTicker;
    private Boolean mFromSearch = false;
    // Constructor
    public TickerAdapter(List<Ticker> tickers, ListItemClickListener listener, Boolean fromSearch) {
        mTicker = tickers;
        mOnClickListener = listener;
        mFromSearch = fromSearch;
    }

    // Primary building blocks of an adapter; onCreateViewHolder, onBindViewHolder and getItemCount.


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate custom view
        View tickerView;
        if (mFromSearch) {
            tickerView = inflater.inflate(R.layout.individual_item_search, viewGroup, false);
        } else {
            tickerView = inflater.inflate(R.layout.individual_item, viewGroup, false);
        }
        // Return holder instance
        ViewHolder viewHolder = new ViewHolder(tickerView);
        return new ViewHolder(tickerView);
    }

    // Used to populate the data
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get data at position
        Ticker ticker = mTicker.get(position);

        if (mFromSearch) {
            TextView nameView = holder.mNameView;
            nameView.setText(ticker.getName());
            TextView tickerView = holder.mTickerView;
            tickerView.setText(ticker.getTicker());
        } else {
            TextView nameView = holder.mNameView;
            nameView.setText(ticker.getName());
            TextView priceView = holder.mPriceView;
            priceView.setText(ticker.getPrice());
        }
    }

    // Gets total count of items in the list
    @Override
    public int getItemCount() {
        return mTicker.size();
    }

    // Used to cache views and define views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Member variables for the Views
        public TextView mNameView;
        public TextView mPriceView;
        public TextView mTickerView;
        public TextView mPicture;
        // Constructor for the entire item row. Also looks up the views
        public ViewHolder(View itemView) {
            super(itemView);
            if (mFromSearch) {
                mNameView = (TextView) itemView.findViewById(R.id.single_search_title);
                mTickerView = (TextView) itemView.findViewById(R.id.single_search_ticker);
            } else {
                mNameView = (TextView) itemView.findViewById(R.id.tv_name);
                mPriceView = (TextView) itemView.findViewById(R.id.tv_price);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    // Member variable to store reference to the click listener. Need to be in the constructor to initialize
    final private ListItemClickListener mOnClickListener;

    // ListItemClick interface that will define the listener
    public interface ListItemClickListener {
        void onListItemClick(int clickedIndex);
    }

}
