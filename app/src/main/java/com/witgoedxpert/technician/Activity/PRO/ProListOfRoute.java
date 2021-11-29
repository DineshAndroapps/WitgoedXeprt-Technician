package com.witgoedxpert.technician.Activity.PRO;

import static com.witgoedxpert.technician.Forms.LoginActivity.BRANCH_DATA;
import static com.witgoedxpert.technician.Forms.LoginActivity.EMAIL;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID_INFO;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.witgoedxpert.technician.Adapters.AdapterDesign;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.BranchModel;
import com.witgoedxpert.technician.model.CategoryModel;
import com.witgoedxpert.technician.model.EmpModel;
import com.witgoedxpert.technician.model.RouteModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProListOfRoute extends AppCompatActivity {
    ArrayList<RouteModel> designModelArrayList = new ArrayList<>();
    ArrayList<BranchModel> branchModelArrayList = new ArrayList<>();
    ArrayList<EmpModel> proModelArrayList = new ArrayList<>();
    RecyclerView recyclerView_design;
    RecyclerView recyclerView_cat;
    ImageButton bt_menu;
    private Toolbar toolbar;
    String user_id_info, str_branch_data;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_email;
    LinearLayout ll_bg;
    CategoryModel categoryModel;
    EditText title;
    ArrayList<String> branch_list_str = new ArrayList<>();
    ArrayList<String> pro_list_str = new ArrayList<>();
    private String SELECT_SUBJECT = "Select Branch";
    private String SELECT_PRO = "Select PRO";
    private Button btnDate;
    ArrayList<RouteModel> proListRoute = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_category_details);
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("List Of Route");
        Intent i = getIntent();
       /* Bundle bundle = i.getExtras();
        categoryModel = (CategoryModel) bundle.getSerializable("model");*/
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");
        str_email = sharedPreferences.getString(EMAIL, "");
        user_id_info = sharedPreferences.getString(USER_ID_INFO, "");
        str_branch_data = sharedPreferences.getString(BRANCH_DATA, "");

        title = findViewById(R.id.title);
        btnDate = findViewById(R.id.btn_date);

        recyclerView_design = findViewById(R.id.recyclerView_design);
        recyclerView_design.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        //  recyclerView_design.setLayoutManager((new GridLayoutManager(getApplicationContext(), 2)));

        SimpleDateFormat dateFormatterDesign = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();

        if (Constant.isNetworkAvailable(getApplicationContext())) {

            if (loadDate().equalsIgnoreCase("")) {
                btnDate.setText(dateFormatterDesign.format(calendar.getTime()));
                ProGetData(dateFormatterDesign.format(calendar.getTime()));
            } else {
                btnDate.setText(loadDate());
                ProGetData(loadDate());
            }

        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        Log.d("str_branch_data", "onCreate: " + str_branch_data);
        findViewById(R.id.btn_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProListOfRoute.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                saveDate("pref_date", dateFormatterDesign.format(newDate.getTime()));
                                btnDate.setText(dateFormatterDesign.format(newDate.getTime()));
                                ProGetData(dateFormatterDesign.format(newDate.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

    }

    public void ProGetData(String selectedDate) {
        proListRoute.clear();
        final ProgressDialog progressDialog = new ProgressDialog(ProListOfRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ProListRoute, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_route_pro", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.GONE);
                        recyclerView_design.setVisibility(View.VISIBLE);
                        JSONArray jsonArrayvideo = jsonObject.optJSONArray("route_data");

                        if (jsonArrayvideo != null && jsonArrayvideo.length() > 0) {
                            proListRoute = new ArrayList<>();
                            for (int i = 0; i < jsonArrayvideo.length(); i++) {
                                RouteModel bmi_model = new RouteModel();
                                JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                                bmi_model.id = BMIReport.getString("id");
                                bmi_model.bde_id = BMIReport.getString("pro_id");
                                bmi_model.route_id = BMIReport.getString("route_id");
                                bmi_model.routeName = BMIReport.getString("routeName");
                                bmi_model.isDeleted = BMIReport.getString("isDeleted");
                                bmi_model.assign_date = BMIReport.getString("assign_date");
                                bmi_model.pro_name = BMIReport.getString("pro_name");
                                bmi_model.createdDate = BMIReport.getString("createdDate");

                                proListRoute.add(bmi_model);
                            }

                            if (proListRoute.size() > 0) {
                                AdapterDesign adapterDesign = new AdapterDesign(proListRoute, ProListOfRoute.this);
                                recyclerView_design.setAdapter(adapterDesign);
                                adapterDesign.notifyDataSetChanged();

                            } else {
                                findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                                recyclerView_design.setVisibility(View.GONE);
                            }

                        } else {
                            findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                            recyclerView_design.setVisibility(View.GONE);
                        }

                    } else {
                        findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                        recyclerView_design.setVisibility(View.GONE);
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
                params.put("pro_id", user_id_info);
                params.put("date", selectedDate);
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

    ArrayAdapter arrayAdapter;
    String branch_id = "";
    String pro_id = "", date_get = "";
    Spinner spinner_pro;
    TextView date, name;
    ImageView delete;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    public void saveDate(String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String loadDate() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sp.getString("pref_date", "");
    }

    @Override
    public void onBackPressed() {
        saveDate("pref_date", "");
        super.onBackPressed();
    }

}