package com.witgoedxpert.technician.Activity.PRO;

import static com.witgoedxpert.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomCollection extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String accessToken, formName;
    String date_get="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_collection);
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(SERVER_TOKEN, "");

        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Custom Collection");

        EditText et_date = findViewById(R.id.et_date);
        et_date.setEnabled(false);
        findViewById(R.id.date_picker_actions) .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CustomCollection.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        et_date.setText(i2 + "-" + (i1 + 1) + "-" + i);
                        date_get = i + "-" + (i1 + 1) + "-" + i2;
                        Date date = null;
                        getData(date_get);
                    }
                }, mYear, mMonth, mDay);
                //datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1);
                datePickerDialog.show();

            }
        });
        findViewById(R.id.ll_date) .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CustomCollection.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        et_date.setText(i2 + "-" + (i1 + 1) + "-" + i);
                        date_get = i + "-" + (i1 + 1) + "-" + i2;
                        Date date = null;
                        getData(date_get);
                    }
                }, mYear, mMonth, mDay);
                //datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1);
                datePickerDialog.show();

            }
        });


    }

    public void getData(String formID) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.ProLeadPerformanceCustomFilter + formID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("form_event_pro", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    ((TextView) findViewById(R.id.yest_count)).setText("Lead Collected:"+jsonObject.getString("lead_collected"));
                    JSONArray yesterday_data_array = jsonObject.getJSONArray("status_data");

                    String CounsellingDone = yesterday_data_array.getJSONObject(0).getString("value");
                    String AdmissionDone = yesterday_data_array.getJSONObject(1).getString("value");
                    String ApplicationDone = yesterday_data_array.getJSONObject(2).getString("value");
                    String FollowUp = yesterday_data_array.getJSONObject(3).getString("value");
                    String CallNotMade = yesterday_data_array.getJSONObject(4).getString("value");
                    String CallNotResponding = yesterday_data_array.getJSONObject(5).getString("value");
                    String NotInterested = yesterday_data_array.getJSONObject(6).getString("value");
                    String WrongNumber = yesterday_data_array.getJSONObject(7).getString("value");
                    String DuplicateLead = yesterday_data_array.getJSONObject(8).getString("value");


                    ((TextView) findViewById(R.id.AdmissionDone)).setText(AdmissionDone);
                    ((TextView) findViewById(R.id.ApplicationDone)).setText(ApplicationDone);
                    ((TextView) findViewById(R.id.CounsellingDone)).setText(CounsellingDone);
                    ((TextView) findViewById(R.id.CallNotMade)).setText(CallNotMade);
                    ((TextView) findViewById(R.id.CallNotResponding)).setText(CallNotResponding);
                    ((TextView) findViewById(R.id.NotInterested)).setText(NotInterested);
                    ((TextView) findViewById(R.id.WrongNumber)).setText(WrongNumber);
                    ((TextView) findViewById(R.id.Duplicate)).setText(DuplicateLead);
                    ((TextView) findViewById(R.id.walk_in)).setText(FollowUp);

                    ((TextView) findViewById(R.id.AdmissionDone_n)).setText(yesterday_data_array.getJSONObject(0).getString("key"));
                    ((TextView) findViewById(R.id.ApplicationDone_n)).setText(yesterday_data_array.getJSONObject(1).getString("key"));
                    ((TextView) findViewById(R.id.CounsellingDone_n)).setText(yesterday_data_array.getJSONObject(2).getString("key"));
                    ((TextView) findViewById(R.id.walk_in_n)).setText(yesterday_data_array.getJSONObject(3).getString("key"));
                    ((TextView) findViewById(R.id.CallNotMade_n)).setText(yesterday_data_array.getJSONObject(4).getString("key"));
                    ((TextView) findViewById(R.id.CallNotResponding_n)).setText(yesterday_data_array.getJSONObject(5).getString("key"));
                    ((TextView) findViewById(R.id.NotInterested_n)).setText(yesterday_data_array.getJSONObject(6).getString("key"));
                    ((TextView) findViewById(R.id.WrongNumber_n)).setText(yesterday_data_array.getJSONObject(7).getString("key"));
                    ((TextView) findViewById(R.id.Duplicate_n)).setText(yesterday_data_array.getJSONObject(8).getString("key"));



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + accessToken);
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