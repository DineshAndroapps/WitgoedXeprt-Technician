package com.witgoedxpert.technician.Activity;

import static com.witgoedxpert.technician.Forms.LoginActivity.EMAIL;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID_INFO;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.witgoedxpert.technician.Adapters.AdapterPlace;
import com.witgoedxpert.technician.Forms.Add_Place;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.PhotoModel;
import com.witgoedxpert.technician.model.PlaceLocationModel;
import com.witgoedxpert.technician.model.PlaceModel;
import com.witgoedxpert.technician.model.ProDetailsModel;
import com.witgoedxpert.technician.model.ProLatLngModelDatum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceLists extends AppCompatActivity {
    ArrayList<PlaceModel> designModelArrayList = new ArrayList<>();
    ArrayList<PlaceModel> designModelArrayList_copy = new ArrayList<>();
    ArrayList<PhotoModel> photoModelArrayList = new ArrayList<>();
    ArrayList<ProDetailsModel> proPlaceLatLong = new ArrayList<>();
    ArrayList<PlaceLocationModel> placeLocationModelArrayList = new ArrayList<>();
    public static ArrayList<ProLatLngModelDatum> proLatLngModelArrayList = new ArrayList<>();
    RecyclerView recyclerView_design;
    RecyclerView recyclerView_cat;

    private Toolbar toolbar;
    String user_id_info;
    String server_pro_id = "";
    String server_assign_date = "";
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_email, str_route_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_lists);
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("List Of Place");
        Intent i = getIntent();
        str_route_id = i.getStringExtra("id");
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");
        str_email = sharedPreferences.getString(EMAIL, "");
        user_id_info = sharedPreferences.getString(USER_ID_INFO, "");


        recyclerView_design = findViewById(R.id.recyclerView_place);
        recyclerView_design.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        if (Constant.isNetworkAvailable(getApplicationContext())) {
            GetData();

        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.add_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // alertDialogDemo();
                Intent intent = new Intent(getApplicationContext(), Add_Place.class);
                intent.putExtra("id", str_route_id);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnPlot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PlaceLists.this, MapActivity.class);
                intent.putExtra(Constant.DATA, designModelArrayList_copy);
                intent.putExtra(Constant.DATA_PHOTO, photoModelArrayList);
                intent.putExtra(Constant.ROUTE_ID, str_route_id);
                intent.putExtra(Constant.DATA_PLACE_LAT_LONG, placeLocationModelArrayList);
                startActivity(intent);
            }
        });
    }

    public void GetData() {
        designModelArrayList.clear();
        designModelArrayList_copy.clear();
        final ProgressDialog progressDialog = new ProgressDialog(PlaceLists.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.placeByRoute, new Response.Listener<String>() {
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
                        JSONArray jsonArrayPhoto = jsonObject.getJSONArray("photo_data");
                        JSONArray jsonArrayPro_details = jsonObject.getJSONArray("pro_details");

                        for (int i = 0; i < jsonArrayPro_details.length(); i++) {
                            ProDetailsModel placeModel = new ProDetailsModel();
                            JSONObject BMIReport = jsonArrayPro_details.getJSONObject(0);
                            server_pro_id = BMIReport.getString("pro_id");
                            server_assign_date = BMIReport.getString("assign_date");
                            ProGetDataLatLong(server_pro_id, server_assign_date);
                        }

                        for (int i = 0; i < jsonArrayPhoto.length(); i++) {
                            PhotoModel placeModel = new PhotoModel();
                            JSONObject BMIReport = jsonArrayPhoto.getJSONObject(i);

                            placeModel.id = BMIReport.getString("id");
                            placeModel.photo = BMIReport.getString("photo");
                            placeModel.latitude = BMIReport.getString("latitude");
                            placeModel.longitude = BMIReport.getString("longitude");
                            placeModel.isDeleted = BMIReport.getString("isDeleted");
                            placeModel.createdDate = BMIReport.getString("createdDate");
                            photoModelArrayList.add(placeModel);
                        }


                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("place_data");

                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            PlaceModel placeModel = new PlaceModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                            placeModel.id = BMIReport.getString("id");
                            placeModel.sequence_id = BMIReport.getString("sequence_id");
                            placeModel.photoCreatedDate = BMIReport.getString("photoCreatedDate");
                            placeModel.bde_id = BMIReport.getString("bde_id");
                            placeModel.route_id = BMIReport.getString("route_id");
                            placeModel.name = BMIReport.getString("name");
                            placeModel.latitude = BMIReport.getString("latitude");
                            placeModel.longitude = BMIReport.getString("longitude");
                            placeModel.isDeleted = BMIReport.getString("isDeleted");
                            placeModel.branchAssigned = BMIReport.getString("branchAssigned");
                            placeModel.city = BMIReport.getString("city");
                            placeModel.state = BMIReport.getString("state");
                            placeModel.contactPerson = BMIReport.getString("contactPerson");
                            placeModel.mobile = BMIReport.getString("mobile");
                            placeModel.phone = BMIReport.getString("phone");
                            placeModel.email = BMIReport.getString("email");
                            placeModel.category = BMIReport.getString("category");
                            placeModel.photo = BMIReport.optString("photo");
                            designModelArrayList.add(placeModel);
                            designModelArrayList_copy.add(placeModel);
                        }
                        recyclerView_design.setAdapter(new AdapterPlace(designModelArrayList, PlaceLists.this));


                    } else {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                }
                if (designModelArrayList_copy.size() > 0)
                    designModelArrayList_copy.add(designModelArrayList_copy.size(), designModelArrayList_copy.get(0));

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

    public void ProGetDataLatLong(String server_pro_id, String server_assign_date) {
        final ProgressDialog progressDialog = new ProgressDialog(PlaceLists.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.proLocationList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ProGetDataLatLong", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();

                        JSONArray jsonArrayvideo = jsonObject.optJSONArray("location");

                        if (jsonArrayvideo != null && jsonArrayvideo.length() > 0) {
                            placeLocationModelArrayList = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("location");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                PlaceLocationModel placeModel = new PlaceLocationModel();
                                JSONObject BMIReport = jsonArray.getJSONObject(i);

                                placeModel.id = BMIReport.getString("id");
                                placeModel.pro_id = BMIReport.getString("pro_id");
                                placeModel.latitude = BMIReport.getString("latitude");
                                placeModel.longitude = BMIReport.getString("longitude");
                                placeModel.isDeleted = BMIReport.getString("isDeleted");
                                placeLocationModelArrayList.add(placeModel);

                            }

                            //Adapter add here if needed


                        } else {
                            //   findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                        }

                    } else {
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

                progressDialog.dismiss();
                Log.d("proPlaceListRoute", "onResponse: " + placeLocationModelArrayList.size());
                if (placeLocationModelArrayList.size() > 0) {
                    placeLocationModelArrayList.add(placeLocationModelArrayList.size(), placeLocationModelArrayList.get(0));
                }

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
                params.put("pro_id", server_pro_id);
                params.put("date", server_assign_date);
                Log.d("ProGetDataLatLong", "getParams: " + params);
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

    public void onClickCalled(PlaceModel designModel, int position, String s) {
        alertDialogDemo(designModel.id, designModel.sequence_id);
    }


    void alertDialogDemo(String id, String s_id) {
        // get alert_dialog.xml view
        LayoutInflater li = LayoutInflater.from(PlaceLists.this);
        View promptsView = li.inflate(R.layout.alert_dialog_sequnace, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                PlaceLists.this);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText userInput = (EditText) promptsView.findViewById(R.id.title);
        userInput.setText(s_id);
        // set dialog message
        promptsView.findViewById(R.id.submit_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("", "onClick: " + id);
                if (Constant.isNetworkAvailable(getApplicationContext())) {
                    if (userInput.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter the Sequence", Toast.LENGTH_SHORT).show();
                    } else {
                        alertDialog.cancel();
                        SendSequence(userInput.getText().toString(), id);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
                }


            }
        });


        // show it
        alertDialog.show();
    }

    private void SendSequence(String toString, String id) {


        final ProgressDialog progressDialog = new ProgressDialog(PlaceLists.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.updateSequenceId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();

                        GetData();
                        Toast.makeText(PlaceLists.this, "Sequence Updated Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(PlaceLists.this, "" + message, Toast.LENGTH_SHORT).show();
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
                params.put("id", id);
                params.put("sequence_id", toString);
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

    public void onClickCalled_delete(PlaceModel designModel, int position, String s) {
        DialogForLogout(designModel.id, designModel.sequence_id);

    }

    private void DialogForLogout(String id, String sequence_id) {
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(PlaceLists.this);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage("Do you Really Want to Delete Place?");
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


        final ProgressDialog progressDialog = new ProgressDialog(PlaceLists.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.deletePlace, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();

                        GetData();
                        Toast.makeText(PlaceLists.this, "Place Deleted Successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(PlaceLists.this, "" + message, Toast.LENGTH_SHORT).show();
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
                params.put("place_id", id);
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