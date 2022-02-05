package com.witgoedxpert.technician.Forms;

import static com.witgoedxpert.technician.Forms.Functions.getStringFromEdit;
import static com.witgoedxpert.technician.Forms.LoginActivity.ADDRESS;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.DrawingViewUtils;
import com.witgoedxpert.technician.Helper.Methods;
import com.witgoedxpert.technician.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class
AddEnquiry extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String str_userid, str_user_name, str_user_address, str_name, str_name_pro, str_customer_id = "", str_product_id = "", str_main_id, date_get, userGender = "0", str_sign = "";
    private RadioGroup radioGenderGroup;
    private RadioButton radio_F, radio_M;
    DrawingViewUtils mDrawingViewUtils;
    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke;
    ImageView img_sign;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enquairy);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Billing Details");
        findViewById(R.id.bt_menu).setOnClickListener(view -> onBackPressed());

        str_name_pro = getIntent().getStringExtra("str_name_pro");
        str_product_id = getIntent().getStringExtra("str_product_id");
        str_customer_id = getIntent().getStringExtra("user_id");
        str_main_id = getIntent().getStringExtra("main_id");
        str_user_name = getIntent().getStringExtra("user_name");
        str_user_address = getIntent().getStringExtra("user_address");

        GetCurrent_Date();

        img_sign = findViewById(R.id.img_sign);
        radioGenderGroup = findViewById(R.id.radioGrp);
        radio_F = findViewById(R.id.radioF);
        radio_M = findViewById(R.id.radioM);
        ((TextView) findViewById(R.id.toolbr_lbl)).setText(str_name_pro);
        textInputLayout(R.id.et_name).setText(str_user_name);
        textInputLayout(R.id.et_address).setText(str_user_address);


        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.img_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogClass cdd = new CustomDialogClass(AddEnquiry.this);
                cdd.show();
            }
        });
        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            //str_sign = Constant.Convert_Bitmap_to_base64(mDrawingViewUtils.getBitmap());
            Log.e("str_sign", "onClick: " + str_sign);
            if (!textInputLayout(R.id.et_name).getText().toString().equals("")) {
                if (!textInputLayout(R.id.et_address).getText().toString().equals("")) {
                    if (!textInputLayout(R.id.et_cost).getText().toString().equals("")) {
                        if (!textInputLayout(R.id.et_time).getText().toString().equals("")) {
                            if (!textInputLayout(R.id.et_parts).getText().toString().equals("")) {
                                if (!textInputLayout(R.id.et_total).getText().equals("")) {
                                    if (!textInputLayout(R.id.et_email).getText().equals("")) {
                                        if (!str_sign.equals("")) {
                                            if (Constant.isNetworkAvailable(getApplicationContext())) {
                                                SubmitData();
                                            } else {
                                                Methods.ShowMsg(AddEnquiry.this, getString(R.string.check_net));
                                            }

                                        } else {
                                            Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_name));
                                        }

                                    } else {
                                        Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_address));
                                    }

                                } else {
                                    Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_invalid_email));
                                }
                            } else {
                                Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_cost));
                            }

                        } else {
                            Methods.ShowMsg(AddEnquiry.this, "Enter the valid time");
                        }

                    } else {
                        Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_part));
                    }


                } else {
                    Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_total));
                }
            } else {
                Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_sing));
            }

        });

        radioGenderGroup.setOnCheckedChangeListener((group, checkedId) -> {
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
        });
    }

    private void initDrawingView() {
        mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
        mCurrentColor = ContextCompat.getColor(this, android.R.color.black);
        mCurrentStroke = 10;

        mDrawingViewUtils.setBackgroundColor(mCurrentBackgroundColor);
        mDrawingViewUtils.setPaintColor(mCurrentColor);
        mDrawingViewUtils.setPaintStrokeWidth(mCurrentStroke);
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

    private void SubmitData() {
        final ProgressDialog progressDialog = new ProgressDialog(AddEnquiry.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.addEnquiry,
                response -> {
                    Log.e("jsonObject_", "onResponse: " + response);

                    String code, message, id, user_id;

                    try {
                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.getString("code").equals("200")) {


                            finish();
                            Toast.makeText(getApplicationContext(), "Details Submitted Successfully!", Toast.LENGTH_SHORT).show();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialog.dismiss();

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(AddEnquiry.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("email", getStringFromEdit(textInputLayout(R.id.et_email)));
                params.put("signature", str_sign);
                params.put("date", date_get);
                params.put("assigned", "6");
                params.put("enquiry_id", str_main_id);
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


        Volley.newRequestQueue(this).add(stringRequest);

    }

    private EditText textInputLayout(int id) {

        return ((EditText) findViewById(id));
    }

    public class CustomDialogClass extends Dialog implements
            android.view.View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;

        public CustomDialogClass(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_new);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            mDrawingViewUtils = findViewById(R.id.main_drawing_view);
            initDrawingView();
            yes.setOnClickListener(this);
            no.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes:
                    str_sign = Constant.Convert_Bitmap_to_base64(mDrawingViewUtils.getBitmap());
                    Log.e("str_sign", "onClick: " + str_sign);
                    Glide.with(c)
                            .asBitmap()
                            .load(mDrawingViewUtils.getBitmap())
                            .into(img_sign);

                    break;
                case R.id.btn_no:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }
}