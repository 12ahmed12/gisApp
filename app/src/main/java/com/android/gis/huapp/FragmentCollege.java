package com.android.gis.huapp;
import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FragmentCollege extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<com.android.gis.huapp.PlacesData> coleges;
    com.android.gis.huapp.CollegeAdapter colegeAdapter;
    ListView colegeList;
    boolean isImageFitToScreen;
    private Animator mCurrentAnimator;
    //private OnFragmentInteractionListener mListener;

    public FragmentCollege() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOne.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCollege newInstance(String param1, String param2) {
        FragmentCollege fragment = new FragmentCollege();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_fragment_college, container, false);
        coleges=new ArrayList<>();
        colegeList= (ListView) rootView.findViewById(R.id.ListColege);

      /*  colegeAdapter = new com.android.gis.huapp.CollegeAdapter(getActivity(), R.layout.place_row, coleges);
        colegeList.setAdapter(colegeAdapter);*/

        colegeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.android.gis.huapp.PlacesData college = colegeAdapter.getItem(position);
                Toast.makeText(getActivity(), college.getName(), Toast.LENGTH_LONG).show();
                showPopup(getActivity(), college);

            }
        });
        return rootView;


    }

    private void showPopup(final Activity context, final com.android.gis.huapp.PlacesData college) {
       int Height=500;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.builds_item, viewGroup);
        TextView title= (TextView) layout.findViewById(R.id.poptitle);
        TextView details= (TextView) layout.findViewById(R.id.popDetails);


        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);

        popup.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popup.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        popup.setOutsideTouchable(false);

        title.setText(college.getName());
        details.setText(college.getDetails());
        // Getting a reference to Close button, and close the popup when clicked.
        TextView showMap = (TextView) layout.findViewById(R.id.showinmap);
        TextView showdetails = (TextView) layout.findViewById(R.id.showDetails);
        showdetails.setAlpha(0.0f);
        showMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
                Intent intent = new Intent(context,PlaceInMap.class);
                intent.putExtra("name",college.getName().toString());
                startActivity(intent);
            }
        });
      /*  img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    img.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    img.setAdjustViewBounds(true);
                }else{
                    isImageFitToScreen=true;
                    img.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    img.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });*/
    }



    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public void update(){
      /*  coleges.clear();
        colegeAdapter.clear();*/
        GetPlaces task=new GetPlaces(getActivity());
        task.execute();
    }

    public class GetPlaces extends AsyncTask<Void, Void, String> {
        Context ctx;
        String result = "ckecking..";
        String jsonUrl;
        ProgressDialog progressDialog;
        GetPlaces(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog= ProgressDialog.show(getActivity(), "Downloading Data", "Wait Please...", false,true);
            jsonUrl="http://hugis2.esy.es/get_Places.php";
        }
        @Override
        protected String doInBackground(Void... params) {
            String JasonStr=null;
            try {
                URL url = new URL(jsonUrl);
                Log.d("url", "" + url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));

                StringBuilder stringBuilder=new StringBuilder();

                String line;
                StringBuffer buffer=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }
                Log.d("dataaaa", ""+buffer);

                JasonStr=buffer.toString();
                GetNewsDataFromJson(JasonStr);
                IS.close();
                bufferedReader.close();
                httpURLConnection.disconnect();
                if (buffer != null) {
                    result = ""+buffer;
                }


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
                final String LIST = "Places";
                final String ID = "id";
                final String NAME = "Place_name";
                final String WIDTH = "Pwidth";
                final String HEIGHT="Pheight";
                final String PLACETYPE="placeType";
                final String DETAILS="placeDetails";


                JSONObject JsonObj = new JSONObject(JsonStr);
                JSONArray MoviesArray = JsonObj.getJSONArray(LIST);
                com.android.gis.huapp.PlacesData item;
                String ImgExtra ;

                coleges.clear();




                for (int i = 0; i < MoviesArray.length(); i++) {
                    item =new com.android.gis.huapp.PlacesData();
                    JSONObject dataObj = MoviesArray.getJSONObject(i);

                    String id=dataObj.getString(ID);
                    String name = dataObj.getString(NAME);
                    String width = dataObj.getString(WIDTH);
                    String height = dataObj.getString(HEIGHT);
                    String type=dataObj.getString(PLACETYPE);
                    String details=dataObj.getString(DETAILS);


                    item.setName(name);
                    item.setWidth(width);
                    item.setHeight(height);
                    item.setType(type);
                    item.setId(id);
                    item.setDetails(details);
                    Log.d("item type", item.getType());

                    if(item.getType().equals("1"))
                    {
                        coleges.add(item);
                        Log.d("itemRes", "item found as library");
                    }


                }
            }catch (JSONException  e)
            {
                e.printStackTrace();
            }



        }

        @Override
        protected void onPostExecute(String res) {
            //Log.d("finalRes", res);
            progressDialog.dismiss();
            for (com.android.gis.huapp.PlacesData g:coleges) {
                Log.d("colege name ",g.getName());
            }
            colegeAdapter = new com.android.gis.huapp.CollegeAdapter(getActivity(), R.layout.place_row, coleges);
            colegeList.setAdapter(colegeAdapter);

            /*colegeAdapter = new ArrayAdapter<String>
                    (getActivity(), R.layout.fragment_fragment_college, R.id.list_item_colege, coleges);
            colegeList.setAdapter(colegeAdapter);*/
            colegeAdapter.notifyDataSetChanged();
            //colegeList.deferNotifyDataSetChanged();

        }

    }

   /* // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
