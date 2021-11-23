package com.androapp.technician.Helper;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Methods {


    public static String getStringFromTextview(TextView textView) {

        return textView.getText().toString().trim();
    }

    public static String getStringFromEdit(EditText editText) {

        return editText.getText().toString().trim();
    }

    public static void ShowMsg(Activity context,String id) {
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
    }

}
