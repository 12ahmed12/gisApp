package com.android.gis.huapp;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ShopDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    private Toolbar mToolbar;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    com.android.gis.huapp.SessionManager sessionManager;
    String shop_id;
    String shopAccID;
    com.android.gis.huapp.ShopData shopData;
    ArrayList<com.android.gis.huapp.ShopData> ShopList;
    ArrayList<com.android.gis.huapp.Shop_offers>offersList;
    ArrayList<com.android.gis.huapp.Shop_offers>offersList2;
    private com.android.gis.huapp.OfferListAdapter mListAdapter;
    private ListView mListView;
    ProgressDialog progressDialog;
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        sessionManager =new com.android.gis.huapp.SessionManager(getApplicationContext());
       /* boolean loged=sessionManager.checkLogin();

        if(loged) {
            User user = sessionManager.getUserDetails();
            Log.d("user_name", user.getName());
            Log.d("user_username", user.getUsername());
        }*/
        ShopList=new ArrayList<>();
        offersList=new ArrayList<>();
        offersList2=new ArrayList<>();
        BackGroundShop bgshop=new BackGroundShop(this);


        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            shop_id = bundle.getString("ID");
            Log.d("shop id_preferance", shop_id);
            bgshop.execute(shop_id);
        }else
        {
            SharedPreferences prefs =getSharedPreferences("X", 0);
            SharedPreferences.Editor editor = prefs.edit();
            String id=prefs.getString("ID",null);
            shop_id=id;
            Log.d("shop id_preferance", shop_id);
            bgshop.execute(shop_id);
        }



        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Shop Details");
        getSupportActionBar().setSubtitle("H-U-App");
        mDrawer = (NavigationView) findViewById(R.id.main_navigate);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mListView = (ListView)findViewById(R.id.list_offers);
        mListAdapter = new com.android.gis.huapp.OfferListAdapter(getApplicationContext(), R.layout.griditem, offersList);
    }



    @Override
    protected void onResume() {
        super.onResume();
       sessionManager.checkLogin();
    }



    public class BackGroundShop extends AsyncTask<String, Void, String> {
        Context ctx;
        String result = "ckecking..";

        BackGroundShop(Context ctx) {
            progressDialog= ProgressDialog.show(ShopDetails.this, "Downloading Menu And Offers","Please Wait...", false);
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            ShopList.clear();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String Shop_Account_url = "http://hugis2.esy.es/getShop_Acc.php";
            //Log.d("ahmed", "hi");
            String ID = params[0];

            Log.d("id", ID);
            String JasonStr=null;

            try {
                URL url = new URL(Shop_Account_url);
                Log.d("url", "" + url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8");

                bufferedWriter.write(data);
                Log.d("id ", data);
                bufferedWriter.flush();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String L;
                String M = "";
                M = bufferedReader.readLine();
                Log.d("bbbbb", M);
                if(M.contains("error"))
                {
                    Log.d("error","something wrong");
                    result = M;
                }else
                {
                    JasonStr=M.toString();
                    GetNewsDataFromJson(JasonStr);
                    Log.d("found shop account", "correct data");
                    result="success";

                }




                os.close();
                bufferedWriter.close();
                IS.close();
                bufferedReader.close();
                httpURLConnection.disconnect();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        private void GetNewsDataFromJson(String JsonStr)
                throws JSONException

        {
            try {
                // These are the names of the JSON objects that need to be extracted.
                final String LIST = "shop";

                final String SHOP_NAME = "shop_name";
                final String SHOP_MENU1 = "image";
                final String SHOP_MENU2 = "image2";
                final String SHOP_STATUS="shop_status";
                final String SHOP_ID="shop_id";



                JSONObject JsonObj = new JSONObject(JsonStr);
                JSONArray MoviesArray = JsonObj.getJSONArray(LIST);
                com.android.gis.huapp.ShopData shop;
                String ImgExtra ;

                // data.clear();

                for (int i = 0; i < MoviesArray.length(); i++) {
                    shop = new com.android.gis.huapp.ShopData();
                    JSONObject dataObj = MoviesArray.getJSONObject(i);

                    String name = dataObj.getString(SHOP_NAME);
                    String shop_menu1 = dataObj.get(SHOP_MENU1).toString();
                    String shop_menu2 = dataObj.get(SHOP_MENU2).toString();
                    String shop_satus=dataObj.getString(SHOP_STATUS);
                    String id=dataObj.getString(SHOP_ID);

                    shop.setId(id);
                    shop.setShopName(name);
                    shop.setShopMenu1(shop_menu1);
                    shop.setShopMenu2(shop_menu2);
                    shop.setShopState(shop_satus);
                    shopAccID=shop.getId();
                    ShopList.add(shop);



                    Log.d("shop ID:", shop.getId());
                    Log.d("shop menu1:",shop.getShopMenu1());
                    Log.d("shop menu2:", shop.getShopMenu2());


                    //data.add(item);

                }
            }catch (JSONException  e)
            {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String res) {

            Log.d("finalRes", res);

            for (com.android.gis.huapp.ShopData d:ShopList) {
                Log.d("AccID",d.getId());
                Log.d("menu1:",d.getShopMenu1());
                Log.d("menu2:",d.getShopMenu2());
                BackGroundOffers of=new BackGroundOffers(ctx);
                of.execute(d.getId());
            }

        }
    }


    public class BackGroundOffers extends AsyncTask<String, Void, String> {
        Context ctx;
        String result = "ckecking..";

        BackGroundOffers(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
           // mListAdapter.clear();
            offersList.clear();
            offersList2.clear();
            mListAdapter.clear();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {
            String offers_url = "http://hugis2.esy.es/getOffers.php";
            //Log.d("ahmed", "hi");
            String shop_id = params[0];

            Log.d("accoID", shop_id);
            String JasonStr=null;

            try {
                URL url = new URL(offers_url);
                Log.d("url", "" + url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = URLEncoder.encode("shop_id", "UTF-8") + "=" + URLEncoder.encode(shop_id, "UTF-8");

                bufferedWriter.write(data);
                Log.d("input ", data);
                bufferedWriter.flush();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                String M=null;
                String line;
                StringBuffer buffer=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }
                M=""+buffer;

                Log.d("bbbbb", M);
                if(M.contains("error"))
                {
                    Log.d("error","something wrong");
                    result = M;
                }else
                {
                    JasonStr=M.toString();
                    GetNewsDataFromJson(JasonStr);
                    Log.d("found shop offers", "correct data");
                    result="success";

                }




                os.close();
                bufferedWriter.close();
                IS.close();
                bufferedReader.close();
                httpURLConnection.disconnect();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        private void GetNewsDataFromJson(String JsonStr)
                throws JSONException

        {
            try {
                // These are the names of the JSON objects that need to be extracted.
                final String LIST = "offers";
                final String OFFER = "offer";
                final String START = "offer_startdate";
                final String END = "offer_enddate";
                final String OFFER_ID="offer_id";



                JSONObject JsonObj = new JSONObject(JsonStr);
                JSONArray MoviesArray = JsonObj.getJSONArray(LIST);
                com.android.gis.huapp.Shop_offers offer;
                String ImgExtra ;

                // data.clear();

                for (int i = 0; i < MoviesArray.length(); i++) {
                    offer = new com.android.gis.huapp.Shop_offers();
                    JSONObject dataObj = MoviesArray.getJSONObject(i);

                    String offerimg = dataObj.get(OFFER).toString();
                    String OfferId=dataObj.getString(OFFER_ID);
                    String offerStart=dataObj.getString(START);
                    String offerEnd=dataObj.getString(END);

                    offer.setId(OfferId);
                    offer.setStart(offerStart);
                    offer.setEnd(offerEnd);
                    offer.setOffer(offerimg);


                    offersList.add(offer);

                    Log.d("offer ID:", offer.getId());
                    Log.d("offer:",offer.getOffer());



                    //data.add(item);

                }
            }catch (JSONException  e)
            {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String res) {

            Log.d("finalRes", res);

            for (com.android.gis.huapp.Shop_offers d:offersList) {
                Log.d("Offer:",d.getOffer());
            }

            boolean loged=sessionManager.isLoggedIn();


            if(loged) {
                com.android.gis.huapp.ShopData element=ShopList.get(0);
                final String im=element.getShopMenu1();
                byte[] decodedString = Base64.decode(String.valueOf(im), Base64.DEFAULT);
                final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mListAdapter = new com.android.gis.huapp.OfferListAdapter(ctx, R.layout.griditem, offersList);
                View headerView = ((LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menuheader, null, false);
                View offerView = ((LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.griditem, null, false);

                final ImageView imageView= (ImageView) headerView.findViewById(R.id.menu);
                imageView.setImageBitmap(decodedByte);
                mListView.addHeaderView(headerView);
                mListView.setAdapter(mListAdapter);
                progressDialog.dismiss();
                mListAdapter.notifyDataSetChanged();

                final ImageView imgoffer= (ImageView) offerView.findViewById(R.id.offer_item_image);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //zoomImageFromThumb(imageView, decodedByte);
                        if(im!=null) {
                            Intent intent = new Intent(ShopDetails.this, images.class);
                            intent.putExtra("imgstring", im);
                            startActivity(intent);
                        }
                    }
                });

                Shop_offers omenu=new Shop_offers();
                omenu.setOffer(im);
                offersList2.add(omenu);
                for(int i=0; i<offersList.size(); i++)
                {
                    Shop_offers o=offersList.get(i);

                    offersList2.add(o);
                }

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Shop_offers of=offersList2.get(position);
                        String source=of.getOffer();
                        byte[] decodedString = Base64.decode(String.valueOf(source), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        //zoomImageFromThumb(imgoffer,decodedByte);
                        Intent intent = new Intent(ShopDetails.this, images.class);
                        intent.putExtra("imgstring", source);
                        startActivity(intent);
                    }
                });


                // Retrieve and cache the system's default "short" animation time.
                mShortAnimationDuration = getResources().getInteger(
                        android.R.integer.config_shortAnimTime);
            }
            }
        }

  /*  private void zoomImageFromThumb(final View thumbView, Bitmap imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expandedimg);
        expandedImageView.setImageBitmap(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }


                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
                return false;
            }
        });



        *//*expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }


                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });*//*
    }*/




    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void navigate(int mSelectedId) {
        Intent intent = null;

        if (mSelectedId == R.id.navigation_item_1) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, SearchMap.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_2) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, AllBuilds.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_3) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, WeatherToday.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_4) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, com.android.gis.huapp.News.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_5) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, Notifications.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_6) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();

        navigate(mSelectedId);
        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, mSelectedId);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {
            sessionManager.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
