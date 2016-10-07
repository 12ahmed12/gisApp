package com.android.gis.huapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class View_Holder extends RecyclerView.ViewHolder  {

    CardView cv;
    TextView title;
    TextView message;
    TextView Date;
    ImageView img;

    View_Holder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        message = (TextView) itemView.findViewById(R.id.message);
        Date = (TextView) itemView.findViewById(R.id.date);
        img=(ImageView)itemView.findViewById(R.id.imageView);


    }


}