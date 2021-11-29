package com.witgoedxpert.technician.Helper;

import static android.content.Context.MODE_PRIVATE;
import static com.witgoedxpert.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.witgoedxpert.technician.Forms.Functions;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MultiPartRequest {

    public Activity context;
    public MultipartBuilder builder;
    private OkHttpClient client;
    private Callback callBack;
    SharedPreferences sharedPreferences;
    String url = "";

    public MultiPartRequest(Activity context, String url_call, Callback callback) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        this.context = context;
        this.url = url_call;
        this.builder = new MultipartBuilder();
        this.builder.type(MultipartBuilder.FORM);
        this.client = new OkHttpClient();
        client.setConnectTimeout(20, TimeUnit.SECONDS);
        client.setWriteTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        this.callBack = callback;
    }

    public void addString(String name, String value) {
        this.builder.addFormDataPart(name, value);
        Functions.LOGE("MultipartRequest_strings", "" + name + " : " + value);


    }


    public void addvideoFile(String name, String filePath, String fileName) {
        this.builder.addFormDataPart(name, fileName, RequestBody.create(
                MediaType.parse("video/mp4"), new File(filePath)));
    }

    public void addImageFile(String name, String filePath, String fileName) {
        this.builder.addFormDataPart(name, fileName, RequestBody.create(
                MediaType.parse("image/png"), new File(filePath)));

        Functions.LOGE("MultipartRequest_image", "" + name + " : " + filePath);


    }

    public void addPDF(String name, String filePath, String fileName) {
        this.builder.addFormDataPart(name, fileName, RequestBody.create(
                MediaType.parse("text/plain"), new File(filePath)));
    }


    public String execute() {
        new send().execute();
        return "";
    }

    public class send extends AsyncTask<MultiPartRequest, Void, String> {


        @Override
        protected String doInBackground(MultiPartRequest... multiPartRequests) {
            RequestBody requestBody = null;
            Request request = null;
            Response response = null;


            String strResponse = null;

            try {
                Headers.Builder headerBuilder = new Headers.Builder();
                String authorization = "Bearer "+ sharedPreferences.getString(SERVER_TOKEN, "");
                ;

                if (authorization != null && !authorization.equals(""))
                    headerBuilder.add("Authorization", authorization);


                Functions.LOGE(Variables.tag, authorization);
                Functions.LOGE(Variables.tag, url);

                requestBody = builder.build();
                request = new Request.Builder()
                        .url(url).post(requestBody)
                        .headers(headerBuilder.build())
                        .build();


                response = client.newCall(request).execute();

                int code = response.code();
                String message = response.message();

                Functions.LOGE(Variables.tag, "MultiPartRequest " + response.code());
                Functions.LOGE(Variables.tag, "MultiPartRequest " + response.message());
                Functions.LOGE(Variables.tag, "MultiPartRequest " + response.toString());


                if (code == 403) {

                    if (message.equalsIgnoreCase("Forbidden")) {

                    }

                }


                Functions.LOGE(Variables.tag, "MultiPartRequest_response" + response.toString());

                if (!response.isSuccessful())
                    throw new IOException();

                if (response.isSuccessful()) {
                    strResponse = response.body().string();
                    Functions.LOGE(Variables.tag, "MultiPartRequest_s: " + strResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Functions.LOGE(Variables.tag, "MultiPartRequest_e: " + e.toString());
                /*Functions.CALL_API_login(context);*/

            } finally {
                builder = null;
                if (client != null)
                    client = null;
                System.gc();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String response) {

            if (response != null)
                callBack.Responce(response);
            else
                callBack.Responce("");

            Functions.LOGE(Variables.tag, "MultiPartRequest_:" + response);
            super.onPostExecute(response);
        }
    }

}

