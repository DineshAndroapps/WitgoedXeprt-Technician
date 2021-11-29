package com.witgoedxpert.technician.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.witgoedxpert.technician.Forms.Functions;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Location_Services.GpsUtils;
import com.witgoedxpert.technician.Location_Services.LocationTrack;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.DesignModel;
import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;

import static com.witgoedxpert.technician.Forms.LoginActivity.ROLE;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_LAT;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_LONG;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlaceDetails extends AppCompatActivity implements View.OnClickListener {

    TextView title, details, email_id, address, experience, feepersession, qualification, sessiontiming, uploadPlacePath;
    ImageView placeImage;
    Button book_now, uploadPlaceImage;
    SharedPreferences sharedPreferences;
    String str_userid, str_intent_flag, str_theme;
    DesignModel designModel;
    private static final int UPLOADING_IMAGE_SIZE = 800;
    private int GALLERY = 1, CAMERA = 2;

    private static final String IMAGE_DIRECTORY_NAME = "ProMarketing";
    private static final int REQUEST_TAKE_PHOTO = 02;
    private static final int REQUEST_PICK_PHOTO = 13;
    private static final int CAMERA_PIC_REQUEST = 16;
    private Uri fileUri;
    private String mImageFileLocation = "", imagePath;
    String str_image_1 = "", address_id = "", str_user_lat = "0.0", str_user_long = "0.0";
    TextView tv_photo;

    public static final int PERMISSIONS_DENIED = 1;
    private static final String PACKAGE_URL_SCHEME = "package:";
    android.app.AlertDialog alertDialog;
    SharedPreferences.Editor editor;
    private static final int TAKE_IMAGE1_REQ_CODE = 18;

    private static final int PICK_IMAGE1_REQ_CODE = 21;

    private LocationTrack mLocationTrack;
    private double latitude = 0.0, longitude = 0.0;
    private String parsedDistance;
    String photo_server = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_details);
        EnableGPS();
        Intent i = getIntent();


        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER, "");
        str_user_lat = sharedPreferences.getString(USER_LAT, "");
        str_user_long = sharedPreferences.getString(USER_LONG, "");

        String userRole = sharedPreferences.getString(ROLE, "");
        tv_photo = findViewById(R.id.tv_photo);
        placeImage = findViewById(R.id.placeImage);

        if (userRole.equalsIgnoreCase("BDM")) {
            findViewById(R.id.tv_photo).setVisibility(View.GONE);
            findViewById(R.id.uploadPlaceImage).setVisibility(View.GONE);
            if (i.getStringExtra("photoCreatedDate").equals("null")) {
                ((TextView) (findViewById(R.id.upload_date))).setText("");

            } else {
                ((TextView) (findViewById(R.id.upload_date))).setText("Added Date & Time:- " + i.getStringExtra("photoCreatedDate"));

            }
        } else {
            findViewById(R.id.tv_photo).setVisibility(View.VISIBLE);
            findViewById(R.id.uploadPlaceImage).setVisibility(View.VISIBLE);
            placeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestMultiplePermissions();
                }
            });
            tv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestMultiplePermissions();
                }
            });

        }

        Log.d("varribel", "onCreate: " + str_user_lat);

        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Place Details");


//        Glide.with(DesignerDetails.this).load(Constant.design_img + designModel.image).placeholder(R.drawable.logo_trans).into(image);
        Log.d("data_here", "onCreate: " + i.getStringExtra("id"));
