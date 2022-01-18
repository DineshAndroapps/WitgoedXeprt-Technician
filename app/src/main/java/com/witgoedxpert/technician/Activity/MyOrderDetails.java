package com.witgoedxpert.technician.Activity;

import static com.witgoedxpert.technician.Activity.Home.MainActivity.selectPage;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.witgoedxpert.technician.Adapters.AdapterSchedule;
import com.witgoedxpert.technician.Forms.AddEnquiry;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyOrderDetails extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String str_userid, str_intent_flag, accessToken;
    ImageView image;
    OrderModel orderModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        findViewById(R.id.bt_menu).setOnClickListener(view -> onBackPressed());
        Intent i = getIntent();
        orderModel = (OrderModel) i.getSerializableExtra("MyClass");
        image = findViewById(R.id.image);
        Glide.with(getApplicationContext()).load(Constant.image_url_ + orderModel.image).placeholder(R.drawable.app_icon).into(image);


/*
        findViewById(R.id.btn_Open_signature).setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddEnquiry.class);
            intent.putExtra("str_product_id", orderModel.product_id);
            intent.putExtra("str_name_pro", orderModel.name);
            intent.putExtra("user_id", orderModel.user_id);
            intent.putExtra("main_id", orderModel.id);
            startActivity(intent);
        });
*/

        OnClick();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GetData(orderModel.id);
    }

    private void OnClick() {
        findViewById(R.id.btn_yes).setOnClickListener(v -> {
            api_Yes_NO(orderModel.id, "1");//accept
        });
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            api_Yes_NO(orderModel.id, "2");//start
        });
        findViewById(R.id.btn_end).setOnClickListener(v -> {
            api_Yes_NO(orderModel.id, "3");//complete
        });
        findViewById(R.id.btn_no).setOnClickListener(v -> {
            api_Yes_NO(orderModel.id, "4");//reject
        });


    }


    private void GetData(String enquiry_id) {

        final ProgressDialog progressDialog = new ProgressDialog(MyOrderDetails.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.enquiry_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("GetData_Assign_data", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        JSONObject jsonArrayvideo = jsonObject.getJSONObject("enquiry_data");
                        OrderModel placeModel = new OrderModel();

                        placeModel.id = jsonArrayvideo.getString("id");
                        placeModel.user_id = jsonArrayvideo.getString("user_id");
                        placeModel.product_id = jsonArrayvideo.getString("product_id");
                        placeModel.description = jsonArrayvideo.getString("description");
                        placeModel.error_code = jsonArrayvideo.getString("error_code");
                        placeModel.type_of_machine = jsonArrayvideo.getString("type_of_machine");
                        placeModel.name = jsonArrayvideo.getString("name");
                        placeModel.assigned = jsonArrayvideo.getString("assigned");
                        placeModel.age_machine = jsonArrayvideo.getString("age_machine");
                        placeModel.address = jsonArrayvideo.getString("address");
                        placeModel.isDeleted = jsonArrayvideo.getString("isDeleted");
                        placeModel.phone = jsonArrayvideo.getString("phone");
                        placeModel.additional_info = jsonArrayvideo.getString("additional_info");
                        placeModel.added_date = jsonArrayvideo.getString("added_date");
                        placeModel.product_name = jsonArrayvideo.getString("product_name");
                        placeModel.image = jsonArrayvideo.getString("image");
                        placeModel.time = jsonArrayvideo.getString("time");
                        placeModel.date = jsonArrayvideo.getString("date");
                        placeModel.status = jsonArrayvideo.getString("status");
                        placeModel.service_staus = "pending";
                        Log.d("BookedSlot", "onResponse: " + jsonArrayvideo.optJSONObject("BookedSlot"));

                        JSONObject BookedSlot = jsonArrayvideo.optJSONObject("BookedSlot");
                        if (BookedSlot != null) {
                            if (BookedSlot.has("id")) {
                                placeModel.slot_start_time = BookedSlot.getString("start_time");
                                placeModel.slot_end_time = BookedSlot.getString("end_time");
                                placeModel.slot_id = BookedSlot.getString("id");
                                placeModel.slot_date = BookedSlot.getString("booking_date");
                                placeModel.enquiry_id = BookedSlot.getString("enquiry_id");
                                placeModel.mechanic_id = BookedSlot.getString("mechanic_id");

                            }
                        }

                        ((TextView) findViewById(R.id.toolbr_lbl)).setText(placeModel.name);
                        ((TextView) findViewById(R.id.name)).setText(placeModel.name);
                        ((TextView) findViewById(R.id.description)).setText(placeModel.description);
                        ((TextView) findViewById(R.id.error_code)).setText(placeModel.error_code);
                        ((TextView) findViewById(R.id.type_of_machine)).setText(placeModel.type_of_machine);
                        ((TextView) findViewById(R.id.age_machine)).setText(placeModel.age_machine);
                        ((TextView) findViewById(R.id.address)).setText(placeModel.address);
                        ((TextView) findViewById(R.id.phone)).setText(placeModel.phone);
                        if (placeModel.additional_info.equals(null)) {
                            ((TextView) findViewById(R.id.additional_info)).setText(placeModel.additional_info);
                        } else {
                            ((TextView) findViewById(R.id.additional_info)).setText("");
                        }
                        ((TextView) findViewById(R.id.added_date)).setText(placeModel.added_date);
                        ((TextView) findViewById(R.id.product_name)).setText(placeModel.product_name);
                        ((TextView) findViewById(R.id.slot_time)).setText(placeModel.slot_start_time + " - " + placeModel.slot_end_time);
                        ((TextView) findViewById(R.id.slot_date)).setText(placeModel.slot_date );

                        findViewById(R.id.btn_Open_signature).setOnClickListener(view -> {
                            Intent intent = new Intent(getApplicationContext(), AddEnquiry.class);
                            intent.putExtra("str_product_id", orderModel.product_id);
                            intent.putExtra("str_name_pro", orderModel.name);
                            intent.putExtra("user_name", placeModel.name);
                            intent.putExtra("user_address", placeModel.address);
                            intent.putExtra("user_id", orderModel.user_id);
                            intent.putExtra("main_id", orderModel.id);
                            startActivity(intent);
                        });



/*
0=pending
1=process
2=start
3=complete
4=reject
5=cancelled
*/
                        Log.e("assigned", "onResponse: " + placeModel.assigned);
                        if (placeModel.assigned.equals("0")) { //pending
                            findViewById(R.id.div_pending).setVisibility(View.VISIBLE);
                            findViewById(R.id.div_process).setVisibility(View.GONE);
                        } else if (placeModel.assigned.equals("1")) {//process
                            findViewById(R.id.div_pending).setVisibility(View.GONE);
                            findViewById(R.id.div_process).setVisibility(View.VISIBLE);
                        } else if (placeModel.assigned.equals("2")) {//Started
                            findViewById(R.id.div_pending).setVisibility(View.GONE);
                            findViewById(R.id.div_process).setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_start).setVisibility(View.GONE);
                        } else if (placeModel.assigned.equals("3")) {//complete
                            findViewById(R.id.div_pending).setVisibility(View.GONE);
                            findViewById(R.id.div_process).setVisibility(View.GONE);
                            findViewById(R.id.btn_Open_signature).setVisibility(View.VISIBLE);
                        } else if (placeModel.assigned.equals("4")) {
                            findViewById(R.id.div_alldone).setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_comment).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.btn_comment)).setText("Cancelled By You");
                            findViewById(R.id.div_pending).setVisibility(View.GONE);
                            findViewById(R.id.div_process).setVisibility(View.GONE);
                        } else if (placeModel.assigned.equals("5")) {
                            findViewById(R.id.div_alldone).setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_comment).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.btn_comment)).setText("Cancelled By User");
                            findViewById(R.id.div_pending).setVisibility(View.GONE);
                            findViewById(R.id.div_process).setVisibility(View.GONE);
                        } else if (placeModel.assigned.equals("6")) {
                            findViewById(R.id.div_alldone).setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_comment).setVisibility(View.VISIBLE);
                            findViewById(R.id.btn_bill).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.btn_comment)).setText("Service Completed");
                            findViewById(R.id.div_pending).setVisibility(View.GONE);
                            findViewById(R.id.div_process).setVisibility(View.GONE);
                            findViewById(R.id.btn_Open_signature).setVisibility(View.GONE);
                        }

                    } else {
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
                params.put("enquiry_id", enquiry_id);

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
        RequestQueue requestQueue = Volley.newRequestQueue(MyOrderDetails.this);
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);


    }

    private void api_Yes_NO(String id, String flag) {
        final ProgressDialog progressDialog = new ProgressDialog(MyOrderDetails.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.booking, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        GetData(orderModel.id);

                    } else {
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
                params.put("mechanic_id", str_userid);
                params.put("booking_id", orderModel.id);
                params.put("assigned", flag);
                /* 0=pending,1=process/accept,2=complete, 3=reject*/
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
        RequestQueue requestQueue = Volley.newRequestQueue(MyOrderDetails.this);
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }

}