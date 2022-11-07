package com.witgoedxpert.technician.Forms;

import static com.witgoedxpert.technician.Forms.Functions.getStringFromEdit;
import static com.witgoedxpert.technician.Forms.LoginActivity.ADDRESS;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.util.IslamicCalendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.witgoedxpert.technician.Activity.Home.SchedulePage_A;
import com.witgoedxpert.technician.Adapters.AdapterSlot;
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
import com.witgoedxpert.technician.model.SlotModel;

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

public class
AddEnquiry extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String str_userid, str_user_name, str_user_address, date_get_next, str_name, str_name_pro, str_customer_id = "", str_product_id = "", str_main_id, date_get, userGender = "", str_sign = "";
    private RadioGroup radioGenderGroup;
    private RadioButton radio_F, radio_M;
    DrawingViewUtils mDrawingViewUtils;
    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke;
    ImageView img_sign;

    TextView title, date_show;
    ImageView image, date_picker_actions;
    //  DesignModel designModel;
    RecyclerView recyclerView_slot;
    TextView no_slot;
    ArrayList<SlotModel> slotModelArrayList = new ArrayList<>();
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd'th' MMMM yy ");
    SimpleDateFormat fmtOut_ = new SimpleDateFormat("EEEE ");
    String booking_id = "";
    public static int checkPosition = -1;
    AdapterSlot adapterSlot;
    String str_mec_id, str_timer = "", str_selected_slot_date = "", str_selected_slot_id = "";

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

        textInputLayout(R.id.et_total).setEnabled(false);
        str_name_pro = getIntent().getStringExtra("str_name_pro");
        str_product_id = getIntent().getStringExtra("str_product_id");
        str_customer_id = getIntent().getStringExtra("user_id");
        str_main_id = getIntent().getStringExtra("main_id");
        str_user_name = getIntent().getStringExtra("user_name");
        str_user_address = getIntent().getStringExtra("user_address");
        str_timer = getIntent().getStringExtra("timer");
        SetDataForNextBooking();
        GetCurrent_Date();
        CalculationTotal();

        img_sign = findViewById(R.id.img_sign);
        radioGenderGroup = findViewById(R.id.radioGrp);
        radio_F = findViewById(R.id.radioF);
        radio_M = findViewById(R.id.radioM);
        ((TextView) findViewById(R.id.toolbr_lbl)).setText(str_name_pro);
        textInputLayout(R.id.et_name).setText(str_user_name);
        textInputLayout(R.id.et_address).setText(str_user_address);
        Log.e("str_timer", "onCreate: " + str_timer);
        textInputLayout(R.id.et_time).setText(str_timer);


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
                    if (!textInputLayout(R.id.et_time).getText().toString().equals("")) {

                        if (!textInputLayout(R.id.et_parts).getText().toString().equals("")) {
                            if (!textInputLayout(R.id.et_parts_amt).getText().toString().equals("")) {
                                if (!textInputLayout(R.id.et_cost).getText().toString().equals("")) {
                                    if (!textInputLayout(R.id.et_total).getText().toString().equals("")) {
                                        if (!textInputLayout(R.id.et_email).getText().toString().equals("")) {
                                            if (!str_sign.equals("")) {
                                                if (!userGender.equals("")) {
                                                    if (Constant.isNetworkAvailable(getApplicationContext())) {
                                                        SubmitData();
                                                    } else {
                                                        Methods.ShowMsg(AddEnquiry.this, getString(R.string.check_net));
                                                    }
                                                } else {
                                                    Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_app));
                                                }
                                            } else {
                                                Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_sing));
                                            }
                                        } else {
                                            Methods.ShowMsg(AddEnquiry.this, getString(R.string.email_error));
                                        }
                                    } else {
                                        Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_total));
                                    }
                                } else {
                                    Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_cost));
                                }
                            } else {
                                Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_part_amt));
                            }
                        } else {
                            Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_part));
                        }

                    } else {
                        Methods.ShowMsg(AddEnquiry.this, "Enter the valid time");
                    }


                } else {
                    Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_address));
                }
            } else {
                Methods.ShowMsg(AddEnquiry.this, getString(R.string.error_name));
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
                        findViewById(R.id.next_task_lnr).setVisibility(View.VISIBLE);
                    } else {
                        //  checkPosition = -1;
                        str_selected_slot_date = "";
                        str_selected_slot_id = "";
                        userGender = "0";
                        findViewById(R.id.next_task_lnr).setVisibility(View.GONE);
                    }

                }

            }

            Log.e("Gender", userGender);
        });
    }

    private void CalculationTotal() {
        textInputLayout(R.id.et_cost).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate();

            }
        });
        textInputLayout(R.id.et_parts_amt).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculate();

            }
        });

    }

    public void calculate() {
        String epText = textInputLayout(R.id.et_cost).getText().toString().trim();
        String stopText = textInputLayout(R.id.et_parts_amt).getText().toString().trim();

        if (epText.isEmpty() || stopText.isEmpty()) {
            textInputLayout(R.id.et_total).setText("");
            return;
        }


        double ep = Double.parseDouble(epText);
        double stop = Double.parseDouble(stopText);

        double result = ep + stop;
        textInputLayout(R.id.et_total).setText(String.valueOf(result));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetDataForNextBooking() {
        checkPosition = -1;
        recyclerView_slot = findViewById(R.id.recyclerView_slot);
        date_picker_actions = findViewById(R.id.date_picker_actions);
        date_show = findViewById(R.id.date_show);
        no_slot = findViewById(R.id.no_slot);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        recyclerView_slot.setLayoutManager(gridLayoutManager);
        adapterSlot = new AdapterSlot(slotModelArrayList, this);
        recyclerView_slot.setAdapter(adapterSlot);

        if (Constant.isNetworkAvailable(getApplicationContext())) {
            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year = localDate.getYear();
            int month = localDate.getMonthValue();
            int day = localDate.getDayOfMonth();
            date_get_next = year + "-" + month + "-" + day;
            Date date_ = null;
            try {
                date_ = fmt.parse(date_get_next);
                date_show.setText(fmtOut.format(date));
                ((TextView) findViewById(R.id.date_show_day)).setText(fmtOut_.format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            GetData(year + "-" + month + "-" + day);
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        date_picker_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEnquiry.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        //   editDate.setText(i2 + "-" + (i1 + 1) + "-" + i);
                        date_get_next = i + "-" + (i1 + 1) + "-" + i2;
                        Date date = null;
                        try {
                            date = fmt.parse(date_get_next);
                            date_show.setText(fmtOut.format(date));
                            ((TextView) findViewById(R.id.date_show_day)).setText(fmtOut_.format(date));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        slotModelArrayList.clear();
                        GetData(date_get_next);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1);
                datePickerDialog.show();
            }

        });
    }

    public void GetData(String date_get) {
        final ProgressDialog progressDialog = new ProgressDialog(AddEnquiry.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.mechanic_slot, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        no_slot.setVisibility(View.GONE);
                        recyclerView_slot.setVisibility(View.VISIBLE);
                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("Mechanic_slots");
                        slotModelArrayList.clear();

                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            SlotModel slotModel = new SlotModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);
                            slotModel.id = BMIReport.getString("id");
                            slotModel.day = BMIReport.getString("day");
                            slotModel.start_time = BMIReport.getString("start_time");
                            slotModel.end_time = BMIReport.getString("end_time");
                            slotModel.added_date = BMIReport.getString("added_date");
                            slotModel.status = BMIReport.getString("status");
                            slotModel.bookingStatus = BMIReport.getString("is_booked");

                            slotModel.date_get = date_get;//Booked Date

                            slotModelArrayList.add(slotModel);
                        }
                        adapterSlot = new AdapterSlot(slotModelArrayList, AddEnquiry.this);
                        recyclerView_slot.setAdapter(adapterSlot);
                        adapterSlot.notifyDataSetChanged();
                    } else {

                        progressDialog.dismiss();
                        no_slot.setVisibility(View.VISIBLE);
                        recyclerView_slot.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    no_slot.setVisibility(View.VISIBLE);
                    recyclerView_slot.setVisibility(View.GONE);
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
                params.put("date", date_get);
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
        RetryPolicy retryPolicy = new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);

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
                            if (userGender.equals("1")) {
                                Intent intent = new Intent(getApplicationContext(), SchedulePage_A.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                            } else {
                                SendEmail();
                                Intent intent = new Intent(getApplicationContext(), SchedulePage_A.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
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
                params.put("parts_amount", getStringFromEdit(textInputLayout(R.id.et_parts_amt)));
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
                params.put("slot_id", str_selected_slot_id);
                params.put("slot_date", str_selected_slot_date);
                params.put("customer_id", str_customer_id);
                Log.e("params", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };


        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void SendEmail() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.send_invoice,
                response -> {
                    Log.e("jsonObject_sendemail", "onResponse: " + response);

                    String code, message, id, user_id;

                    try {
                        JSONObject jsonObject = new JSONObject(response);


                        if (jsonObject.getString("code").equals("200")) {

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(AddEnquiry.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("product_id", str_product_id);
                params.put("user_id", str_userid);
                params.put("address", getStringFromEdit(textInputLayout(R.id.et_address)));
                params.put("email", getStringFromEdit(textInputLayout(R.id.et_email)));
                params.put("enquiry_id", str_main_id);
                Log.e("params_send", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);


    }

    private EditText textInputLayout(int id) {

        return ((EditText) findViewById(id));
    }

    public void onClickCalled(SlotModel formListModel, int position, String s) {
        if (adapterSlot.getSelected() != null) {
            Log.d("adapterFormList_event", "Init: " + adapterSlot.getSelected().start_time);
            Log.d("adapterFormList_event", "Init: " + adapterSlot.getSelected().id);
            Log.d("adapterFormList_event", "Init: " + date_get_next);
            str_selected_slot_id = adapterSlot.getSelected().id;
            str_selected_slot_date = date_get_next;
           /* book_now.setBackground(getResources().getDrawable(R.drawable.d_orange_corner));
            book_now.setText("Processed for Booking");
            String final_time = adapterSlot.getSelected().start_time + " - " + adapterSlot.getSelected().end_time;
            Log.d("adapterFormList_event", "Init: " + final_time);
            book_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AddEnquiry.class);
                    intent.putExtra("str_product_id", sharedPreferences.getString(PRODUCT_ID, ""));
                    intent.putExtra("date_get", designModel.date_get);
                    intent.putExtra("str_mech_id", str_mec_id);
                    intent.putExtra("slot_id", designModel.id);
                    intent.putExtra("form", "now");
                    intent.putExtra("flag", "add");
                    startActivity(intent);
                }
            });
*/

        }
    }

    class CustomDialogClass extends Dialog implements
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