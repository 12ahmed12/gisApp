package com.android.gis.huapp;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class notificationDialogFragment extends BottomSheetDialogFragment {
    RecycleData element;
    TextView shopName;
    TextView startdate;
    TextView enddate;
    TextView messge;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public static notificationDialogFragment newInstance(com.android.gis.huapp.notification element) {
        notificationDialogFragment frag = new notificationDialogFragment();
        Bundle argsBundle = new Bundle();
        argsBundle.putString("shopname",element.getShop_name());
        argsBundle.putString("start",element.getStartDate());
        argsBundle.putString("end", element.getEndDate());
        argsBundle.putString("messege",element.getMessge());
        frag.setArguments(argsBundle);
        return frag;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }


    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        LinearLayout viewGroup = (LinearLayout) getActivity().findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String shop = getArguments().getString("shopname");
        String start=getArguments().getString("start");
        String end=getArguments().getString("end");
        String message=getArguments().getString("messege");


        View contentView = layoutInflater.inflate( R.layout.dialog_notification, viewGroup);
        shopName= (TextView) contentView.findViewById(R.id.notshopname);
        startdate= (TextView) contentView.findViewById(R.id.notstart);
        enddate= (TextView) contentView.findViewById(R.id.notend);
        messge= (TextView) contentView.findViewById(R.id.notmessage);
        shopName.setText(shop);
        startdate.setText(start);
        enddate.setText(end);
        messge.setText(message);

        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        return dialog;
    }


}
