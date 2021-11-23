package com.androapp.technician.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProLatLngModelDatum implements Parcelable {

    @SerializedName("assign_date")
    @Expose
    private String assignDate;
    @SerializedName("pro_id")
    @Expose
    private String proId;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    public final static Creator<ProLatLngModelDatum> CREATOR = new Creator<ProLatLngModelDatum>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ProLatLngModelDatum createFromParcel(android.os.Parcel in) {
            return new ProLatLngModelDatum(in);
        }

        public ProLatLngModelDatum[] newArray(int size) {
            return (new ProLatLngModelDatum[size]);
        }

    };

    protected ProLatLngModelDatum(android.os.Parcel in) {
        this.assignDate = ((String) in.readValue((String.class.getClassLoader())));
        this.proId = ((String) in.readValue((String.class.getClassLoader())));
        this.longitude = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ProLatLngModelDatum() {
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(assignDate);
        dest.writeValue(proId);
        dest.writeValue(longitude);
        dest.writeValue(latitude);
    }

    public int describeContents() {
        return 0;
    }
}
