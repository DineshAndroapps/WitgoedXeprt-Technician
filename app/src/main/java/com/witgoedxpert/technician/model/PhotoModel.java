package com.witgoedxpert.technician.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoModel implements Parcelable {

    public String id;
    public String pro_id;
    public String place_id;
    public String photo;
    public String longitude;
    public String latitude;
    public String createdDate;
    public String updatedDate;
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

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public PhotoModel() {
    }

    protected PhotoModel(Parcel in) {
        id = in.readString();
        photo = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        createdDate = in.readString();
    }

    public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel in) {
            return new PhotoModel(in);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(photo);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(createdDate);

    }
}
