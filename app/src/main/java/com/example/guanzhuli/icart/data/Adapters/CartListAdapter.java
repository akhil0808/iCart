package com.example.guanzhuli.icart.data.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.guanzhuli.icart.R;
import com.example.guanzhuli.icart.data.DBManipulation;
import com.example.guanzhuli.icart.data.Item;
import com.example.guanzhuli.icart.data.SPManipulation;

import java.util.List;

/**
 * Created by Guanzhu Li on 1/3/2017.
 */
public class CartListAdapter extends RecyclerView.Adapter<CartListViewHolder>{
    private RequestQueue mRequestQueue;
    Context mContext;
    Activity mActivity;
    LayoutInflater inflater;
    ImageLoader mImageLoader;
    List<Item> mItemArrayList;
    public CartListAdapter(Context context, List<Item> objects, Activity activity) {
        this.mActivity = activity;
        this.mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        inflater = LayoutInflater.from(context);
        mItemArrayList = objects;
    }

    @Override
    public CartListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_cart_item, parent, false);
        CartListViewHolder cartListViewHolder = new CartListViewHolder(v);
        return cartListViewHolder;
    }

    @Override
    public void onBindViewHolder(final CartListViewHolder holder, final int position) {
        holder.mTextCartId.setText(mItemArrayList.get(position).getId());
        holder.mTextCartquant.setText(Integer.toString(mItemArrayList.get(position).getQuantity()));
        holder.mTextCartName.setText(mItemArrayList.get(position).getName());
        holder.mTextCartPrice.setText(Double.toString(mItemArrayList.get(position).getPrice()));
        holder.mImage.setImageUrl(mItemArrayList.get(position).getImageUrl(), mImageLoader);
        holder.mButtonQuantAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = mItemArrayList.get(position).getQuantity();
                mItemArrayList.get(position).setQuantity(++temp);
                holder.mTextCartquant.setText(Integer.toString(temp));
                TextView mTextTotal = (TextView) mActivity.findViewById(R.id.cart_total);
                double result = Double.parseDouble(mTextTotal.getText().toString());
                result += mItemArrayList.get(position).getPrice();
                mTextTotal.setText(String.valueOf(result));
            }
        });
        holder.mButtonQuantMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = mItemArrayList.get(position).getQuantity();
                if (temp == 0) {
                    return;
                }
                TextView mTextTotal = (TextView) mActivity.findViewById(R.id.cart_total);
                double result = Double.parseDouble(mTextTotal.getText().toString());
                result -= mItemArrayList.get(position).getPrice();
                mTextTotal.setText(String.valueOf(result));
                mItemArrayList.get(position).setQuantity(--temp);
                holder.mTextCartquant.setText(Integer.toString(temp));
            }
        });
        holder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // restart the activity
                TextView mTextTotal = (TextView) mActivity.findViewById(R.id.cart_total);
                double result = Double.parseDouble(mTextTotal.getText().toString());
                result -= mItemArrayList.get(position).getQuantity() * mItemArrayList.get(position).getPrice();
                mTextTotal.setText(String.valueOf(result));
                String s = new SPManipulation().getValue(mContext);
                String[] temp = s.split(" ");
                new DBManipulation(mContext, temp[0] + temp[2]).delete(mItemArrayList.get(position).getId());
                mItemArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemChanged(position, mItemArrayList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemArrayList.size();
    }
}

class CartListViewHolder extends RecyclerView.ViewHolder {
    NetworkImageView mImage;
    TextView mTextCartName, mTextCartId, mTextCartquant, mTextCartPrice;
    ImageButton mButtonQuantAdd, mButtonQuantMinus;
    ImageView mButtonDelete;

    public CartListViewHolder(View itemView) {
        super(itemView);
        mImage = (NetworkImageView) itemView.findViewById(R.id.cart_item_image);
        mTextCartName = (TextView) itemView.findViewById(R.id.cart_item_name);
        mTextCartId = (TextView) itemView.findViewById(R.id.cart_item_id);
        mTextCartquant = (TextView) itemView.findViewById(R.id.cart_item_quant);
        mTextCartPrice = (TextView) itemView.findViewById(R.id.cart_item_price);
        mButtonQuantAdd = (ImageButton) itemView.findViewById(R.id.cart_quant_add);
        mButtonQuantMinus = (ImageButton) itemView.findViewById(R.id.cart_quant_minus);
        mButtonDelete = (ImageView) itemView.findViewById(R.id.cart_item_delete);
    }
}