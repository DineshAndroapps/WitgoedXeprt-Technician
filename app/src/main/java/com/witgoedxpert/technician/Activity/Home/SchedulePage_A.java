package com.witgoedxpert.technician.Activity.Home;

import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.witgoedxpert.technician.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class SchedulePage_A extends AppCompatActivity {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd'th' MMMM yy ");
    SimpleDateFormat fmtOut_ = new SimpleDateFormat("EEEE ");
    SharedPreferences sharedPreferences;
    String str_userid, str_intent_flag, accessToken;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_page);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        findViewById(R.id.bt_menu).setOnClickListener(view -> onBackPressed());
        ((TextView) findViewById(R.id.toolbr_lbl)).setText("Profile");

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        String date_get = year + "-" + month + "-" + day;
        Date date_ = null;
        try {
            date_ = fmt.parse(date_get);
            ((TextView) findViewById(R.id.date_show)).setText(fmtOut_.format(date) + " " + fmtOut.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        GetData(year + "-" + month + "-" + day);
    }

    private void GetData(String date_) {
        Log.e("date_format", "GetData: "+date_ );


    }
}