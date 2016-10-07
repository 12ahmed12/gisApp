package com.android.gis.huapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class WeatherAdapter  extends RecyclerView.Adapter<WeatherAdapter.viewHolder> {
    int ResourceLayoutId;
    ArrayList<WeatherData> mData;
    private Context mContext;
    private static ClickListener clickListener;

    public WeatherAdapter(Context mContext,ArrayList<WeatherData>mData ) {
        this.mContext=mContext;
        this.mData=mData;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        viewHolder view=new viewHolder(v);

        return view;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {

        WeatherData item=mData.get(position);


        String higdlow= String.valueOf(item.getHighandLow());
        String desc=item.getDescription();
        String day=item.getDateTime();
         double humi= item.getHumidity();
        String humityCondition;
        //String pressure=String.valueOf(item.getPressure());
        //String himi=String.valueOf(item.getHumidity());


        Log.d("dayhh",""+item.getDateTime());
        Log.d("descriptionhh", desc);

        holder.Dayname.setText(day);
        holder.Description.setText(desc);
        holder.HighTemp.setText("  درجة الحرارة  "+higdlow);
        if(humi<=20)
        {
         humityCondition="الرطوبة منخفضة "   ;
        }else if(humi>20&&humi<=40)
        {
            humityCondition="الرطوبة متوسطة";
        }else
        {
            humityCondition="الرطوبة مرتفعة";
        }
        holder.LowTemp.setText(humityCondition);
        animate(holder);
    }

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener=clickListener;
    }


    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(mContext, R.anim.abc_popup_enter);
        animAnticipateOvershoot.setDuration(1000);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    static  class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView HighTemp;
        TextView LowTemp;
        TextView Dayname;
        TextView Description;
        CardView cardView;



        public viewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            HighTemp= (TextView) cardView.findViewById(R.id.hightemp);
            LowTemp= (TextView) cardView.findViewById(R.id.lowtemp);
            Dayname= (TextView) cardView.findViewById(R.id.daynum);
            Description= (TextView) cardView.findViewById(R.id.description);

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

