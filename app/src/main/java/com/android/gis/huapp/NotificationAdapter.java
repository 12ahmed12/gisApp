package com.android.gis.huapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by spider on 20/06/2016.
 */
public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {
int ResourceLayoutId;
ArrayList<com.android.gis.huapp.notification> mData;
private Context mContext;
private static ClickListener clickListener;

    public NotificationAdapter(Context mContext,ArrayList<com.android.gis.huapp.notification>mData ) {
        this.mContext=mContext;
        this.mData=mData;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.notify_element_layout, parent, false);
        viewHolder view=new viewHolder(v);

        return view;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {

        com.android.gis.huapp.notification item=mData.get(position);
        holder.ShopName.setText(item.getShop_name());
        holder.Message.setText(item.getMessge());
        holder.StartDate.setText(item.getStartDate());
        holder.EndDate.setText(item.getEndDate());
        animate(holder);
    }

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener=clickListener;
    }


    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(mContext, R.anim.abc_popup_enter);
        animAnticipateOvershoot.setDuration(5000);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    static  class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView ShopName;
        TextView StartDate;
        TextView EndDate;
        TextView Message;
        CardView cardView;



        public viewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = (CardView) itemView.findViewById(R.id.notfycard);
            ShopName= (TextView) cardView.findViewById(R.id.shopname);
            StartDate= (TextView) cardView.findViewById(R.id.startdate);
            EndDate= (TextView) cardView.findViewById(R.id.enddate);
            Message= (TextView) cardView.findViewById(R.id.message);

        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null)
            {
                clickListener.itemClicked(v,getPosition());
            }

        }
    }

    public interface  ClickListener{
        public void itemClicked(View view,int position);
    }
}

