package com.androapp.technician.Helper.Connection;


import com.androapp.technician.Helper.Connection.ServerCall.CallbackOrder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {

    @GET("Enquiry/viewEnquiry")
    Call<CallbackOrder> getListOfOrder(@Query("user_id") String user_id);

}
