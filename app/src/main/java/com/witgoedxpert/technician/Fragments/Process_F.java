package com.witgoedxpert.technician.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.witgoedxpert.technician.Forms.Functions.getStringFromEdit;
import static com.witgoedxpert.technician.Forms.LoginActivity.ADDRESS;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.witgoedxpert.technician.Activity.ListOfRoute;
import com.witgoedxpert.technician.Activity.MyOrderDetails;
import com.witgoedxpert.technician.Adapters.AdapterAppointment;
import com.witgoedxpert.technician.Forms.AddEnquiry;
import com.witgoedxpert.technician.Helper.Adapter_ClickListener;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.Methods;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.EmpModel;
import com.witgoedxpert.technician.model.OrderModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Process_F extends Fragment {


    public Process_F() {
        // Required empty public constructor
    }


    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_email, str_branch_data = "";
    SharedPreferences.Editor editor;
    ArrayList<OrderModel> home_data_list = new ArrayList<>();
    RecyclerView rv_list;
    String type;
    View v;
    Context context;
    AdapterAppointment orders_adapter;

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void On_Item_Click(int postion, Object Model, View view) {

                OrderModel model = (OrderModel) Model;
                /*0=pending,1=process,2=start,3=complete,4=reject*/

                switch (view.getId()) {
                    case R.id.btn_start:
                        api_Yes_NO(model.id, "2");//start
                        break;
                    case R.id.btn_end:
                        OpenDialog(model);
                        //  api_Yes_NO(model.id, "3");//end
                        break;
                    case R.id.btn_details_p:
                        Intent intent = new Intent(context, MyOrderDetails.class);
                        intent.putExtra("MyClass", model);
                        context.startActivity(intent);
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

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    String str_name_pro, str_customer_id = "", str_product_id = "", str_main_id, date_get, userGender = "0";
    private RadioGroup radioGenderGroup;
    private RadioButton radio_F, radio_M;
    View promptsView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void OpenDialog(OrderModel model) {
        str_product_id = model.product_id;

        LayoutInflater li = LayoutInflater.from(context);
        promptsView = li.inflate(R.layout.activity_add_enquairy, null);
        alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialog = alertDialogBuilder.create();
        GetCurrent_Date();

        radioGenderGroup = promptsView.findViewById(R.id.radioGrp);
        radio_F = promptsView.findViewById(R.id.radioF);
        radio_M = promptsView.findViewById(R.id.radioM);
        ((TextView) promptsView.findViewById(R.id.toolbr_lbl)).setText(str_name_pro);
        textInputLayout(R.id.et_name).setText((sharedPreferences.getString(NAME, "")));
        textInputLayout(R.id.et_address).setText((sharedPreferences.getString(ADDRESS, "")));

        promptsView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textInputLayout(R.id.et_name).getText().toString().equals("")) {
                    if (!textInputLayout(R.id.et_address).getText().toString().equals("")) {
                        if (!textInputLayout(R.id.et_cost).getText().toString().equals("")) {
                            if (!textInputLayout(R.id.et_time).getText().toString().equals("")) {
                                if (!textInputLayout(R.id.et_parts).getText().toString().equals("")) {
                                    if (!textInputLayout(R.id.et_total).getText().equals("")) {
                                        if (!textInputLayout(R.id.et_signature).getText().equals("")) {
                                            if (Constant.isNetworkAvailable(context)) {
                                                SubmitData(model.id);
                                            } else {
                                                Methods.ShowMsg(getActivity(), getString(R.string.check_net));
                                            }


                                        } else {
                                            Methods.ShowMsg(getActivity(), getString(R.string.error_name));
                                        }

                                    } else {
                                        Methods.ShowMsg(getActivity(), getString(R.string.error_address));
                                    }

                                } else {
                                    Methods.ShowMsg(getActivity(), getString(R.string.error_cost));
                                }

                            } else {
                                Methods.ShowMsg(getActivity(), "Enter the valid time");
                            }

                        } else {
                            Methods.ShowMsg(getActivity(), getString(R.string.error_part));
                        }


                    } else {
                        Methods.ShowMsg(getActivity(), getString(R.string.error_total));
                    }
                } else {
                    Methods.ShowMsg(getActivity(), getString(R.string.error_sing));
                }

            }
        });

        radioGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int childCount = group.getChildCount();

                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);


                    if (btn.getId() == R.id.radioM) {
                        btn.setText("Yes");
                    } else {
                        btn.setText("No");
                    }
                    if (btn.getId() == checkedId) {

                        if (btn.getText().toString().equals("Yes")) {
                            userGender = "1";
                        } else {
                            userGender = "0";
                        }

                    }

                }

                Log.e("Gender", userGender);
            }
        });


        // show it
        alertDialog.show();
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
                        progressDialog.dismiss();
                        CallHomeData();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ListAllProduct, new Response.Listener<String>() {
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
                        v.findViewById(R.id.rv_list).setVisibility(View.VISIBLE);


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
                            placeModel.service_staus = "process";
                            placeModel.service_charge = BMIReport.getString("service_charge");
                            placeModel.time = BMIReport.getString("time");
                            placeModel.date = BMIReport.getString("date");
                            placeModel.service_status = BMIReport.getString("service_status");
                            placeModel.status = BMIReport.getString("status");
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
                params.put("assigned", "1");
/*
                0=pending,1=process,2=start,3=complete,4=reject
*/
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

    private EditText textInputLayout(int id) {

        return ((EditText) promptsView.findViewById(id));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void GetCurrent_Date() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        date_get = year + "-" + month + "-" + day;
    }

    private void SubmitData(String id_main) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.addEnquiry,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e("jsonObject_", "onResponse: " + response);

                            if (jsonObject.getString("code").equals("200")) {
                                api_Yes_NO(id_main, "3");//end
                                alertDialog.cancel();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("product_id", str_product_id);
                params.put("user_id", str_userid);
                params.put("cost", getStringFromEdit(textInputLayout(R.id.et_cost)));
                params.put("time", getStringFromEdit(textInputLayout(R.id.et_time)));
                params.put("parts", getStringFromEdit(textInputLayout(R.id.et_parts)));
                params.put("total", getStringFromEdit(textInputLayout(R.id.et_total)));
                params.put("name", getStringFromEdit(textInputLayout(R.id.et_name)));
                params.put("appointment", userGender);
                params.put("new_appointment", "0");
                params.put("address", getStringFromEdit(textInputLayout(R.id.et_address)));
                params.put("signature", getStringFromEdit(textInputLayout(R.id.et_signature)));
                params.put("date", date_get);
                Log.e("params", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };


        Volley.newRequestQueue(getActivity()).add(stringRequest);

    }

}


