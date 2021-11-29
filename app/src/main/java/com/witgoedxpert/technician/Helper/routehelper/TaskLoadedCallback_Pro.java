package com.witgoedxpert.technician.Helper.routehelper;


public interface TaskLoadedCallback_Pro {
    void onTaskDone_Pro(String type,Object... values);
    void onError_Pro();
}