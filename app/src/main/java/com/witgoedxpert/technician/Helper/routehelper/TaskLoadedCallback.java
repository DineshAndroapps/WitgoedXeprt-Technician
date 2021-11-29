package com.witgoedxpert.technician.Helper.routehelper;

public interface TaskLoadedCallback {
    void onTaskDone(String type,Object... values);
    void onError();
}
