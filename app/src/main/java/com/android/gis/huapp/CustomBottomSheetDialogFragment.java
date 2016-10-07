package com.android.gis.huapp;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomBottomSheetDialogFragment extends BottomSheetDialogFragment {
RecycleData element;
    TextView title;
    TextView date;
    ImageView img;
    TextView messge;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public static CustomBottomSheetDialogFragment newInstance(RecycleData element) {
        CustomBottomSheetDialogFragment frag = new CustomBottomSheetDialogFragment();
        Bundle argsBundle = new Bundle();
        argsBundle.putString("img",element.getImg());
        argsBundle.putString("title", element.getTitle());
        argsBundle.putString("date", element.getStartDate());
        argsBundle.putString("messege",element.getMessage());
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
         element = getArguments().getParcelable("data");
        String Title=getArguments().getString("title");
        String Message=getArguments().getString("messege");
        String Date=getArguments().getString("date");
        final String Img=getArguments().getString("img");

        View contentView = layoutInflater.inflate( R.layout.dialog_news, viewGroup);
        title= (TextView) contentView.findViewById(R.id.poptitle);
        date= (TextView) contentView.findViewById(R.id.popdate);
        img= (ImageView) contentView.findViewById(R.id.popimg);
        messge= (TextView) contentView.findViewById(R.id.popmessage);
        title.setText(Title);
        date.setText(Date);
        messge.setText(Message);
        byte[] decodedString = Base64.decode(String.valueOf(Img), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        img.setImageBitmap(decodedByte);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), images.class);
                intent.putExtra("imgstring", Img);
                startActivity(intent);
            }
        });
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }


        return dialog;
    }

   /* @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_news, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }*/
}
