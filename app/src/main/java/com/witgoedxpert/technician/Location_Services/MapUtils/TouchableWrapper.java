package com.witgoedxpert.technician.Location_Services.MapUtils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.witgoedxpert.technician.Forms.Functions;


public class TouchableWrapper extends FrameLayout {
    private OnMapInterTouch mListener;

    public TouchableWrapper(Context context) {
        super(context);
        try {
            mListener = (OnMapInterTouch) (Activity) context;
        } catch (ClassCastException e) {

            Functions.LOGE("TouchableWrapper", "error=>" + e.getMessage());
        }
        // this.mListener = mListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                MapsFragment.mMapIsTouched = true;
//                mListener.OnInterCeptMap("down");
                break;

            case MotionEvent.ACTION_UP:
                MapsFragment.mMapIsTouched = false;
//                mListener.OnInterCeptMap("up");
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}