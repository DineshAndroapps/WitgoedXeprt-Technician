package com.witgoedxpert.technician.Forms;

import static com.witgoedxpert.technician.Forms.Functions.getStringFromEdit;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.witgoedxpert.technician.Activity.ProfilePage_A;
import com.witgoedxpert.technician.Adapters.ServiceModelAdapter;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.Tools;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.ServiceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UpdateProfile_A extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_name_pro, str_image_1 = "", str_product_id = "", str_main_id, date_get, userGender = "0";
    private RadioGroup radioGenderGroup;
    private RadioButton radio_F, radio_M;
    ImageView image;
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Update Profile");
        findViewById(R.id.bt_menu).setOnClickListener(view -> onBackPressed());
        image = findViewById(R.id.image);
        radioGenderGroup = findViewById(R.id.radioGrp);
        radio_F = findViewById(R.id.radioF);
        radio_M = findViewById(R.id.radioM);
        radioGenderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // TODO Auto-generated method stub
            int childCount = group.getChildCount();

            for (int x = 0; x < childCount; x++) {
                RadioButton btn = (RadioButton) group.getChildAt(x);


                if (btn.getId() == R.id.radioM) {
                    btn.setText("Male");
                } else {
                    btn.setText("Female");
                }
                if (btn.getId() == checkedId) {

                    if (btn.getText().toString().equals("Male")) {
                        userGender = "male";
                    } else {
                        userGender = "female";
                    }

                }

            }

            Log.e("Gender", userGender);
        });

        findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        SendFCM("");
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (Constant.isNetworkAvailable(UpdateProfile_A.this)) {
                    if (textInputLayout(R.id.et_name).getText().toString().equals("")) {
                        Toast.makeText(UpdateProfile_A.this, "Please Enter the valid Name", Toast.LENGTH_SHORT).show();

                    } else if (textInputLayout(R.id.et_address).getText().toString().trim().equals("")) {
                        Toast.makeText(UpdateProfile_A.this, "Please Select the Address", Toast.LENGTH_SHORT).show();

                    }else if (textInputLayout(R.id.et_phone).getText().toString().trim().length() < 10) {
                        Toast.makeText(UpdateProfile_A.this, "Please Enter the valid Phone No", Toast.LENGTH_SHORT).show();

                    }   else if (userGender.trim().equals("")) {
                        Toast.makeText(UpdateProfile_A.this, "Please Select the Gender", Toast.LENGTH_SHORT).show();

                    }  else {

                        UpdateData();
                    }
                } else {
                    Toast.makeText(UpdateProfile_A.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }




    private EditText textInputLayout(int id) {

        return ((EditText) findViewById(id));
    }

    private void SendFCM(String token) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.mechanicDetails,
                response -> {

                    String code, message, id, user_id;

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        code = jsonObject.getString("code");
                        message = jsonObject.getString("message");

                        Log.d("resp", "onResponse: " + jsonObject + "===" + code);
                        if (code.equals("200")) {

                            JSONObject user_data = jsonObject.getJSONObject("Mechanic_data");
                            textInputLayout(R.id.et_email).setText(user_data.getString("email"));
                            textInputLayout(R.id.et_name).setText(user_data.getString("name"));
                            textInputLayout(R.id.et_phone).setText(user_data.getString("mobile"));
                            textInputLayout(R.id.et_address).setText(user_data.getString("address"));
                            Glide.with(getApplicationContext()).load(Constant.image_url_mechanic + user_data.getString("profile_pic")).placeholder(R.drawable.app_icon).into(image);

                            if (user_data.getString("gender").equals("male")) {
                                radio_M.setChecked(true);
                            } else {
                                radio_F.setChecked(true);
                            }

                        } else {
                            // Toast.makeText(UpdateProfile_A.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // progressDialog.dismiss();

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                // Toast.makeText(UpdateProfile_A.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mechanic_id", str_userid);
                params.put("fcm_id", "");
                params.put("fcm_flag", "0");// 1 -Update FCM and 0- Don't update fcm
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

        Volley.newRequestQueue(UpdateProfile_A.this).add(stringRequest);


    }
    private void UpdateData() {

        final ProgressDialog progressDialog = new ProgressDialog(UpdateProfile_A.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.update_profile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            code = jsonObject.getString("code");
                            message = jsonObject.getString("message");

                            Log.d("resp", "onResponse: " + jsonObject + "===" + code);
                            if (code.equals("200")) {


                                Intent intent = new Intent(getBaseContext(), ProfilePage_A.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();


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
                map.put("name", getStringFromEdit(textInputLayout(R.id.et_name)));
                map.put("email", getStringFromEdit(textInputLayout(R.id.et_email)));
                map.put("mobile", getStringFromEdit(textInputLayout(R.id.et_phone)));
                map.put("gender", userGender);
                map.put("profile_pic", str_image_1);
                map.put("mechanic_id", str_userid);
                map.put("address", getStringFromEdit(textInputLayout(R.id.et_address)));

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (resultCode == RESULT_OK) {
                    image.setVisibility(View.VISIBLE);
                    // selfie_imageview.setImageURI(selectedImage);
                    Glide.with(UpdateProfile_A.this)
                            .load(selectedImage)
                            .into(image);
                    InputStream imageStream = null;
                    try {
                        imageStream = UpdateProfile_A.this.getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), null, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    str_image_1 = Constant.getStringImageBase64(rotatedBitmap);
                    Log.d("IMAGE_1sstrDocument_ga", "onActivityResult: " + str_image_1);

                   /* try {
                        str_image_1 = FileUtils.getFileFromUri(getApplicationContext(), selectedImage);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }*/

                }
            }
        } else if (requestCode == CAMERA) {
            // Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

            File image_file = new File(imageFilePath);
            Uri selectedImage = (Uri.fromFile(image_file));
            if (resultCode == RESULT_OK) {
                image.setVisibility(View.VISIBLE);
                //  selfie_imageview.setImageURI(selectedImage);

                Glide.with(UpdateProfile_A.this)
                        .load(selectedImage)
                        .into(image);

                InputStream imageStream = null;
                try {
                    imageStream = UpdateProfile_A.this.getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), null, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                str_image_1 = Constant.getStringImageBase64(rotatedBitmap);

               /* File file = new File(getExternalFilesDir(null) + "img" + timeStamp + ".jpg");

                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(file));
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    str_image_1 = file;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    str_image_1 = FileUtils.getFileFromUri(getApplicationContext(), selectedImage);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }*/

            }


        }


    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile_A.this, R.style.AlertDialogCustom);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog_pic, int item) {

                if (options[item].equals("Take Photo")) {
                    if (Tools.check_permissions(UpdateProfile_A.this))
                        openCameraIntent();
                } else if (options[item].equals("Choose from Gallery")) {

                    if (Tools.check_permissions(UpdateProfile_A.this)) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY);
                    }
                } else if (options[item].equals("Cancel")) {

                    dialog_pic.dismiss();

                }

            }

        });

        builder.show();

    }


    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(UpdateProfile_A.this.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(UpdateProfile_A.this, UpdateProfile_A.this.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, CAMERA);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = UpdateProfile_A.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix /
                ".jpg",         // suffix /
                storageDir      // directory /
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }
    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}