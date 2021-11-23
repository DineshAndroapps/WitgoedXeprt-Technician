package com.androapp.technician.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaceLocationModel implements Parcelable {
    public String id;
    public String pro_id;
    public String longitude;
    public String latitude;
    public String createdDate;
    public String isDeleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPro_id() {
        return pro_id;
    }

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public PlaceLocationModel() {
    }

    protected PlaceLocationModel(Parcel in) {
        id = in.readString();
        pro_id = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        createdDate = in.readString();
        isDeleted = in.readString();
    }

    public static final Creator<PlaceLocationModel> CREATOR = new Creator<PlaceLocationModel>() {
        @Override
        public PlaceLocationModel createFromParcel(Parcel in) {
            return new PlaceLocationModel(in);
        }

        @Override
        public PlaceLocationModel[] newArray(int size) {
            return new PlaceLocationModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(pro_id);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(createdDate);
        dest.writeString(isDeleted);
    }
}
