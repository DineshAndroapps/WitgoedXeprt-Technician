package com.witgoedxpert.technician.Activity.Home;

import static com.witgoedxpert.technician.Forms.LoginActivity.ADDRESS;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.PHONENO;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.witgoedxpert.technician.Activity.MyOrder;
import com.witgoedxpert.technician.Activity.ProfilePage_A;
import com.witgoedxpert.technician.Adapters.AdapterSchedule;
import com.witgoedxpert.technician.Forms.LoginActivity;
import com.witgoedxpert.technician.Helper.Adapter_ClickListener;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SchedulePage_A extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy ");
    SimpleDateFormat fmtOut_ = new SimpleDateFormat("EEEE ");
    SharedPreferences sharedPreferences;
    String str_userid, str_name, token;
    View headerView;

    SharedPreferences.Editor editor;
    ArrayList<OrderModel> home_data_list = new ArrayList<>();
    RecyclerView rv_list;
    String type;
    AdapterSchedule orders_adapter;
    private ActionBar actionBar;
    private Toolbar toolbar;
    Date date_main;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_page);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");

        initToolbar();
        initNavigationMenu();
        date_main = new Date();
        LocalDate localDate = date_main.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        String date_get = year + "-" + month + "-" + day;

        Date date_ = null;
        try {
            date_ = fmt.parse(date_get);
            ((TextView) findViewById(R.id.date_show)).setText(fmtOut_.format(date_main) + " " + fmtOut.format(date_main));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        findViewById(R.id.date_picker_actions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SchedulePage_A.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        //   editDate.setText(i2 + "-" + (i1 + 1) + "-" + i);
                        String date_get = i + "-" + (i1 + 1) + "-" + i2;
                        Date date = null;
                        try {
                            date = fmt.parse(date_get);
                            ((TextView) findViewById(R.id.date_show)).setText(fmtOut_.format(date) + " " + fmtOut.format(date));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        home_data_list.clear();
                        GetData(fmt.format(date));
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1);
                datePickerDialog.show();
            }
        });

        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(SchedulePage_A.this, LinearLayoutManager.VERTICAL, false));
        rv_list.setAdapter(orders_adapter);


    }

    void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.title)).setText("Home");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("BDM");
    }

    private void GetData(String date_) {
        home_data_list.clear();
        final ProgressDialog progressDialog = new ProgressDialog(SchedulePage_A.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        Log.e("date_format", "GetData: " + Constant.AssignMechanic);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.AssignMechanic, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Assign_data", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.GONE);
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
                            placeModel.flag = BMIReport.getString("flag");
                            placeModel.service_staus = "pending";
                            Log.d("BookedSlot", "onResponse: " + BMIReport.optJSONObject("BookedSlot"));

                            JSONObject BookedSlot = BMIReport.optJSONObject("BookedSlot");
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
                            ;//book_now=0 book_later=1
                            home_data_list.add(placeModel);
                        }
                        rv_list.setAdapter(new AdapterSchedule(home_data_list, (AppCompatActivity) SchedulePage_A.this));


                    } else {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                        rv_list.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
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
                params.put("assigned", "");
                params.put("date", date_);
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
        RequestQueue requestQueue = Volley.newRequestQueue(SchedulePage_A.this);
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);


    }

    TextView navUsername;

    void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.navigationView);
        headerView = nav_view.getHeaderView(0);
        //TextView logout_date = (TextView) headerView.findViewById(R.id.logout_date);
        navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        TextView navUseremail = (TextView) headerView.findViewById(R.id.navUseremail);
        //logout_date.setText("20-09-2021 (1.0)");


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);

        // open drawer at start
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent i = new Intent(getApplicationContext(), SchedulePage_A.class);
            finish();
            startActivity(i);


        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(getApplicationContext(), ProfilePage_A.class);
            i.putExtra("flag", "1");
            i.putExtra("user_id", sharedPreferences.getString(USER, ""));
            finish();
            startActivity(i);


        } else if (id == R.id.nav_sefie) {
            Intent i = new Intent(getApplicationContext(), MyOrder.class);
            i.putExtra("flag", "1");
            i.putExtra("user_id", sharedPreferences.getString(USER, ""));
            startActivity(i);

        } else if (id == R.id.nav_help) {
            DialogForLogout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void DialogForLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SchedulePage_A.this);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage(R.string.logout_confirmation_text);
        dialog.setNegativeButton(R.string.CANCEL, null);
        dialog.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClearAllSherf();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void ClearAllSherf() {
        SharedPreferences sharedpreferences =
                getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void getuserdata() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        SendFCM(token);
                        //   getUser(token);

                        Log.d("TOKEN_", "onComplete: " + token);
                    }
                });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.isNetworkAvailable(SchedulePage_A.this)) {
            GetData(fmt.format(date_main));
            getuserdata();
        } else {
            Toast.makeText(SchedulePage_A.this, "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

    }

    private void SendFCM(String token) {

//    final ProgressDialog progressDialog = new ProgressDialog(SchedulePage_A.this);
//    progressDialog.setCancelable(false);
//    progressDialog.setMessage("Loading..");
//    progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.mechanicDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            code = jsonObject.getString("code");
                            message = jsonObject.getString("message");

                            Log.d("resp", "onResponse: " + jsonObject + "===" + code);
                            if (code.equals("200")) {

                                JSONObject user_data = jsonObject.getJSONObject("Mechanic_data");

                                sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                navUsername.setText(user_data.getString("name"));
                                editor.putString(USER, user_data.getString("id"));
                                editor.putString(USER_ID, user_data.getString("id"));
                                editor.putString(NAME, user_data.getString("name"));
                                editor.putString(PHONENO, user_data.getString("mobile"));
                                editor.putString(ADDRESS, user_data.getString("address"));
                                editor.apply();
                            } else {
                                // Toast.makeText(SchedulePage_A.this, "" + message, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                // Toast.makeText(SchedulePage_A.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mechanic_id", str_userid);
                params.put("fcm_id", token);
                params.put("fcm_flag", "1");// 1 -Update FCM and 0- Don't update fcm
                Log.d("params", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };

        Volley.newRequestQueue(SchedulePage_A.this).add(stringRequest);


    }

    public void onClickCalled(OrderModel bookModel, int position, String s) {
        SendSms(bookModel.enquiry_id, bookModel.user_id,bookModel.slot_date);


    }

    private void SendSms(String enquiry_id, String userid,String booking_date) {

        final ProgressDialog progressDialog = new ProgressDialog(SchedulePage_A.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.sendSMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            code = jsonObject.getString("code");
                            message = jsonObject.getString("message");

                            Log.d("resp", "onResponse: " + jsonObject + "===" + code);
                            if (code.equals("200")) {
                                progressDialog.dismiss();
                                Toast.makeText(SchedulePage_A.this, "Sms sent successfully", Toast.LENGTH_SHORT).show();
                                getuserdata();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SchedulePage_A.this, "" + message, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                        // progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                // Toast.makeText(SchedulePage_A.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mechanic_id", str_userid);
                params.put("enquiry_id", enquiry_id);
                params.put("user_id", userid);
                params.put("date", booking_date);
                params.put("flag", "1");
                Log.d("params_sms", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };

        Volley.newRequestQueue(SchedulePage_A.this).add(stringRequest);


    }

}