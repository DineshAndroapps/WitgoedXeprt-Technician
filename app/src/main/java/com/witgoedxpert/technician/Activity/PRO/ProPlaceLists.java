package com.witgoedxpert.technician.Activity.PRO;

import static com.witgoedxpert.technician.Forms.LoginActivity.EMAIL;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID_INFO;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.witgoedxpert.technician.Adapters.AdapterPlace;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.PlaceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProPlaceLists extends AppCompatActivity {
    ArrayList<PlaceModel> proPlaceListRoute = new ArrayList<>();
    ArrayList<PlaceModel> proPlaceListRoute_copy = new ArrayList<>();
    RecyclerView recyclerView_design;
    RecyclerView recyclerView_cat;
    private Toolbar toolbar;
    String user_id_info;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_email, str_route_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_place_lists);
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("List Of Place");
        Intent i = getIntent();
        str_route_id = i.getStringExtra("id");
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");
        str_email = sharedPreferences.getString(EMAIL, "");
        user_id_info = sharedPreferences.getString(USER_ID_INFO, "");


        recyclerView_design = findViewById(R.id.recyclerView_place);
        recyclerView_design.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        if (Constant.isNetworkAvailable(getApplicationContext())) {
            ProGetData();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.btnPlot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProPlaceLists.this, ProMapActivity.class);
                intent.putExtra(Constant.DATA, proPlaceListRoute_copy);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        ProGetData();
        ;
    }

    public void ProGetData() {
        final ProgressDialog progressDialog = new ProgressDialog(ProPlaceLists.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.FatchPlaceByRoute, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_places_pro", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.GONE);

                        JSONArray jsonArrayvideo = jsonObject.optJSONArray("route_data");

                        if (jsonArrayvideo != null && jsonArrayvideo.length() > 0) {
                            proPlaceListRoute = new ArrayList<>();
                            proPlaceListRoute_copy = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("route_data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                PlaceModel placeModel = new PlaceModel();
                                JSONObject BMIReport = jsonArray.getJSONObject(i);

                                placeModel.id = BMIReport.getString("id");
                                placeModel.bde_id = BMIReport.getString("bde_id");
                                placeModel.route_id = BMIReport.getString("route_id");
                                placeModel.sequence_id = BMIReport.getString("sequence_id");
                                placeModel.name = BMIReport.getString("name");
                                placeModel.latitude = BMIReport.getString("latitude");
                                placeModel.longitude = BMIReport.getString("longitude");
                                placeModel.isDeleted = BMIReport.getString("isDeleted");
                                placeModel.branchAssigned = BMIReport.getString("branchAssigned");
                                placeModel.city = BMIReport.getString("city");
                                placeModel.state = BMIReport.getString("state");
                                placeModel.contactPerson = BMIReport.getString("contactPerson");
                                placeModel.mobile = BMIReport.getString("mobile");
                                placeModel.phone = BMIReport.getString("phone");
                                placeModel.email = BMIReport.getString("email");
                                placeModel.category = BMIReport.getString("category");
                                placeModel.photo = BMIReport.getString("photo");


                                proPlaceListRoute.add(placeModel);
                                proPlaceListRoute_copy.add(placeModel);
                            }

                            if (proPlaceListRoute.size() > 0) {
                                AdapterPlace adapterDesign = new AdapterPlace(proPlaceListRoute, ProPlaceLists.this);
                                recyclerView_design.setAdapter(adapterDesign);
                                adapterDesign.notifyDataSetChanged();
                            } else {
                                findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                            }

                        } else {
                            findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                        }

                    } else {
                        findViewById(R.id.no_slot).setVisibility(View.VISIBLE);

                        //  Toast.makeText(ProPlaceLists.this, "Designer Not Available", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
                Log.d("proPlaceListRoute", "onResponse: " + proPlaceListRoute_copy.size());
                if (proPlaceListRoute_copy.size() > 0) {
                    proPlaceListRoute_copy.add(proPlaceListRoute_copy.size(), proPlaceListRoute_copy.get(0));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap();
                params.put("route_id", str_route_id);
                Log.d("params", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("token", Constant.Token);
                return header;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }

}