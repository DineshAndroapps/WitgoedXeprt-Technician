package com.androapp.technician.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.androapp.technician.Adapters.AdapterDesign;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.R;
import com.androapp.technician.model.BranchModel;
import com.androapp.technician.model.CategoryModel;
import com.androapp.technician.model.EmpModel;
import com.androapp.technician.model.RouteModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.androapp.technician.Forms.LoginActivity.BRANCH_DATA;
import static com.androapp.technician.Forms.LoginActivity.EMAIL;
import static com.androapp.technician.Forms.LoginActivity.NAME;
import static com.androapp.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.androapp.technician.Forms.LoginActivity.USER_ID;
import static com.androapp.technician.Forms.LoginActivity.USER_ID_INFO;

public class ListOfRoute extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);
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
        recyclerView_design = findViewById(R.id.recyclerView_design);
        recyclerView_design.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        //  recyclerView_design.setLayoutManager((new GridLayoutManager(getApplicationContext(), 2)));

        if (Constant.isNetworkAvailable(getApplicationContext())) {
            GetData();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.add_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogDemo();
            }
        });
        findViewById(R.id.arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogDemo();
            }
        });

        Log.d("str_branch_data", "onCreate: " + str_branch_data);


    }

    private void SendRoute(String string_route, String branch_id) {

        final ProgressDialog progressDialog = new ProgressDialog(ListOfRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.route, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        findViewById(R.id.route_tb).setVisibility(View.GONE);
                        designModelArrayList.clear();
                        GetData();
                        Toast.makeText(ListOfRoute.this, "Route Added Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ListOfRoute.this, ""+message, Toast.LENGTH_SHORT).show();
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
                params.put("bde_id", user_id_info);
                params.put("routeName", string_route);
                params.put("branch_id", branch_id);
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

    public void GetData() {
        designModelArrayList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(ListOfRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.routeByBde, new Response.Listener<String>() {
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
                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("route_data");
                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            RouteModel bmi_model = new RouteModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                            bmi_model.id = BMIReport.getString("id");
                            bmi_model.bde_id = BMIReport.getString("bde_id");
                            bmi_model.branch_id = BMIReport.getString("branch_id");
                            bmi_model.routeName = BMIReport.getString("routeName");
                            bmi_model.isDeleted = BMIReport.getString("isDeleted");
                            designModelArrayList.add(bmi_model);
                        }
                        recyclerView_design.setAdapter(new AdapterDesign(designModelArrayList, ListOfRoute.this));


                    } else {
                       // Toast.makeText(ListOfRoute.this, "Designer Not Available", Toast.LENGTH_SHORT).show();
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
                params.put("bde_id", user_id_info);
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

    void alertDialogDemo() {
        // get alert_dialog.xml view
        LayoutInflater li = LayoutInflater.from(ListOfRoute.this);
        View promptsView = li.inflate(R.layout.alert_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ListOfRoute.this);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText userInput = (EditText) promptsView.findViewById(R.id.title);
        final Spinner spinner = promptsView.findViewById(R.id.et_branch_assign);
        branch_list_str = new ArrayList<>();
        branch_list_str.add(SELECT_SUBJECT);

        final BranchModel categoryOrProductModel = new BranchModel();
        categoryOrProductModel.setId("0");
        categoryOrProductModel.setBranchName(SELECT_SUBJECT);
        branchModelArrayList.add(categoryOrProductModel);

        arrayAdapter = new ArrayAdapter(ListOfRoute.this, android.R.layout.simple_spinner_dropdown_item, branch_list_str);
        spinner.setAdapter(arrayAdapter);

        try {
            JSONArray jsonArray = new JSONArray(str_branch_data);

            for (int j = 0; j < jsonArray.length(); j++) {
                BranchModel bmi_model = new BranchModel();
                JSONObject BMIReport = jsonArray.getJSONObject(j);

                Log.d("check_", "onCreate: " + BMIReport.getString("branch_name"));
                bmi_model.setId(BMIReport.getString("id"));
                bmi_model.setBranchName(BMIReport.getString("branch_name"));

                branchModelArrayList.add(bmi_model);
                branch_list_str.add(BMIReport.getString("branch_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arrayAdapter = new ArrayAdapter(ListOfRoute.this, android.R.layout.simple_spinner_dropdown_item, branch_list_str);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                branch_id = branchModelArrayList.get(i).getId();
                Log.d("mother_tongue", "onItemSelected: " + branch_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // set dialog message
        promptsView.findViewById(R.id.submit_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("", "onClick: " + branch_id);
                if (Constant.isNetworkAvailable(getApplicationContext())) {
                    if (userInput.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter the Route", Toast.LENGTH_SHORT).show();
                    } else if ((branch_id.equals("") || branch_id.equals(SELECT_SUBJECT)) || branch_id.equals("0")) {
                        Toast.makeText(getApplicationContext(), "Select the Branch", Toast.LENGTH_SHORT).show();
                    } else {
                        alertDialog.cancel();
                        SendRoute(userInput.getText().toString(), branch_id);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
                }


            }
        });


        // show it
        alertDialog.show();
    }

    String pro_id = "", date_get = "",pro_name="";
    Spinner spinner_pro;
    TextView date, name;
    ImageView delete;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    public void onClickCalled(RouteModel designModel, int position, String s) {


        // get alert_dialog.xml view
        LayoutInflater li = LayoutInflater.from(ListOfRoute.this);
        View promptsView = li.inflate(R.layout.alert_dialog_assign, null);
        alertDialogBuilder = new AlertDialog.Builder(
                ListOfRoute.this);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialog = alertDialogBuilder.create();

        final EditText et_date = (EditText) promptsView.findViewById(R.id.et_date);
        final ImageView date_picker_action = promptsView.findViewById(R.id.date_picker_actions);
        spinner_pro = promptsView.findViewById(R.id.et_pro_assign);
        date = promptsView.findViewById(R.id.date);
        delete = promptsView.findViewById(R.id.delete);
        name = promptsView.findViewById(R.id.name);


        date_picker_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ListOfRoute.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        et_date.setText(i2 + "-" + (i1 + 1) + "-" + i);
                        date_get = i + "-" + (i1 + 1) + "-" + i2;
                        Date date = null;

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1);
                datePickerDialog.show();

            }
        });

        pro_list_str = new ArrayList<>();
        pro_list_str.add(SELECT_PRO);

        final EmpModel categoryOrProductModel = new EmpModel();
        categoryOrProductModel.id = "0";
        categoryOrProductModel.first_name = SELECT_PRO;
        proModelArrayList.add(categoryOrProductModel);

        arrayAdapter = new ArrayAdapter(ListOfRoute.this, android.R.layout.simple_spinner_dropdown_item, pro_list_str);
        spinner_pro.setAdapter(arrayAdapter);
        GetData_PRO(designModel.branch_id);
        GetData_AssignPRO(designModel.id);


        spinner_pro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pro_id = proModelArrayList.get(i).id;
                pro_name = proModelArrayList.get(i).username;
                Log.d("mother_tongue_pro_id", "onItemSelected: " + pro_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        // set dialog message
        promptsView.findViewById(R.id.submit_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("", "onClick: " + pro_id);
                if (Constant.isNetworkAvailable(getApplicationContext())) {
                    if (et_date.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Select Date", Toast.LENGTH_SHORT).show();
                    } else if ((pro_id.equals("") || pro_id.equals(SELECT_PRO)) || pro_id.equals("0")) {
                        Toast.makeText(getApplicationContext(), "Select the PRO", Toast.LENGTH_SHORT).show();
                    } else {
                        alertDialog.cancel();
                        AssignRoute(date_get, pro_id, designModel.id,pro_name);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
                }


            }
        });


        // show it
        alertDialog.show();

    }

    private void DialogForDelete(String finalAssign_id) {
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(ListOfRoute.this);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage(R.string.logout_confirmation_text_del);
        dialog.setNegativeButton(R.string.CANCEL, null);
        dialog.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteAssignPRO(finalAssign_id);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void DeleteAssignPRO(String finalAssign_id) {

        final ProgressDialog progressDialog = new ProgressDialog(ListOfRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.deleteAssignRoute, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        alertDialog.cancel();
                        GetData();
                        Toast.makeText(ListOfRoute.this, "Assigned PRO Deleted Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ListOfRoute.this, ""+message, Toast.LENGTH_SHORT).show();
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
                params.put("assign_id", finalAssign_id);

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

    private void GetData_AssignPRO(String id) {

        final ProgressDialog progressDialog = new ProgressDialog(ListOfRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.checkAssignRoute, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    String finalAssign_id="";
                    if (code.equals("200")) {
                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("assign_route");
                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            RouteModel bmi_model = new RouteModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                            finalAssign_id= BMIReport.getString("id");
                            bmi_model.id = BMIReport.getString("id");
                            name.setText(BMIReport.getString("pro_name"));
                            date.setText(BMIReport.getString("assign_date"));


                        }
                        String finalAssign_id1 = finalAssign_id;
                        delete.setOnClickListener(v -> {
                            DialogForDelete(finalAssign_id1);
                        });
                    } else {
                        Toast.makeText(ListOfRoute.this, message, Toast.LENGTH_SHORT).show();
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
                params.put("route_id", id);
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

    //Assign Route to PRO by BDM
    private void AssignRoute(String date_get, String pro_id, String route_id, String pro_name) {

        final ProgressDialog progressDialog = new ProgressDialog(ListOfRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.assignRoute, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        GetData();
                        Toast.makeText(ListOfRoute.this, "Route Assign Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ListOfRoute.this, ""+message, Toast.LENGTH_SHORT).show();
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
                params.put("pro_id", pro_id);
                params.put("route_id", route_id);
                params.put("assign_date", date_get);
                params.put("pro_name", pro_name);
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

    public void GetData_PRO(String str_branch_id) {
        final ProgressDialog progressDialog = new ProgressDialog(ListOfRoute.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        String url = Constant.branchwisepro + str_branch_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("GetData_PRO", "onResponse: " + response);
                try {
                    progressDialog.dismiss();
                    JSONArray jsonArrayvideo = new JSONArray(response);

                    if (jsonArrayvideo != null) {
                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            EmpModel bmi_model = new EmpModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);
                            bmi_model.id = BMIReport.getString("id");
                            bmi_model.username = BMIReport.getString("username");
                            bmi_model.first_name = BMIReport.getString("first_name");
                            bmi_model.email = BMIReport.getString("email");

                            JSONObject employee = BMIReport.getJSONObject("employee");

                            bmi_model.emp_id = employee.getString("id");
                            bmi_model.name = employee.getString("name");
                            bmi_model.erp = employee.getString("erp");

                            proModelArrayList.add(bmi_model);


                            pro_list_str.add(BMIReport.getString("first_name"));

                        }

                        arrayAdapter = new ArrayAdapter(ListOfRoute.this, android.R.layout.simple_spinner_dropdown_item, pro_list_str);
                        spinner_pro.setAdapter(arrayAdapter);

                    } else {
                        Log.d("null", "onResponse: ");
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
                //  params.put("user_id", str_userid);
                Log.d("params", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "bearer " + sharedPreferences.getString(SERVER_TOKEN, ""));
                Log.d("params", "getParams: " + header);
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