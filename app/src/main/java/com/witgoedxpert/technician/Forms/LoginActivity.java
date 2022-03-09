package com.witgoedxpert.technician.Forms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.witgoedxpert.technician.Activity.Home.MainActivity;
import com.witgoedxpert.technician.Activity.Home.SchedulePage_A;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCES_NAME = "login_portal";
    public static final String USER_ID = "user_id";
    public static final String USER = "user";
    public static final String EMAIL = "email";
    public static final String ERP = "erp";
    public static final String PHONENO = "phoneno";
    public static final String PROFILE_PIC = "pic";
    public static final String NAME = "name";
    public static final String ADDRESS = "location";
    public static final String STATUS = "status";
    public static final String SERVER_TOKEN = "server_token";
    public static final String ROLE = "role";
    public static final String USER_ID_INFO = "user_id_info";
    public static final String BRANCH_DATA = "branch_array";
    public static final String CATEGORY_DATA = "category_array";
    public static final String USER_LAT = "user_lat";
    public static final String USER_LONG = "user_long";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText email, password;
    Button login;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.btn_login);
        email = findViewById(R.id.edPhone);
        password = findViewById(R.id.et_password);
        findViewById(R.id.text_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register_A.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.forgot_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_showMessage();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isNetworkAvailable(LoginActivity.this)) {
                    if (email.getText().toString().trim().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please Enter the valid Phone No", Toast.LENGTH_SHORT).show();

                    } else if (password.getText().toString().trim().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please Enter the valid Password", Toast.LENGTH_SHORT).show();

                    } else {
                        String str_email = email.getText().toString().trim();
                        String str_pass = password.getText().toString().trim();
                        getUserLogin(str_email, str_pass);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    public void btn_showMessage(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog_email,null);
        final EditText txt_inputText = (EditText)mView.findViewById(R.id.txt_input);
        Button btn_cancel = (Button)mView.findViewById(R.id.btn_cancel);
        Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                SubmitEmail(txt_inputText.getText().toString());
            }
        });
        alertDialog.show();
    }

    private void SubmitEmail(String toString) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.forgotpassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onResponse: " + response);
                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            message = jsonObject.getString("message");

                            if (jsonObject.getString("code").equals("200")) {


                                Toast.makeText(getApplicationContext(), "Password is Sent on Email Successfully!", Toast.LENGTH_SHORT).show();

                            } else if (jsonObject.getString("code").equals("403")) {
                                Toast.makeText(LoginActivity.this, "Email not registered", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", toString);
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


        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void getUserLogin(String str_email, String str_pass) {

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onResponse: " + response);
                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            message = jsonObject.getString("message");

                            if (jsonObject.getString("code").equals("200")) {
                                JSONObject user_data = jsonObject.getJSONObject("Mechanic_data");

                                sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString(USER, user_data.getString("id"));
                                editor.putString(USER_ID, user_data.getString("id"));
                                editor.putString(NAME, user_data.getString("name"));
                                editor.putString(PHONENO, user_data.getString("mobile"));
                                editor.putString(ADDRESS, user_data.getString("address"));
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), SchedulePage_A.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_SHORT).show();

                            } else if (jsonObject.getString("code").equals("403")) {
                                Toast.makeText(LoginActivity.this, "Phone No not registered", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("password", str_pass);
                params.put("mobile", str_email);
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


        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void SetDataToSherf(String str_id, String name, String user, String erp, String location, String token, String role, JSONArray branch_array, String user_id_info) {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(USER_ID, str_id);
        editor.putString(USER, user);
        editor.putString(ERP, erp);
        editor.putString(NAME, name);
        editor.putString(ADDRESS, location);
        editor.putString(SERVER_TOKEN, token);
        editor.putString(ROLE, role);
        editor.putString(USER_ID_INFO, user_id_info);
        editor.putString(BRANCH_DATA, branch_array.toString());
        editor.apply();

    }


}