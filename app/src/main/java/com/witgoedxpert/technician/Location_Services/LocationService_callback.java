package com.witgoedxpert.technician.Location_Services;

import com.google.android.gms.maps.model.LatLng;

public interface LocationService_callback {

    void onDataReceived(LatLng data);
}
