package com.witgoedxpert.technician.Forms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.Methods;
import com.witgoedxpert.technician.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register_A extends AppCompatActivity {

    EditText phone, password, address, name;
    Button login;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    private static final String TAG = "Register_A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        login = findViewById(R.id.btn_login);
        address = findViewById(R.id.et_address);
        name = findViewById(R.id.et_name);
        phone = findViewById(R.id.edPhone);
        password = findViewById(R.id.et_password);
        findViewById(R.id.textlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isNetworkAvailable(Register_A.this)) {
                    if (name.getText().toString().trim().equals("")) {
                        Toast.makeText(Register_A.this, "Please Enter the valid Name", Toast.LENGTH_SHORT).show();

                    } else if (phone.getText().length() != 10) {
                        Toast.makeText(Register_A.this, "Please Enter the valid Phone No.", Toast.LENGTH_SHORT).show();

                    } else if (address.getText().toString().trim().equals("")) {
                        Toast.makeText(Register_A.this, "Please Enter the valid address", Toast.LENGTH_SHORT).show();

                    } else if (password.getText().toString().trim().equals("")) {
                        Toast.makeText(Register_A.this, "Please Enter the valid Password", Toast.LENGTH_SHORT).show();

                    } else {
                        String str_phone = phone.getText().toString().trim();
                        String str_pass = password.getText().toString().trim();
                        String str_address = address.getText().toString().trim();
                        String str_name = name.getText().toString().trim();
                        getUserLogin(str_phone, str_pass, str_address, str_name);
                    }
                } else {
                    Toast.makeText(Register_A.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void getUserLogin(String str_phone, String str_pass, String str_address, String str_name) {

        final ProgressDialog progressDialog = new ProgressDialog(Register_A.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.register,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onResponse: " + response);

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            message= jsonObject.getString("message");
                            if (jsonObject.getString("code").equals("200")) {

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Register Successfully!", Toast.LENGTH_SHORT).show();

                            } else {
                                Methods.ShowMsg(Register_A.this, message);

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
                Toast.makeText(Register_A.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", str_name);
                params.put("password", str_pass);
                params.put("mobile", str_phone);
                params.put("address", str_address);
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
}