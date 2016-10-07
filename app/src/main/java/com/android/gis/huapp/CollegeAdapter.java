package com.android.gis.huapp;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CollegeAdapter extends ArrayAdapter<PlacesData> {

    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<PlacesData> mColegeData = new ArrayList<>();

    public CollegeAdapter(Context mContext, int layoutResourceId, ArrayList<PlacesData> mColegeData) {
        super(mContext, layoutResourceId, mColegeData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mColegeData = mColegeData;
    }

    public void ClrData()
    {
        this.mColegeData.clear();
    }
    /**
     * Updates grid data and refresh grid items.
     *
     * @param mColegeData
     */
    public void setColegeData(ArrayList<PlacesData> mColegeData) {
        this.mColegeData = mColegeData;
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
            holder.Name = (TextView) row.findViewById(R.id.list_item_colege);
            //holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        PlacesData item = mColegeData.get(position);
        holder.Name.setText(Html.fromHtml(item.getName()));

        //Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        TextView Name;
        ImageView imageView;
    }
}