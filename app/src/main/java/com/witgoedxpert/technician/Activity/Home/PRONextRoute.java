package com.witgoedxpert.technician.Activity.Home;

import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.witgoedxpert.technician.Adapters.AdapterHomeRoute;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.RouteHomeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PRONextRoute extends AppCompatActivity {
    ArrayList<RouteHomeModel> routeHomeModelArrayList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView_route;
    String str_userid, str_name, accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronext_route);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER, "");
        str_name = sharedPreferences.getString(NAME, "");
        accessToken = sharedPreferences.getString(SERVER_TOKEN, "");
        recyclerView_route = findViewById(R.id.recyclerView_route);
        recyclerView_route.setLayoutManager((new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false)));

        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Assign Routes");
        GetData();
    }

    public void GetData() {
        routeHomeModelArrayList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(PRONextRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.upcommingAssigned, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("routeHomeModelArrayList", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();

                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            RouteHomeModel bmi_model = new RouteHomeModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                            bmi_model.assign_date = BMIReport.getString("assign_date");
                            bmi_model.day = BMIReport.getString("day");
                            bmi_model.jsonArray = (BMIReport.getJSONArray("routeName"));

                            routeHomeModelArrayList.add(bmi_model);
                        }
                        recyclerView_route.setAdapter(new AdapterHomeRoute(routeHomeModelArrayList, PRONextRoute.this));


                    } else {
                        // Toast.makeText(PRONextRoute.this, "Designer Not Available", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();

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
                params.put("pro_id", str_userid);
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