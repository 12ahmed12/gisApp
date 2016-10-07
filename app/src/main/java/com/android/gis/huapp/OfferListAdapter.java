package com.android.gis.huapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

//import com.squareup.picasso.Picasso;

public class OfferListAdapter extends ArrayAdapter<Shop_offers> {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<Shop_offers> offers = new ArrayList<>();
    ArrayList<String> data;

    public OfferListAdapter(Context mContext, int layoutResourceId, ArrayList<Shop_offers> offers) {
        super(mContext, layoutResourceId, offers);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.offers = offers;

    }

    public void ClrData()
    {
        this.offers.clear();
    }
    /**
     * Updates grid data and refresh grid items.
     *
     * @param offers
     */
    public void Setoffers(ArrayList<Shop_offers> offers) {
        this.offers = offers;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.offer_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

          Shop_offers offer=offers.get(position);
          String img=offer.getOffer();
        byte[] decodedString = Base64.decode(String.valueOf(img), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageView.setImageBitmap(decodedByte);
        //Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}