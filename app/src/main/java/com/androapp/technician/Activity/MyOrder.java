package com.androapp.technician.Activity;

import static com.androapp.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.androapp.technician.Forms.LoginActivity.USER_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androapp.technician.Helper.Connection.ServerCall.CallbackOrder;
import com.androapp.technician.model.OrderModel;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androapp.technician.Adapters.AdapterAppointment;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class MyOrder extends AppCompatActivity {

    private static final String TAG = "SELFIE";
    public String userName, userEmail, userPhone, userPassword, userAddress;
    Button register;
    TextView login;
    SharedPreferences sharedPreferences;
    String str_userid, str_intent_flag, accessToken;

    ArrayList<OrderModel> designModelArrayList = new ArrayList<>();
    RecyclerView recyclerView_appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_list);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        accessToken = sharedPreferences.getString(SERVER_TOKEN, "");
        str_userid = getIntent().getStringExtra("user_id");
        str_intent_flag = getIntent().getStringExtra("flag");
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("My Orders");
        recyclerView_appointment = findViewById(R.id.recyclerView_appointment);
        recyclerView_appointment.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        if (Constant.isNetworkAvailable(getApplicationContext())) {
            ProGetData();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

    }


    public void onClickCalled(OrderModel bookModel, int position, String s) {
        DialogForLogout(bookModel.id, bookModel.id);
    }


    private void DialogForLogout(String id, String sequence_id) {
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(MyOrder.this);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage("Do you Really Want to Delete Selfie?");
        dialog.setNegativeButton(R.string.CANCEL, null);
        dialog.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Delete_data(id);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }



    private void Delete_data(String id) {

        final ProgressDialog progressDialog = new ProgressDialog(MyOrder.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "onResponse: " + jsonObject);


        String url = Constant.MAIN+"pro_image_delete/";
        Log.e(TAG, "onResponse: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            //TODO: Handle your response here
                            if (!response.equals("null")) {

                                progressDialog.dismiss();

                                ProGetData();
                                Toast.makeText(MyOrder.this, "Selfie Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Log.d("resp", "onResponse: " + response + "===");

                            } else {
                                ProGetData();
                                progressDialog.dismiss();
                                Toast.makeText(MyOrder.this, "Selfie Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                        System.out.print(response);

                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                        progressDialog.dismiss();
                        ProGetData();

                    }


                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + accessToken);
                Log.e("header", "getHeaders: "+header );
                return header;
            }

        };;

        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }


    private Call<CallbackOrder> callbackProductCall = null;

    public void ProGetData() {
        designModelArrayList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(MyOrder.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.viewEnquiry, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.GONE);


                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("enquiry_data");

                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            OrderModel placeModel = new OrderModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                            placeModel.id = BMIReport.getString("id");
                            placeModel.user_id = BMIReport.getString("user_id");
                            placeModel.description = BMIReport.getString("description");
                            placeModel.error_code = BMIReport.getString("error_code");
                            placeModel.type_of_machine = BMIReport.getString("type_of_machine");
                            placeModel.name = BMIReport.getString("name");
                            placeModel.age_machine = BMIReport.getString("age_machine");
                            placeModel.address = BMIReport.getString("address");
                            placeModel.isDeleted = BMIReport.getString("isDeleted");
                            placeModel.phone = BMIReport.getString("phone");
                            placeModel.additional_info = BMIReport.getString("additional_info");
                            placeModel.added_date = BMIReport.getString("added_date");
                            placeModel.product_name = BMIReport.getString("product_name");
                            placeModel.image = BMIReport.getString("image");

                            designModelArrayList.add(placeModel);
                        }
                        recyclerView_appointment.setAdapter(new AdapterAppointment(designModelArrayList, MyOrder.this));


                    } else {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
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
                params.put("user_id", str_userid);
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
    public void ShowAnswerDialogImg(String image_server) {
        Log.d(TAG, "ShowAnswerDialogImg: "+image_server);

        Dialog settingsDialog = new Dialog(MyOrder.this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
        settingsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        SetGlideImage(image_server, settingsDialog.findViewById(R.id.img_sol));
        settingsDialog.show();
    }

    private void SetGlideImage(String img_url, ImageView imageView) {
        // Glide.with(SelfieList.this).load(img_url).override(1000).into(imageView);
        Picasso.get()
                .load(img_url)
                .into(imageView);
    }

}