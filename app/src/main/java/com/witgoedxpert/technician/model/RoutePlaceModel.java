package com.witgoedxpert.technician.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RoutePlaceModel implements Parcelable {
    public String id;
    public String sequence_id;
    public String bde_id;
    public String route_id;
    public String name;
    public String latitude;
    public String longitude;
    public String branchAssigned;
    public String contactPerson;
    public String mobile;
    public String phone;
    public String email;
    public String address;
    public String city;
    public String state;
    public String category;
    public String createdDate;
    public String isDeleted;

    public RoutePlaceModel() {
    }

    protected RoutePlaceModel(Parcel in) {
        id = in.readString();
        sequence_id = in.readString();
        bde_id = in.readString();
        route_id = in.readString();
        name = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        branchAssigned = in.readString();
        contactPerson = in.readString();
        mobile = in.readString();
        phone = in.readString();
        email = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        category = in.readString();
        createdDate = in.readString();
        isDeleted = in.readString();
    }

    public static final Creator<RoutePlaceModel> CREATOR = new Creator<RoutePlaceModel>() {
        @Override
        public RoutePlaceModel createFromParcel(Parcel in) {
            return new RoutePlaceModel(in);
        }

        @Override
        public RoutePlaceModel[] newArray(int size) {
            return new RoutePlaceModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sequence_id);
        dest.writeString(bde_id);
        dest.writeString(route_id);
        dest.writeString(name);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(branchAssigned);
        dest.writeString(contactPerson);
        dest.writeString(mobile);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(category);
        dest.writeString(createdDate);
        dest.writeString(isDeleted);
    }
}
