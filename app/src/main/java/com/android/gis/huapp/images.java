/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.android.gis.huapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class images extends AppCompatActivity {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";

    private TextView mCurrMatrixTv;

    private PhotoViewAttacher mAttacher;

    private Toast mCurrentToast;

    private Matrix mCurrentDisplayMatrix = null;
    String image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

       Bundle bundle= getIntent().getExtras();

        if(bundle!=null)
        {
            image=bundle.getString("imgstring");
        }

        byte[] decodedString = Base64.decode(String.valueOf(image), Base64.DEFAULT);
        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ImageView mImageView = (ImageView) findViewById(R.id.iv_photo);
        mCurrMatrixTv = (TextView) findViewById(R.id.tv_current_matrix);
        Drawable bitmap = getResources().getDrawable(R.drawable.menu7);
        mImageView.setImageBitmap(decodedByte);

        // The MAGIC happens here!
        mAttacher = new PhotoViewAttacher(mImageView);

        // Lets attach some listeners, not required though!
        mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
        mAttacher.setOnSingleFlingListener(new SingleFlingListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Need to call clean-up
        mAttacher.cleanup();
    }


    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }

        @Override
        public void onOutsidePhotoTap() {
            showToast("You have a tap event on the place where out of the photo.");
        }
    }

    private void showToast(CharSequence text) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(images.this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            //mCurrMatrixTv.setText(rect.toString());
        }
    }

    private class SingleFlingListener implements PhotoViewAttacher.OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (BuildConfig.DEBUG) {
                Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            }
            return true;
        }
    }
}
