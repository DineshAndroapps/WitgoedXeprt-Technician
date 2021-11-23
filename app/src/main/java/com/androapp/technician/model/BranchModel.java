package com.androapp.technician.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BranchModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("branch_latitude")
    @Expose
    private String branchLatitude;
    @SerializedName("branch_longitude")
    @Expose
    private String branchLongitude;
    public final static Creator<BranchModel> CREATOR = new Creator<BranchModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BranchModel createFromParcel(android.os.Parcel in) {
            return new BranchModel(in);
        }

        public BranchModel[] newArray(int size) {
            return (new BranchModel[size]);
        }

    };

    protected BranchModel(android.os.Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.branchName = ((String) in.readValue((String.class.getClassLoader())));
        this.branchLatitude = ((String) in.readValue((String.class.getClassLoader())));
        this.branchLongitude = ((String) in.readValue((String.class.getClassLoader())));
    }

    public BranchModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchLatitude() {
        return branchLatitude;
    }

    public void setBranchLatitude(String branchLatitude) {
        this.branchLatitude = branchLatitude;
    }

    public String getBranchLongitude() {
        return branchLongitude;
    }

    public void setBranchLongitude(String branchLongitude) {
        this.branchLongitude = branchLongitude;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(branchName);
        dest.writeValue(branchLatitude);
        dest.writeValue(branchLongitude);
    }

    public int describeContents() {
        return 0;
    }

}