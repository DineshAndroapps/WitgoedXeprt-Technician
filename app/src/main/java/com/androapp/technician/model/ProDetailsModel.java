package com.androapp.technician.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProDetailsModel implements Parcelable {
    public String id;
    public String pro_id;
    public String pro_name;
    public String route_id;
    public String assign_date;
    public String createdDate;
    public Object updatedDate;
    public String isDeleted;

    public ProDetailsModel() {
    }

    public ProDetailsModel(Parcel in) {
        id = in.readString();
        pro_id = in.readString();
        pro_name = in.readString();
        route_id = in.readString();
        assign_date = in.readString();
        createdDate = in.readString();
        isDeleted = in.readString();
    }

    public static final Creator<ProDetailsModel> CREATOR = new Creator<ProDetailsModel>() {
        @Override
        public ProDetailsModel createFromParcel(Parcel in) {
            return new ProDetailsModel(in);
        }

        @Override
        public ProDetailsModel[] newArray(int size) {
            return new ProDetailsModel[size];
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
        dest.writeString(pro_name);
        dest.writeString(route_id);
        dest.writeString(assign_date);
        dest.writeString(createdDate);
        dest.writeString(isDeleted);
    }
}
