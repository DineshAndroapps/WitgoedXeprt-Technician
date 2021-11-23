package com.androapp.technician.Activity.PRO;

import static com.androapp.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.androapp.technician.Adapters.AdapterFormList;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.R;
import com.androapp.technician.model.FormListModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProFormListActivity extends Activity {
    private RecyclerView mRecyclerView;
    private TextView tvNoFormList, tvBackFormList;
    private String TAG = ProFormListActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);

        Init();
        setData();
    }

    private void setData() {
        tvBackFormList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(SERVER_TOKEN, "");

//        str_name = sharedPreferences.getString(NAME, "");
//        str_email = sharedPreferences.getString(EMAIL, "");
//        user_id_info = sharedPreferences.getString(USER_ID_INFO, "");
//        str_branch_data = sharedPreferences.getString(BRANCH_DATA, "");

        if (Constant.isNetworkAvailable(getApplicationContext())) {
            ProGetData();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    private void Init() {
        mRecyclerView = findViewById(R.id.form_list_recycler_view);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        tvNoFormList = findViewById(R.id.tv_no_form_list);
        tvBackFormList = findViewById(R.id.tv_back_form_list);
    }

    public void ProGetData() {
        final ProgressDialog progressDialog = new ProgressDialog(ProFormListActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.formList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("form_list_pro", "onResponse: " + response);
                try {

//                    Gson gson = new Gson();
//                    Reader reader = new StringReader(response);
//                    FormListModel formListModel = gson.fromJson(reader, FormListModel.class);

                    Type listType = new TypeToken<ArrayList<FormListModel>>() {}.getType();
                    final ArrayList<FormListModel> formListModel = new Gson().fromJson(response, listType);

                    if (formListModel.size() > 0) {
                        AdapterFormList adapterFormList = new AdapterFormList(formListModel, ProFormListActivity.this);
                        mRecyclerView.setAdapter(adapterFormList);
                        adapterFormList.notifyDataSetChanged();

                    } else {
                        Toast.makeText(ProFormListActivity.this, "Data Not Found!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + accessToken);
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

