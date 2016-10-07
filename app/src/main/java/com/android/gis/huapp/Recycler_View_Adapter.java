package com.android.gis.huapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Collections;
import java.util.List;

public class Recycler_View_Adapter extends RecyclerView.Adapter<View_Holder> implements RecyclerView.OnItemTouchListener{
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    GestureDetector mGestureDetector;
    List<com.android.gis.huapp.RecycleData> list = Collections.emptyList();
    Context context;

    public Recycler_View_Adapter(List<com.android.gis.huapp.RecycleData> list, Context context,OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }



    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {
        com.android.gis.huapp.RecycleData element=list.get(position);

        String message=element.getMessage();
        if(message.length()>60)
        {
            String part=message.substring(0,50)+"...";
            holder.message.setText(part);
        }else
        {
            holder.message.setText(message);
        }
        String im=element.getImg();
        byte[] decodedString = Base64.decode(String.valueOf(im), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(element.getTitle());
        holder.Date.setText(element.getStartDate());
        holder.img.setImageBitmap(decodedByte);

        animate(holder);

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, com.android.gis.huapp.RecycleData data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(com.android.gis.huapp.RecycleData data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}