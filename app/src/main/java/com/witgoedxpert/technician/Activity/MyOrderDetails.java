package com.witgoedxpert.technician.Activity;

import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.witgoedxpert.technician.Forms.AddEnquiry;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;
import com.bumptech.glide.Glide;

public class MyOrderDetails extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String str_userid, str_intent_flag, accessToken;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        findViewById(R.id.bt_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Intent i = getIntent();
        OrderModel orderModel = (OrderModel) i.getSerializableExtra("MyClass");
        image = findViewById(R.id.image);
        Glide.with(getApplicationContext()).load(Constant.image_url_ + orderModel.image).placeholder(R.drawable.app_icon).into(image);

        ((TextView) findViewById(R.id.toolbr_lbl)).setText(orderModel.product_name);

        ((TextView) findViewById(R.id.name)).setText(orderModel.name);
        ((TextView) findViewById(R.id.description)).setText(orderModel.description);
        ((TextView) findViewById(R.id.error_code)).setText(orderModel.error_code);
        ((TextView) findViewById(R.id.type_of_machine)).setText(orderModel.type_of_machine);
        ((TextView) findViewById(R.id.age_machine)).setText(orderModel.age_machine);
        ((TextView) findViewById(R.id.address)).setText(orderModel.address);
        ((TextView) findViewById(R.id.phone)).setText(orderModel.phone);
        ((TextView) findViewById(R.id.additional_info)).setText(orderModel.additional_info);
        ((TextView) findViewById(R.id.added_date)).setText(orderModel.added_date);
        ((TextView) findViewById(R.id.product_name)).setText(orderModel.product_name);

        findViewById(R.id.btn_Open_signature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEnquiry.class);
                intent.putExtra("str_product_id",orderModel.product_id);
                intent.putExtra("str_name_pro",orderModel.name);
                intent.putExtra("user_id",orderModel.user_id);
                intent.putExtra("main_id",orderModel.id);
                startActivity(intent);
            }
        });
    }
}