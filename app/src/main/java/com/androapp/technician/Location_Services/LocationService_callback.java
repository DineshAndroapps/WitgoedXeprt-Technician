package com.androapp.technician.Location_Services;

import com.google.android.gms.maps.model.LatLng;

public interface LocationService_callback {

    void onDataReceived(LatLng data);
}
