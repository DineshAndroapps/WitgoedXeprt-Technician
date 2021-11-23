package com.androapp.technician.Forms;

import static com.androapp.technician.Forms.Functions.Bind_Location_service;
import static com.androapp.technician.Forms.LoginActivity.BRANCH_DATA;
import static com.androapp.technician.Forms.LoginActivity.CATEGORY_DATA;
import static com.androapp.technician.Forms.LoginActivity.EMAIL;
import static com.androapp.technician.Forms.LoginActivity.NAME;
import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.androapp.technician.Forms.LoginActivity.USER_ID;
import static com.androapp.technician.Forms.LoginActivity.USER_ID_INFO;
import static com.androapp.technician.Helper.Variables.REQUESTCODE_MAP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androapp.technician.Activity.PlaceLists;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.Location_Services.GpsUtils;
import com.androapp.technician.Location_Services.PlacesSearch.GooglePlaces_A;
import com.androapp.technician.R;
import com.androapp.technician.model.BranchModel;
import com.androapp.technician.model.CategoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Add_Place extends AppCompatActivity {
    private Toolbar toolbar;
    String user_id_info, str_branch_data, str_category_data;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_email, str_route_id;
    EditText et_add_category, et_email, et_address, et_city, et_state, et_cont_1, et_cont_2, et_name, et_contact_person, edt_location;
    Button login;
    TextView register;
    String str_bde = "", str_longitude = "", str_latitude = "", str_contactPerson = "", str_mobile = "", str_phone = "", str_category = "";
    private static final String TAG = "Add_Place";
    ArrayAdapter arrayAdapter;
    ArrayAdapter arrayAdapter_cate;
    String branch_id = "";
    ArrayList<String> branch_list_str = new ArrayList<>();
    ArrayList<String> category_list_str = new ArrayList<>();

    private String SELECT_SUBJECT = "Select Branch";
    private String SELECT_PRO = "Select PRO";
    private String SELECT_CATEGORY = "Select Category";
    ArrayList<BranchModel> branchModelArrayList = new ArrayList<>();
    ArrayList<CategoryModel> categoryModelArrayList = new ArrayList<>();
    Spinner et_category;
    String str_add_category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Add Place");

        Intent i = getIntent();
        str_route_id = i.getStringExtra("id");
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");
        str_email = sharedPreferences.getString(EMAIL, "");
        user_id_info = sharedPreferences.getString(USER_ID_INFO, "");
        str_branch_data = sharedPreferences.getString(BRANCH_DATA, "");
        str_category_data = sharedPreferences.getString(CATEGORY_DATA, "");

        login = findViewById(R.id.btn_login);
        et_email = findViewById(R.id.et_email);
        et_add_category = findViewById(R.id.et_add_category);
        et_address = findViewById(R.id.et_address);
        et_city = findViewById(R.id.et_city);
        et_state = findViewById(R.id.et_state);
        et_cont_1 = findViewById(R.id.et_contact_one);
        et_cont_2 = findViewById(R.id.et_contact_two);
        et_name = findViewById(R.id.et_name);
        et_category = findViewById(R.id.et_category);
        et_contact_person = findViewById(R.id.et_contact_person);
        edt_location = findViewById(R.id.edt_location);


        category_list_str = new ArrayList<>();
        category_list_str.add(SELECT_CATEGORY);

        final CategoryModel categoryOModel = new CategoryModel();
        categoryOModel.setId("0");
        categoryOModel.setName(SELECT_CATEGORY);
        categoryModelArrayList.add(categoryOModel);

        arrayAdapter_cate = new ArrayAdapter(Add_Place.this, android.R.layout.simple_spinner_dropdown_item, category_list_str);
        et_category.setAdapter(arrayAdapter_cate);

        GetDataCategory();

        et_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                str_category = categoryModelArrayList.get(position).getName();
                Log.e("str_category", "onItemSelected: " + str_category);


                if (str_category.trim().equals(SELECT_CATEGORY)) {
                    findViewById(R.id.lyt_add_cate).setVisibility(View.GONE);


                } else {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    if (selectedItem.equals("Other")) {

                        findViewById(R.id.lyt_add_cate).setVisibility(View.VISIBLE);

                    } else {
                        findViewById(R.id.lyt_add_cate).setVisibility(View.GONE);


                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        /*--------------------------------------------------------------------------------------------*/
        final Spinner spinner = findViewById(R.id.et_branch_assign);
        branch_list_str = new ArrayList<>();
        branch_list_str.add(SELECT_SUBJECT);

        final BranchModel categoryOrProductModel = new BranchModel();
        categoryOrProductModel.setId("0");
        categoryOrProductModel.setBranchName(SELECT_SUBJECT);
        branchModelArrayList.add(categoryOrProductModel);

        arrayAdapter = new ArrayAdapter(Add_Place.this, android.R.layout.simple_spinner_dropdown_item, branch_list_str);
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
        arrayAdapter = new ArrayAdapter(Add_Place.this, android.R.layout.simple_spinner_dropdown_item, branch_list_str);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                branch_id = branchModelArrayList.get(i).getBranchName();
                Log.d("mother_tongue", "onItemSelected: " + branch_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_mobile = et_cont_1.getText().toString().trim();
                str_phone = et_cont_2.getText().toString().trim();
                // str_category = et_category.getText().toString().trim();
                str_email = et_email.getText().toString().trim();
                str_contactPerson = et_contact_person.getText().toString().trim();
                if (Constant.isNetworkAvailable(Add_Place.this)) {
                    if (edt_location.getText().toString().trim().equals("")) {
                        Toast.makeText(Add_Place.this, "Please Enter the valid Name", Toast.LENGTH_SHORT).show();

                    } if (str_mobile.length() < 10) {
                        Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();

                    } else if (str_phone.length() < 10) {
                        Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();

                    } else if (str_email.matches("")) {
                        Toast.makeText(getApplicationContext(), "Please enter valid Email", Toast.LENGTH_SHORT).show();

                    }else if (et_address.getText().toString().trim().equals("")) {
                        Toast.makeText(Add_Place.this, "Please Enter the valid Address", Toast.LENGTH_SHORT).show();

                    } else if (et_city.getText().toString().trim().equals("")) {
                        Toast.makeText(Add_Place.this, "Please Enter the valid city", Toast.LENGTH_SHORT).show();

                    } else if (et_state.getText().toString().trim().equals("")) {
                        Toast.makeText(Add_Place.this, "Please Enter the valid state", Toast.LENGTH_SHORT).show();

                    }  else {
                        String str_name = et_name.getText().toString().trim();
                        String str_location = edt_location.getText().toString().trim();
                        String str_address = et_address.getText().toString().trim();
                        String str_city = et_city.getText().toString().trim();
                        String str_state = et_state.getText().toString().trim();


                        if ((branch_id.equals("") || branch_id.equals(SELECT_SUBJECT)) || branch_id.equals("0")) {
                            Toast.makeText(Add_Place.this, "Please Select Branch", Toast.LENGTH_SHORT).show();

                        } else if (str_category.toString().trim().equals("") || str_category.toString().trim().equals(SELECT_CATEGORY)) {
                            Toast.makeText(Add_Place.this, "Please Enter the category", Toast.LENGTH_SHORT).show();

                        } else if (str_category.equals("Other")) {
                            str_add_category = et_add_category.getText().toString().trim();
                            if (str_add_category.equals("") || str_add_category.equals("null")) {
                                Toast.makeText(Add_Place.this, "Enter the valid Category Name ", Toast.LENGTH_SHORT).show();
                            } else {
                                str_add_category = et_add_category.getText().toString().trim();
                                SendPlaceData(str_name, str_location, str_address, str_city, str_state, branch_id);

                            }
                        } else {
                            SendPlaceData(str_name, str_location, str_address, str_city, str_state, branch_id);

                        }
                    }
                } else {
                    Toast.makeText(Add_Place.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.lnr_locate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EnableGPS();

            }
        });
    }

    public void GetDataCategory() {
        categoryModelArrayList.clear();
        CategoryModel examSpinnerModel = new CategoryModel();
        examSpinnerModel.id = "";
        examSpinnerModel.name = SELECT_CATEGORY;
        categoryModelArrayList.add(examSpinnerModel);

        final ProgressDialog progressDialog = new ProgressDialog(Add_Place.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.allCategory, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();

                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            CategoryModel placeModel = new CategoryModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);
                            placeModel.setId(BMIReport.getString("id"));
                            placeModel.setName(BMIReport.getString("category_name"));
                            categoryModelArrayList.add(placeModel);

                            category_list_str.add(BMIReport.getString("category_name"));
                            arrayAdapter_cate = new ArrayAdapter(Add_Place.this, android.R.layout.simple_spinner_dropdown_item, category_list_str);
                            et_category.setAdapter(arrayAdapter_cate);
                        }


                    } else {
                        Toast.makeText(Add_Place.this, "Category Not Available", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
                arrayAdapter_cate.notifyDataSetChanged();
                CategoryModel placeModel = new CategoryModel();
                placeModel.id = "0";
                placeModel.name = "Other";
                category_list_str.add("Other");
                categoryModelArrayList.add(placeModel);
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

    private void enable_permission() {


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {

            new GpsUtils(Add_Place.this).turnGPSOn(isGPSEnable -> {

                enable_permission();

            });
        } else if (Functions.check_location_permissions(Add_Place.this)) {
            Bind_Location_service(Add_Place.this);

            startActivityForResult(new Intent(Add_Place.this, GooglePlaces_A.class), REQUESTCODE_MAP);
        }
    }

    private void SendPlaceData(String str_name, String str_location, String str_address, String str_city, String str_state, String str_branch_assign) {


        final ProgressDialog progressDialog = new ProgressDialog(Add_Place.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.place,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", "onResponse: " + response);
                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            code = jsonObject.getString("code");
                            message = jsonObject.getString("message");

                            Log.d("resp", "onResponse: " + jsonObject + "===" + code);
                            if (code.equals("200")) {
                                Toast.makeText(Add_Place.this, "Places Added Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), PlaceLists.class);
                                intent.putExtra("id", str_route_id);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);

                            } else {
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", str_location);
                map.put("branchAssigned", str_branch_assign);
                map.put("city", str_city);
                map.put("address", str_address);
                map.put("state", str_state);
                map.put("route_id", str_route_id);
                map.put("bde_id", user_id_info);
                map.put("latitude", str_latitude);
                map.put("longitude", str_longitude);
                map.put("contactPerson", str_contactPerson);
                map.put("mobile", str_mobile);
                map.put("phone", str_phone);
                map.put("email", str_email);
                map.put("category", str_category);
                map.put("category_name", str_add_category);
                Log.d("params", "getParams: " + map);
                return map;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    public void EnableGPS() {

        if (Functions.check_location_permissions(Add_Place.this)) {
            if (!GpsUtils.isGPSENABLE(Add_Place.this)) {
                // Toast.makeText(getApplicationContext(), "Please Enable GPS", Toast.LENGTH_SHORT).show();
                Intent gpsOptionsIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            } else {
                enable_permission();
            }
        } else {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
            Toast.makeText(getApplicationContext(), "Please Enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUESTCODE_MAP) {
                Double lat = data.getDoubleExtra("latitude", 0);
                Double longe = data.getDoubleExtra("longitude", 0);
                Address address = Functions.getAddressFromLatLong(lat, longe, Add_Place.this);
                setDataonLocation(address);
                str_latitude = String.valueOf(lat);
                str_longitude = String.valueOf(longe);
                Log.d(TAG, "onActivityResult: " + address);
                Log.d(TAG, "onActivityResult: " + longe);
                Log.d(TAG, "onActivityResult: " + lat);
            }

        }

        switch (requestCode) {
            case 3: {
                if (resultCode == RESULT_OK) {

                    Functions.Bind_Location_service(Add_Place.this);
                }
            }
            break;
        }

    }

    private void setDataonLocation(Address address) {

        if (address != null) {
            Functions.LOGE("EditProfile", "Address  : " + address.toString());
            String location_address = Functions.getLocationFromGeoAddress(address);

            edt_location.setText("" + location_address);

            String city = address.getSubAdminArea();
            if (Functions.checkStringisValid(city)) {
                et_city.setText("" + city);
            }


            String pin_code = address.getPostalCode();
            if (Functions.checkStringisValid(pin_code)) {
                // edt_pincode.setText("" + pin_code);
            }

            String locality = address.getFeatureName();
            if (Functions.checkStringisValid(locality)) {
                // et.setText("" + locality);
            }

            String state = address.getAdminArea();
            if (Functions.checkStringisValid(state)) {
                et_state.setText("" + state);
            }

            String country = address.getCountryName();
            if (Functions.checkStringisValid(country)) {
                //  edt_country.setText("" + country);
            }

        }

    }

    /*

     *
     * */
}
