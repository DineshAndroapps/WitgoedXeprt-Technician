package com.witgoedxpert.technician.Location_Services.PlacesSearch;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Helper.Variables;
import  com.witgoedxpert.technician.Location_Services.PlacesSearch.Adapter.PlacesAdapter;
import  com.witgoedxpert.technician.Location_Services.PlacesSearch.Inteface.GoogleLocationInterface;
import  com.witgoedxpert.technician.Location_Services.PlacesSearch.Model.GoogleLocation;
import  com.witgoedxpert.technician.Location_Services.PlacesSearch.Model.PlacePredicationModel;
import  com.witgoedxpert.technician.Location_Services.MapUtils.MapsFragment;
import  com.witgoedxpert.technician.R;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GooglePlace_Fragment extends Fragment {


    public GooglePlace_Fragment() {
        // Required empty public constructor
    }

    String tag = "";

    public GooglePlace_Fragment(String tag) {
        // Required empty public constructor
        this.tag = tag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View view;
    RecyclerView rec_places;
    PlacesAdapter placesAdapter;
    ArrayList<PlacePredicationModel> placePredicationModels = new ArrayList<>();

    EditText edt_palces;
    GoogleLocation googleLocation;

    private Timer timer = new Timer();
    private final long DELAY = 500; // in ms
    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_google_place_, container, false);

        context = getActivity();

        init();
        return view;
    }

    MapsFragment mapsFragment;

    private void init() {

        mapsFragment = new MapsFragment();

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mapsFragment)
                .commit();

        edt_palces = view.findViewById(R.id.edt_search);
        rec_places = view.findViewById(R.id.rec_places);
        rec_places.setLayoutManager(new LinearLayoutManager(getActivity()));

        googleLocation = new GoogleLocation(getActivity(), onGoogleLocationGetting);

        placesAdapter = new PlacesAdapter(getActivity(), placePredicationModels);
        rec_places.setAdapter(placesAdapter);

        rec_places.setVisibility(View.GONE);
        view.findViewById(R.id.iv_clear).setVisibility(View.GONE);

        edt_palces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = String.valueOf(charSequence);

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: do what you need here (refresh list)
                        // you will probably need to use
                        // runOnUiThread(Runnable action) for some specific
                        // actions
                        if (text.length() >= 3){
                            googleLocation.getCurrentLocation(edt_palces.getText().toString());
                        }
                    }

                }, DELAY);

                if (charSequence.length() <= 0) {
//                    view.findViewById(R.id.lnr_currrentlocation).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.txtresult).setVisibility(View.GONE);
                    view.findViewById(R.id.rec_places).setVisibility(View.GONE);
                    view.findViewById(R.id.iv_clear).setVisibility(View.GONE);

                } else {
//                    view.findViewById(R.id.txtresult).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.lnr_currrentlocation).setVisibility(View.GONE);
                    view.findViewById(R.id.rec_places).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.iv_clear).setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        placesAdapter.setLocationListener(onLocationSelect);

        view.findViewById(R.id.lnr_currrentlocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Variables.select_latitude = Variables.latitude;
                Variables.select_longitude = Variables.longitude;

//                getActivity().finish();

            }
        });

        view.findViewById(R.id.iv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_palces.setText("");
            }
        });


        TypeFace();

    }

    private void TypeFace() {
/*

        StyleDrawable searchDrawable = Functions.setFontIcon(context, null, R.string.icon_search);
        searchDrawable.setTextColor(Functions.getColorThin(R.color.progress_bar_color, context));

        ((ImageView) view.findViewById(R.id.iv_search)).setImageDrawable(searchDrawable);
*/

    }


    PlacesAdapter.OnLocationSelect onLocationSelect = new PlacesAdapter.OnLocationSelect() {
        @Override
        public void onLocationSelect(PlacePredicationModel placePredicationModel) {


            Variables.select_latitude = Double.parseDouble(placePredicationModel.getLat());
            Variables.select_longitude = Double.parseDouble(placePredicationModel.getLng());
//            Variables.select_address = placePredicationModel.getDescription();

            edt_palces.setText("");

            Log.e("GooglePlace", "Lat : " + Variables.select_latitude + "\n Long" + Variables.select_longitude);

            mapsFragment.setupDefaultScreen(Variables.select_latitude, Variables.select_longitude);
           // AppHelper.hideSoftKeyboard(context);
//            getActivity().finish();

        }
    };


    GoogleLocationInterface onGoogleLocationGetting = new GoogleLocationInterface() {
        @Override
        public void onLocationGetting(ArrayList<PlacePredicationModel> placePredicationModels) {

            context.runOnUiThread(new Runnable() {
                public void run() {
                    if (placePredicationModels.size() <= 0) {
                       // Functions.CustomeToast(context, 0, "error_no_result_found");
                    }
                }
            });


            rec_places.getRecycledViewPool().clear();
            placesAdapter.filter(placePredicationModels);
        }
    };

}