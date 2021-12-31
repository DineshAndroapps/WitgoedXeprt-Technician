package com.witgoedxpert.technician.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.witgoedxpert.technician.Activity.Home.MainActivity.selectPage;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.witgoedxpert.technician.Activity.Home.MainActivity;
import com.witgoedxpert.technician.Adapters.AdapterAppointment;
import com.witgoedxpert.technician.Helper.Adapter_ClickListener;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Pending_F extends Fragment {

    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_email, str_branch_data = "";
    SharedPreferences.Editor editor;
    ArrayList<OrderModel> home_data_list = new ArrayList<>();
    RecyclerView rv_list;
    String type;
    AdapterAppointment orders_adapter;

    public Pending_F() {
        // Required empty public constructor
    }

    View v;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pending_, container, false);
        context = getContext();
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");
        rv_list = v.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        orders_adapter = new AdapterAppointment(home_data_list, (AppCompatActivity) getActivity(), new Adapter_ClickListener() {
            @Override
            public void On_Item_Click(int postion, Object Model, View view) {

                OrderModel model = (OrderModel) Model;
                /*0=pending,1=process,2=start,3=complete,4=reject*/
                switch (view.getId()) {
                    case R.id.btn_yes:
                        api_Yes_NO(model.id, "1");//accept
                        break;
                    case R.id.btn_no:
                        api_Yes_NO(model.id, "4");//reject
                        break;
                }

            }
        });
        rv_list.setAdapter(orders_adapter);

        if (Constant.isNetworkAvailable(getActivity())) {
            CallHomeData();
        } else {
            Toast.makeText(getActivity(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void api_Yes_NO(String id, String flag) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                        if (flag.equals("1")) {
                            selectPage(1);
                        } else {
                            CallHomeData();
                        }

                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        v.findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    v.findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
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
                params.put("booking_id", id);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }


    public void CallHomeData() {
        home_data_list.clear();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.AssignMechanic, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        v.findViewById(R.id.no_slot).setVisibility(View.GONE);
                        rv_list.setVisibility(View.VISIBLE);

                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("Assign_data");

                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            OrderModel placeModel = new OrderModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                            placeModel.id = BMIReport.getString("id");
                            placeModel.user_id = BMIReport.getString("user_id");
                            placeModel.product_id = BMIReport.getString("product_id");
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
                            placeModel.service_charge = BMIReport.getString("service_charge");
                            placeModel.time = BMIReport.getString("time");
                            placeModel.date = BMIReport.getString("date");
                            placeModel.status = BMIReport.getString("status");
                            placeModel.service_staus = "pending";
                            ;//book_now=0 book_later=1
                            home_data_list.add(placeModel);
                        }
                        // rv_list.setAdapter(new AdapterAppointment(home_data_list, (AppCompatActivity) getActivity()));
                        orders_adapter.notifyDataSetChanged();


                    } else {
                        progressDialog.dismiss();
                        v.findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                        rv_list.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    v.findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                    rv_list.setVisibility(View.GONE);
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
                params.put("assigned", "0");
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

    }

}


