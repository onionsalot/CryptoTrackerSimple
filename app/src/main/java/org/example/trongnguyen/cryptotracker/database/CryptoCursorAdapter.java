package org.example.trongnguyen.cryptotracker.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import org.example.trongnguyen.cryptotracker.R;

import static android.content.ContentValues.TAG;


public class CryptoCursorAdapter extends RecyclerView.Adapter<CryptoCursorAdapter.ViewHolder> {
    public TextView nameTextView;
    public TextView priceTextView;
    public TextView mTickerView;
    public TextView mPicture;
    CursorAdapter mCursorAdapter;
    Context mContext;
    final private ListItemClickListener mOnClickListener;

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public CryptoCursorAdapter(Context context, Cursor c, ListItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
        mCursorAdapter = new CursorAdapter(mContext, c, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                // Inflate custom view

                return inflater.inflate(R.layout.individual_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

                Log.d(TAG, "bindView: " +  cursor.getPosition() + "");
                // Find the columns of pet attributes that we're interested in
                int nameColumnIndex = cursor.getColumnIndex(CryptoContract.CryptoEntry.COLUMN_CRYPTO_NAME);
                int priceColumnIndex = cursor.getColumnIndex(CryptoContract.CryptoEntry.COLUMN_CRYPTO_PRICE);

                // Read the pet attributes from the Cursor for the current pet
                String cryptoName = cursor.getString(nameColumnIndex);
                String cryptoPrice = cursor.getString(priceColumnIndex);

                // Update the TextViews with the attributes for the current pet
                nameTextView.setText(cryptoName);
                priceTextView.setText(cryptoPrice);

            }

            @Override
            public Cursor getCursor() {
                return super.getCursor();
            }
        };

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mCursorAdapter.newView(mContext,mCursorAdapter.getCursor(),parent);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView,mContext,mCursorAdapter.getCursor());

    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            priceTextView = (TextView) itemView.findViewById(R.id.tv_price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: clicked");
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    // ListItemClick interface that will define the listener
    public interface ListItemClickListener {
        void onListItemClick(int clickedIndex);
    }

    public void changeCursor (Cursor cursor) {
        this.mCursorAdapter.swapCursor(cursor);
        notifyDataSetChanged();
    }

    public Cursor getCursor(){
        return mCursorAdapter.getCursor();
    }
}
