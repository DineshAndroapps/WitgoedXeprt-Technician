package com.androapp.technician.Activity.PRO;

import static com.androapp.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.androapp.technician.Adapters.AdapterAcademicYear;
import com.androapp.technician.Adapters.AdapterChild;
import com.androapp.technician.Adapters.AdapterFormEventList;
import com.androapp.technician.Adapters.AdapterFormList;
import com.androapp.technician.Adapters.AdapterInterested;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.Location_Services.LocationTrack;
import com.androapp.technician.R;
import com.androapp.technician.model.AcademicYearModel;
import com.androapp.technician.model.FormEventModel;
import com.androapp.technician.model.FormListModel;
import com.androapp.technician.model.GradeModel;
import com.androapp.technician.model.StatusModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProFormActivity extends Activity {
    String SELECT_EVENT = "Search Events";
    private RecyclerView form_list_recycler_view;
    private TextView tv_no_form_list, tv_back_form_list;
    private String TAG = ProFormActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private String accessToken, formName;
    private int formID;
    private Bundle bundle;
    String str_mParentRemark = "";
    int str_event_id;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private EditText mParentName, mPhoneNumber, mParentEmail, mParentArea, mParentRemark;
    private TextView mBackButton, mFormTitle;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private ArrayList<FormEventModel> mFormEventList = new ArrayList<>();
    private ArrayList<String> mFormEventList_string = new ArrayList<>();
    private AdapterFormEventList adapterFormEventList;
    private Button btnSubmitForm;
    private Button cvAddChild;
    private ArrayList<ChildMaster> mChildList = new ArrayList<>();
    private ArrayList<GradeModel> mGradeList = new ArrayList<>();
    private ArrayList<StatusModel> mStatusList = new ArrayList<>();
    private ArrayList<FormListModel> event_form_list = new ArrayList<>();
    private String eventID = "";
    private String statusName = "";
    private String academicYear = "";
    private String userGender = "";
    private LocationTrack mLocationTrack;
    private double sLatitude, sLongitude;
    private Spinner mRelationSpinner;
    ArrayAdapter arrayAdapter_event_list;
    private RecyclerView mRecyclerView_f, recycler_view_year, recycler_view_interested, recycler_view_aca_year;
    private TextView tvNoFormList, tvBackFormList;
    private RadioGroup radioGenderGroup;
    private RadioButton radio_F, radio_M, radio_G;
    AdapterFormList adapterFormList_event;
    public static int checkPosition = -1;
    String str_event_server = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Init();

        setData();
    }

    private void setData() {

      /*  bundle = getIntent().getExtras();

        if (bundle != null) {
            formName = bundle.getString("form_name");
            formID = bundle.getInt("form_id");
        }
*/
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(SERVER_TOKEN, "");

        mLocationTrack = new LocationTrack(ProFormActivity.this);
        if (mLocationTrack.canGetLocation()) {
            sLatitude = mLocationTrack.getLatitude();
            sLongitude = mLocationTrack.getLongitude();
        } else {
            mLocationTrack.showSettingsAlert();
        }


        if (Constant.isNetworkAvailable(getApplicationContext())) {
            event_form_list.clear();
            ProGetData();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        btnSubmitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_mParentRemark = mParentRemark.getText().toString();
                Log.e("str_category", "onItemSelected: " + mFormEventList.size());
                Log.e("str_category", "onItemSelected: " + str_event_server);

                if (mFormEventList.size() > 1) {
                    // Log.e("str_category", "onItemSelected: " + mFormEventList.get(mRelationSpinner.getSelectedItemPosition()).getId());
                    str_event_server = mFormEventList.get(mRelationSpinner.getSelectedItemPosition()).getId().toString();
                }
                Log.e("str_category_after", "onItemSelected: " + str_event_server);

                if (str_event_server.equals("Select Events") || str_event_server.equals("0") || str_event_server.equals(""))
                    Toast.makeText(getApplicationContext(), "Please Select event", Toast.LENGTH_SHORT).show();

                else if (academicYear.equals(""))
                    Toast.makeText(getApplicationContext(), "Please choose academic year", Toast.LENGTH_SHORT).show();

                else if (mParentName.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(getApplicationContext(), "Please enter Parent name", Toast.LENGTH_SHORT).show();

                else if (mPhoneNumber.getText().toString().trim().length() < 10)
                    Toast.makeText(getApplicationContext(), "Please enter valid mobile number", Toast.LENGTH_SHORT).show();

                else if (mParentEmail.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "Please enter valid email address", Toast.LENGTH_SHORT).show();

              /*  else if (mParentRemark.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(getApplicationContext(), "Please enter remark", Toast.LENGTH_SHORT).show();
*/
                else if (mParentArea.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(getApplicationContext(), "Please enter lead area", Toast.LENGTH_SHORT).show();

                else if (userGender.equals(""))
                    Toast.makeText(getApplicationContext(), "Please choose relation", Toast.LENGTH_SHORT).show();


                else {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("event_name", str_event_server);
                        jsonObject.put("lead_name", Objects.requireNonNull(mParentName.getText().toString()));
                        jsonObject.put("lead_email_id", mParentEmail.getText().toString());
                        jsonObject.put("lead_contact_no", Objects.requireNonNull(mPhoneNumber.getText().toString()));
                        jsonObject.put("remark", str_mParentRemark);
                        jsonObject.put("lead_area", Objects.requireNonNull(mParentArea.getText().toString()));
                        jsonObject.put("lead_relation", userGender);
                        jsonObject.put("lead_status", statusName);
                        jsonObject.put("academic_year", academicYear);
                        jsonObject.put("contact_source", String.valueOf(formID));
                        jsonObject.put("latitude", String.valueOf(sLatitude));
                        jsonObject.put("longitude", String.valueOf(sLongitude));

                        if (mChildList.size() > 0) {
                            JSONArray childJSONArray = new JSONArray();
                            JSONObject childJObj = new JSONObject();

                            for (ChildMaster childMaster : mChildList) {
                                childJObj.put("child_name", childMaster.getChildName());
                                childJObj.put("child_age", childMaster.getChildAge());
                                childJObj.put("child_previous_school", childMaster.getSchoolName());
                                childJObj.put("child_class", childMaster.getGrade()); // ID
                                childJSONArray.put(childJObj);
                            }

                            jsonObject.put("child_detail_fk", childJSONArray);
                        } else {
                            jsonObject.put("child_detail_fk", new JSONArray());
                        }

                        jsonArray.put(jsonObject);
                        Log.d(TAG, "onClick: " + jsonArray.toString());
                        submitFormJSON(jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cvAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ProFormActivity.this, R.style.DialogTheme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_child);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black50)));
                dialog.show();

                EditText etChildName = dialog.findViewById(R.id.etChildName);
                EditText etChildAge = dialog.findViewById(R.id.etChildAge);
                EditText etSchoolName = dialog.findViewById(R.id.etSchoolName);
                Button btnAddChild = dialog.findViewById(R.id.btnAddChild);
                Spinner spinner = dialog.findViewById(R.id.spinnerGrade);
                ImageView iv_card_child_close = dialog.findViewById(R.id.iv_card_child_close);
                iv_card_child_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (mGradeList.size() > 0) {
                    GradeAdapter gradeAdapter = new GradeAdapter(ProFormActivity.this, mGradeList);
//                    ArrayAdapter<String> adapter = new ArrayAdapter(ProFormActivity.this,
//                            android.R.layout.simple_spinner_item, mGradeList);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(gradeAdapter);
                }

                btnAddChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (etChildName.getText().toString().equalsIgnoreCase(""))
                            Toast.makeText(getApplicationContext(), "Child name should not be blank!", Toast.LENGTH_SHORT).show();

                        else if (etChildAge.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Child age should not be blank!", Toast.LENGTH_SHORT).show();

                        } else if (etSchoolName.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "School name should not be blank!", Toast.LENGTH_SHORT).show();

                        } else {
                            ChildMaster childMaster = new ChildMaster();
                            childMaster.setChildName(etChildName.getText().toString());
                            childMaster.setChildAge(etChildAge.getText().toString());
                            childMaster.setSchoolName(etSchoolName.getText().toString());
                            childMaster.setGrade(String.valueOf(mGradeList.get(spinner.getSelectedItemPosition()).getId()));
                            mChildList.add(childMaster);
                            dialog.dismiss();

                            if (mChildList.size() > 0) {
                                AdapterChild adapterFormEventList = new AdapterChild(mChildList,
                                        ProFormActivity.this);
                                mRecyclerView.setAdapter(adapterFormEventList);
                                adapterFormEventList.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });
    }


    public void onClickCalled(FormListModel designModel, int position, String s) {
        if (adapterFormList_event.getSelected() != null) {
            formName = adapterFormList_event.getSelected().getSubName();
            formID = adapterFormList_event.getSelected().getId();
            mFormTitle.setText("Search " + formName);
            Log.d("adapterFormList_event", "Init: " + adapterFormList_event.getSelected().getId());
            getEvent(adapterFormList_event.getSelected().getId());
            Log.d("TAG_TAG_TAG", "onClick: " + adapterFormList_event.getSelected().getSubName());
        }
        Log.d("TAG_TAG_TAG2", "onClick: " + adapterFormList_event.getSelected().getSubName());

    }


    public void onClickCalled_status(StatusModel designModel, int position, int row_index) {
        statusName = designModel.getStatusName();

        //Below code select and deselect the radio button
       /* if (row_index == -1) {
            statusName = "";
        } else {
            statusName = designModel.getStatusName();
        }*/
        Log.e(TAG, "onClickCalled_status: " + statusName);
        Log.e(TAG, "onClickCalled_status_row_index: " + row_index);
    }

    public void onClickCalled_aca_year(AcademicYearModel designModel, int position, String s) {
        academicYear = designModel.getSessionYear();
    }

    public class ChildMaster {
        public String getChildName() {
            return childName;
        }

        public void setChildName(String childName) {
            this.childName = childName;
        }

        public String getChildAge() {
            return childAge;
        }

        public void setChildAge(String childAge) {
            this.childAge = childAge;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String childName;
        public String childAge;
        public String schoolName;
        public String grade;
    }

    private void submitFormJSON(JSONArray jsonArray) throws JSONException {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.submitForm,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "submitFormJSON: " + response);
                        Toast.makeText(getApplicationContext(), "Successfully added!", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                        checkPosition = -1;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                try {
                    String body = new String(error.networkResponse.data, "UTF-8");
                    Toast.makeText(getApplicationContext(), "Error " + body, Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + accessToken);
                return header;
            }

            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                byte[] byteArray = jsonArray.toString().getBytes();
                String reconstitutedString = new String(byteArray);
                Log.d("getBody: ", reconstitutedString + "");

                return byteArray;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(stringRequest);
    }


    private void Init() {
        mParentName = findViewById(R.id.etParentName);
        mPhoneNumber = findViewById(R.id.etPhoneNo);
        mParentEmail = findViewById(R.id.etEmail);
        mParentArea = findViewById(R.id.etArea);
        mParentRemark = findViewById(R.id.etRemark);
        mRelationSpinner = findViewById(R.id.spinnerEvents);

        FormEventModel examSpinnerModel = new FormEventModel();
        examSpinnerModel.setId(0);
        examSpinnerModel.setEventName(SELECT_EVENT);
        mFormEventList.add(examSpinnerModel);
/*
        mFormEventList_string = new ArrayList<>();
        mFormEventList_string.add(SELECT_EVENT);


        final FormEventModel categoryOModel = new FormEventModel();
        categoryOModel.setId(0);
        categoryOModel.setEventName(SELECT_EVENT);
        mFormEventList.add(categoryOModel);

        arrayAdapter_event_list = new ArrayAdapter(ProFormActivity.this, android.R.layout.simple_spinner_dropdown_item, mFormEventList);
        mRelationSpinner.setAdapter(arrayAdapter_event_list);
        mRelationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                str_event_id = mFormEventList.get(position).getId();
                Log.e("str_category", "onItemSelected: " + str_event_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        radioGenderGroup = findViewById(R.id.radioGrp);
        radio_F = findViewById(R.id.radioF);
        radio_M = findViewById(R.id.radioM);
        radio_G = findViewById(R.id.radioG);

        mFormTitle = findViewById(R.id.formTitle);
        btnSubmitForm = findViewById(R.id.btnSubmitForm);
        cvAddChild = findViewById(R.id.cvAddChild);

        recycler_view_interested = findViewById(R.id.recycler_view_interested);
        recycler_view_interested.setLayoutManager(new GridLayoutManager(this, 3));

        recycler_view_aca_year = findViewById(R.id.recycler_view_aca_year);
        recycler_view_aca_year.setLayoutManager(new GridLayoutManager(this, 3));

        mRecyclerView_f = findViewById(R.id.form_list_recycler_view);
        //mRecyclerView_f.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView_f.setLayoutManager(new GridLayoutManager(this, 5));
        adapterFormList_event = new AdapterFormList(event_form_list, this);
        mRecyclerView_f.setAdapter(adapterFormList_event);

        tvNoFormList = findViewById(R.id.tv_no_form_list);
        tvBackFormList = findViewById(R.id.tv_back_form_list);

        mRecyclerView = findViewById(R.id.childRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        radioGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                int childCount = group.getChildCount();

                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);


                    if (btn.getId() == R.id.radioF) {
                        btn.setText("Father");
                    } else if (btn.getId() == R.id.radioM) {
                        btn.setText("Mother");
                    } else {
                        btn.setText("Guardian");
                    }

                    if (btn.getId() == checkedId) {
                        if (btn.getText().toString().equals("Father")) {
                            userGender = "Father";
                        } else if (btn.getText().toString().equals("Mother")) {
                            userGender = ("Mother");
                        } else {
                            userGender = ("Guardian");
                        }

                    }

                }

                Log.e("Gender", userGender);
            }
        });

    }

    public void getEvent(int formID) {
        mFormEventList = new ArrayList<>();
        Log.d(TAG, "getEvent: "+Constant.proEvent + formID);
        FormEventModel examSpinnerModel = new FormEventModel();
        examSpinnerModel.setId(0);
        examSpinnerModel.setEventName(SELECT_EVENT);
        mFormEventList.add(examSpinnerModel);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.proEvent + formID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("form_event_pro", "onResponse: " + response);
                try {

                    Type listType = new TypeToken<ArrayList<FormEventModel>>() {
                    }.getType();
                    final ArrayList<FormEventModel> formListModel = new Gson().fromJson(response, listType);

                    for (FormEventModel formEventModel : formListModel) {
                        mFormEventList.add(formEventModel);
                    }

                    if (mFormEventList.size() > 0) {
                        ArrayList<String> mAcademicYear = new ArrayList<>();

                        for (FormEventModel academicYearModel : mFormEventList) {
                            mAcademicYear.add(academicYearModel.getEventName());
                        }
                        ArrayAdapter adapter = new ArrayAdapter(ProFormActivity.this, android.R.layout.simple_spinner_dropdown_item, mAcademicYear);
                        mRelationSpinner.setAdapter(adapter);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
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

    public class GradeAdapter extends BaseAdapter {
        Context context;
        ArrayList<GradeModel> statusModels;
        LayoutInflater inflter;

        public GradeAdapter(Context applicationContext, ArrayList<GradeModel> statusModels) {
            this.context = applicationContext;
            this.statusModels = statusModels;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return statusModels.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.item_spinner, null);
            TextView names = (TextView) view.findViewById(R.id.title);
            names.setText(statusModels.get(i).getGrade());
            return view;
        }
    }


    public void ProGetData() {
        final ProgressDialog progressDialog = new ProgressDialog(ProFormActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.pro_common_data, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("form_list_pro", "onResponse: " + response);
                try {

                    Type listType = new TypeToken<ArrayList<FormListModel>>() {
                    }.getType();
                    JSONObject jsonObject = new JSONObject(response);
                    String fetch_event = jsonObject.getJSONArray("source_data").toString();
                    String academic_year_data = jsonObject.getJSONArray("academic_year_data").toString();
                    String enquiry_status_data = jsonObject.getJSONArray("enquiry_status_data").toString();
                    String grade_data = jsonObject.getJSONArray("grade_data").toString();
                    Log.e(TAG, "onResponse_str: " + fetch_event);

                    final ArrayList<FormListModel> formListModel = new Gson().fromJson(fetch_event, listType);

                    if (formListModel.size() > 0) {
                        for (FormListModel gradeModel : formListModel) {
                            event_form_list.add(gradeModel);
                        }
                        adapterFormList_event = new AdapterFormList(event_form_list, ProFormActivity.this);
                        mRecyclerView_f.setAdapter(adapterFormList_event);
                        adapterFormList_event.notifyDataSetChanged();

                    } else {
                        Toast.makeText(ProFormActivity.this, "Data Not Found!", Toast.LENGTH_SHORT).show();
                    }
                    /*-----------------------------------------------academic_year_data----------------------------------------------------------------------------------*/
                    Type listType_academic_year_data = new TypeToken<ArrayList<AcademicYearModel>>() {
                    }.getType();
                    final ArrayList<AcademicYearModel> academicYearModels = new Gson().fromJson(academic_year_data, listType_academic_year_data);

                    if (academicYearModels.size() > 0) {
                        ArrayList<AcademicYearModel> mAcademicYear = new ArrayList<>();
                        for (AcademicYearModel academicYearModel : academicYearModels) {
                            mAcademicYear.add(academicYearModel);
                        }
                        AdapterAcademicYear adapterFormList = new AdapterAcademicYear(mAcademicYear, ProFormActivity.this);
                        recycler_view_aca_year.setAdapter(adapterFormList);
                        adapterFormList.notifyDataSetChanged();

                    }
                    /*-----------------------------------------------enquiry_status_data----------------------------------------------------------------------------------*/

                    Type listType_enquiry_status_data = new TypeToken<ArrayList<StatusModel>>() {
                    }.getType();
                    final ArrayList<StatusModel> statusModels = new Gson().fromJson(enquiry_status_data, listType_enquiry_status_data);

                    if (statusModels.size() > 0) {
                        for (StatusModel statusModel : statusModels) {
                            mStatusList.add(statusModel);
                        }

                        AdapterInterested adapterFormList = new AdapterInterested(mStatusList, ProFormActivity.this);
                        recycler_view_interested.setAdapter(adapterFormList);
                        adapterFormList.notifyDataSetChanged();

                    }
                    /*-----------------------------------------------grade_data----------------------------------------------------------------------------------*/
                    Type listType_grade_data = new TypeToken<ArrayList<GradeModel>>() {
                    }.getType();
                    final ArrayList<GradeModel> gradeModels = new Gson().fromJson(grade_data, listType_grade_data);

                    if (gradeModels.size() > 0) {
                        for (GradeModel gradeModel : gradeModels) {
                            mGradeList.add(gradeModel);
                        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkPosition = -1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        checkPosition = -1;

    }
}

