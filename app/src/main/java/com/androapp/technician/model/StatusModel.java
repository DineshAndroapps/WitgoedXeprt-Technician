package com.androapp.technician.model;

import java.util.List;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("status_name")
    @Expose
    private String statusName;
    @SerializedName("sequence_no")
    @Expose
    private Object sequenceNo;
    @SerializedName("is_delete")
    @Expose
    private Boolean isDelete;
    @SerializedName("is_datetime_required")
    @Expose
    private Boolean isDatetimeRequired;
    @SerializedName("status_code")
    @Expose
    private Object statusCode;
    @SerializedName("status_type")
    @Expose
    private Integer statusType;
    @SerializedName("parent")
    @Expose
    private Object parent;
    @SerializedName("school_fk")
    @Expose
    private List<Integer> schoolFk = null;
    public final static Creator<StatusModel> CREATOR = new Creator<StatusModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public StatusModel createFromParcel(android.os.Parcel in) {
            return new StatusModel(in);
        }

        public StatusModel[] newArray(int size) {
            return (new StatusModel[size]);
        }

    }
            ;

    protected StatusModel(android.os.Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.statusName = ((String) in.readValue((String.class.getClassLoader())));
        this.sequenceNo = ((Object) in.readValue((Object.class.getClassLoader())));
        this.isDelete = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.isDatetimeRequired = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.statusCode = ((Object) in.readValue((Object.class.getClassLoader())));
        this.statusType = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.parent = ((Object) in.readValue((Object.class.getClassLoader())));
        in.readList(this.schoolFk, (java.lang.Integer.class.getClassLoader()));
    }

    public StatusModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Object getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(Object sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public Boolean getIsDatetimeRequired() {
        return isDatetimeRequired;
    }

    public void setIsDatetimeRequired(Boolean isDatetimeRequired) {
        this.isDatetimeRequired = isDatetimeRequired;
    }

    public Object getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Object statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusType() {
        return statusType;
    }

    public void setStatusType(Integer statusType) {
        this.statusType = statusType;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public List<Integer> getSchoolFk() {
        return schoolFk;
    }

    public void setSchoolFk(List<Integer> schoolFk) {
        this.schoolFk = schoolFk;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(statusName);
        dest.writeValue(sequenceNo);
        dest.writeValue(isDelete);
        dest.writeValue(isDatetimeRequired);
        dest.writeValue(statusCode);
        dest.writeValue(statusType);
        dest.writeValue(parent);
        dest.writeList(schoolFk);
    }

    public int describeContents() {
        return 0;
    }

}