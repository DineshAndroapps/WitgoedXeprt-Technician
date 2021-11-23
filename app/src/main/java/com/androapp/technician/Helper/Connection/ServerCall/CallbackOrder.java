package com.androapp.technician.Helper.Connection.ServerCall;

import com.androapp.technician.model.OrderModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackOrder implements Serializable {

    public String code = "";
    public List<OrderModel> orderModels = new ArrayList<>();


}
