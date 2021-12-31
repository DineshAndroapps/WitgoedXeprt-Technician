package com.witgoedxpert.technician.Activity;

import static com.witgoedxpert.technician.Forms.LoginActivity.ADDRESS;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.PHONENO;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import com.witgoedxpert.technician.Activity.Home.SchedulePage_A;
import com.witgoedxpert.technician.Adapters.ServiceModelAdapter;
import com.witgoedxpert.technician.Forms.AddEnquiry;
import com.witgoedxpert.technician.Forms.Functions;
import com.witgoedxpert.technician.Forms.LoginActivity;
import com.witgoedxpert.technician.Forms.UpdateProfile_A;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.FileUtils;
import com.witgoedxpert.technician.Helper.Tools;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;
import com.witgoedxpert.technician.model.ServiceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfilePage_A extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String str_userid, str_intent_flag, accessToken;
    ImageView image;
    final ArrayList<ServiceModel> redeemModelArrayList = new ArrayList();
    RecyclerView recyclerView_gifts;
    private int GALLERY = 1, CAMERA = 2;
    File str_image_1;
    String base_64 = "";
    String token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        findViewById(R.id.bt_menu).setOnClickListener(view -> onBackPressed());
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Profile");
        image = findViewById(R.id.image);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                     token = task.getResult();
                    SendFCM(token);
                    //   getUser(token);

                    Log.d("TOKEN_", "onComplete: " + token);
                });

        findViewById(R.id.edit_profile_pic).setOnClickListener(v -> {
            if (Tools.check_permissions(ProfilePage_A.this))
                selectImage();

        });
        findViewById(R.id.edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UpdateProfile_A.class);
            startActivity(intent);

        });
        findViewById(R.id.image).setOnClickListener(v -> {
            if (Tools.check_permissions(ProfilePage_A.this))
                selectImage();
        });
        recyclerView_gifts = findViewById(R.id.recylerview_gifts);
        recyclerView_gifts.setLayoutManager(new LinearLayoutManager(ProfilePage_A.this));
        //recyclerView_gifts.setLayoutManager(new GridLayoutManager(ProfilePage_A.this, 2));


    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePage_A.this, R.style.AlertDialogCustom);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    if (Tools.check_permissions(ProfilePage_A.this))
                        openCameraIntent();
                } else if (options[item].equals("Choose from Gallery")) {

                    if (Tools.check_permissions(ProfilePage_A.this)) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY);
                    }
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(ProfilePage_A.this.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(ProfilePage_A.this, ProfilePage_A.this.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, CAMERA);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = ProfilePage_A.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix /
                ".jpg",         // suffix /
                storageDir      // directory /
        );

        imageFilePath = image.getAbsolutePath();
        return image;
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
                    // image.setImageURI(selectedImage);

                    InputStream imageStream = null;
                    try {
                        imageStream = ProfilePage_A.this.getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), null, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    String strDocument = Constant.getStringImageBase64(rotatedBitmap);
                    Glide.with(ProfilePage_A.this)
                            .load(rotatedBitmap)
                            .into(image);
                    Log.d("IMAGE_1sstrDocument_ga", "onActivityResult: " + strDocument);
                    try {
                        str_image_1 = FileUtils.getFileFromUri(getApplicationContext(), selectedImage);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                }
            }
        } else if (requestCode == CAMERA) {
            // Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));
            Matrix matrix = new Matrix();
            try {
                ExifInterface exif = new ExifInterface(imageFilePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            str_image_1 = new File(imageFilePath);
            Uri selectedImage = (Uri.fromFile(str_image_1));

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
            Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);

            Bitmap resized = Bitmap.createScaledBitmap(rotatedBitmap, (int) (rotatedBitmap.getWidth() * 0.7), (int) (rotatedBitmap.getHeight() * 0.7), true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 20, baos);


            base_64 = Functions.Bitmap_to_base64(this, resized);
            Log.e("base_64", "onActivityResult: " + base_64);
            if (str_image_1 != null) ;
            Glide.with(this).load(resized).into(image);

            // UserUpdateProfile(edtUsername.getText().toString().trim(), edtUserbank.getText().toString().trim(), edtUserupi.getText().toString().trim(), edtUseradhar.getText().toString().trim());


        }
        switch (requestCode) {
            case 3: {
                if (resultCode == RESULT_OK) {
                    Functions.Bind_Location_service(ProfilePage_A.this);
                }
            }
            break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SendFCM(token);
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
                            ((TextView) findViewById(R.id.name_)).setText(user_data.getString("name"));
                            ((TextView) findViewById(R.id.name)).setText(user_data.getString("name"));
                            ((TextView) findViewById(R.id.mobile_)).setText(user_data.getString("mobile"));
                            ((TextView) findViewById(R.id.mobile)).setText(user_data.getString("mobile"));
                            ((TextView) findViewById(R.id.address)).setText(user_data.getString("address"));
                            ((TextView) findViewById(R.id.name)).setText(user_data.getString("name"));
                            ((TextView) findViewById(R.id.gender)).setText(user_data.getString("gender"));
                            ((TextView) findViewById(R.id.email)).setText(user_data.getString("email"));
                            Glide.with(getApplicationContext()).load(Constant.image_url_mechanic + user_data.getString("profile_pic")).placeholder(R.drawable.app_icon).into(image);
                            JSONArray jsonArrayvideo = user_data.getJSONArray("service_details");

                            if (jsonArrayvideo != null) {
                                redeemModelArrayList.clear();
                                for (int i = 0; i < jsonArrayvideo.length(); i++) {
                                    ServiceModel heartRateModel = new ServiceModel();
                                    JSONObject HeartRateAnlysis = jsonArrayvideo.getJSONObject(i);
                                    Log.e("HeartRateAnlysis", "onResponse: " + HeartRateAnlysis.getString("name"));
                                    heartRateModel.id = HeartRateAnlysis.getString("id");
                                    heartRateModel.name = HeartRateAnlysis.getString("name");
                                    heartRateModel.image = HeartRateAnlysis.getString("image");
                                    heartRateModel.fee = HeartRateAnlysis.getString("fee");
                                    heartRateModel.added_date = HeartRateAnlysis.getString("added_date");
                                    redeemModelArrayList.add(heartRateModel);

                                }
                                ServiceModelAdapter userWinnerAdapter = new ServiceModelAdapter(redeemModelArrayList, (AppCompatActivity) ProfilePage_A.this);
                                recyclerView_gifts.setAdapter(userWinnerAdapter);
                            }

                        } else {
                            // Toast.makeText(ProfilePage_A.this, "" + message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // progressDialog.dismiss();

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                // Toast.makeText(ProfilePage_A.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mechanic_id", str_userid);
                params.put("fcm_id", token);
                params.put("fcm_flag", "1");// 1 -Update FCM and 0- Don't update fcm
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

        Volley.newRequestQueue(ProfilePage_A.this).add(stringRequest);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), SchedulePage_A.class);
        finish();
        startActivity(i);
    }
}