//sequence_id ,photoCreatedDate
        ((TextView) findViewById(R.id.address)).setText(i.getStringExtra("name"));
        address_id = i.getStringExtra("id");
        ((TextView) findViewById(R.id.branchAssigned)).setText(i.getStringExtra("branchAssigned"));
        ((TextView) findViewById(R.id.sessiontiming)).setText(i.getStringExtra("state"));
        ((TextView) findViewById(R.id.category)).setText(i.getStringExtra("category"));
        ((TextView) findViewById(R.id.city)).setText(i.getStringExtra("city"));
        photo_server = i.getStringExtra("photo");
        Log.d("photo_server", "onCreate: " + photo_server);

        Glide.with(getApplicationContext()).load(photo_server).placeholder(R.drawable.ic_action_upload).into(placeImage);


        if (i.getStringExtra("mobile").equals("") || i.getStringExtra("mobile").equals("null")) {
            ((TextView) findViewById(R.id.mobile)).setText("");
        } else {
            ((TextView) findViewById(R.id.mobile)).setText(i.getStringExtra("mobile"));
        }
        if (i.getStringExtra("phone").equals("") || i.getStringExtra("phone").equals("null")) {
            ((TextView) findViewById(R.id.phone)).setText("");
        } else {
            ((TextView) findViewById(R.id.phone)).setText(i.getStringExtra("phone"));
        }
        if (i.getStringExtra("email").equals("") || i.getStringExtra("email").equals("null")) {
            ((TextView) findViewById(R.id.email_id)).setText("");
        } else {
            ((TextView) findViewById(R.id.email_id)).setText(i.getStringExtra("email"));
        }

        uploadPlaceImage = findViewById(R.id.uploadPlaceImage);
        uploadPlacePath = findViewById(R.id.uploadPlacePath);
        uploadPlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (str_image_1.equals("")) {
                    Toast.makeText(PlaceDetails.this, "Please Select Location Image", Toast.LENGTH_SHORT).show();
                } else {
                    SendImage();
                }

            }
        });


    }

    public void EnableGPS() {

        if (Functions.check_location_permissions(this)) {
            if (!GpsUtils.isGPSENABLE(PlaceDetails.this)) {
                Functions.showFragment(PlaceDetails.this);
            } else {
                enable_permission();
            }
        } else {
            Functions.showFragment(PlaceDetails.this);
        }
    }


    private void enable_permission() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {

            new GpsUtils(this).turnGPSOn(isGPSEnable -> {

                enable_permission();

            });
        } else if (Functions.check_location_permissions(this)) {
            Functions.Bind_Location_service(PlaceDetails.this);

            //  SetHomeScreen();

        }
    }


    private void SendImage() {

        final ProgressDialog progressDialog = new ProgressDialog(PlaceDetails.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.locationPhoto,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("resp", "onResponse: " + response);

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            code = jsonObject.getString("code");
                            message = jsonObject.getString("message");

                            if (code.equals("200")) {

                                Toast.makeText(PlaceDetails.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(PlaceDetails.this, "" + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PlaceDetails.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pro_id", str_userid);
                params.put("place_id", address_id);
                params.put("photo", str_image_1);
                params.put("longitude", str_user_lat);
                params.put("latitude", str_user_long);
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

    public static String getStringImageBase64(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void showPictureDialog() {
        alertDialog = new android.app.AlertDialog.Builder(PlaceDetails.this).create();
        LayoutInflater inflater = LayoutInflater.from(PlaceDetails.this);
        View layout_pwd = inflater.inflate(R.layout.capture_image_dialog, null);
        alertDialog.setView(layout_pwd);
        layout_pwd.findViewById(R.id.btn_gallery_dialog).setOnClickListener(this);
        layout_pwd.findViewById(R.id.btn_camera_dialog).setOnClickListener(this);
        alertDialog.show();
    }

    public void setStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            int statusBarColor = Color.parseColor(color);
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
        /*
        * getSupportActionBar().setBackgroundDrawable(
        new ColorDrawable(Color.parseColor("#AA3939")));
        *
        * */
    }
    // IMAGE/VIDEO PICKED DELEGATE ------------------------------


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage)
                        .setAspectRatio(1, 1)
                        .start(PlaceDetails.this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                placeImage.setImageURI(resultUri);

                InputStream imageStream = null;
                try {
                    imageStream = PlaceDetails.this.getContentResolver().openInputStream(resultUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), null, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                //  imageView.setImageBitmap(bitmap);
                String strDocument = Constant.getStringImageBase64(rotatedBitmap);
                Log.d("IMAGE_1sstrDocument_ga", "onActivityResult: " + strDocument);
                str_image_1 = strDocument;


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == CAMERA) {
            Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));
            CropImage.activity(selectedImage).start(PlaceDetails.this);
        }

        switch (requestCode) {
            case 3: {
                if (resultCode == RESULT_OK) {
                    Functions.Bind_Location_service(PlaceDetails.this);
                }
            }
            break;
        }

    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(PlaceDetails.this.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(PlaceDetails.this, PlaceDetails.this.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, CAMERA);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = PlaceDetails.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix /
                ".jpg",         // suffix /
                storageDir      // directory /
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(PlaceDetails.this).withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    showPictureDialog();
                }
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showMissingPermissionDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(PlaceDetails.this, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showMissingPermissionDialog() {
        android.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PlaceDetails.this);
        dialogBuilder.setTitle(R.string.string_permission_help);
        dialogBuilder.setMessage(R.string.string_permission_help_text);
        dialogBuilder.setNegativeButton(R.string.string_permission_quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PlaceDetails.this.setResult(PERMISSIONS_DENIED);
                PlaceDetails.this.finish();
            }
        });
        dialogBuilder.setPositiveButton(R.string.string_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + PlaceDetails.this.getPackageName()));
                startActivity(intent);
            }
        });
        dialogBuilder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_camera_dialog:
                openCameraIntent();
                alertDialog.dismiss();
                break;

            case R.id.btn_gallery_dialog:
                choosePhotoFromGallary();
                alertDialog.dismiss();
                break;


        }
    }
}
