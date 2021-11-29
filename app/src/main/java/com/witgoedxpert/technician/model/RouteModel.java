package com.witgoedxpert.technician.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteModel implements Parcelable {

    public String id;

    public RouteModel() {
    }

    protected RouteModel(Parcel in) {
        id = in.readString();
        bde_id = in.readString();
        branch_id = in.readString();
        routeName = in.readString();
        createdDate = in.readString();
        updatedDate = in.readString();
        isDeleted = in.readString();
        route_id = in.readString();
    }

    public static final Creator<RouteModel> CREATOR = new Creator<RouteModel>() {
        @Override
        public RouteModel createFromParcel(Parcel in) {
            return new RouteModel(in);
        }

        @Override
        public RouteModel[] newArray(int size) {
            return new RouteModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBde_id() {
        return bde_id;
    }

    public void setBde_id(String bde_id) {
        this.bde_id = bde_id;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
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

    public String bde_id;
    public String branch_id;
    public String routeName;
    public String createdDate;
    public String updatedDate;
    public String isDeleted;
    public String assign_date;
    public String pro_name;

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String route_id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(bde_id);
        dest.writeString(branch_id);
        dest.writeString(routeName);
        dest.writeString(createdDate);
        dest.writeString(updatedDate);
        dest.writeString(isDeleted);
        dest.writeString(route_id);
    }
}
