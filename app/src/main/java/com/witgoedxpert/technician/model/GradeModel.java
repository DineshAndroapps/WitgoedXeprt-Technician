package com.witgoedxpert.technician.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GradeModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("grade")
    @Expose
    private String grade;
    @SerializedName("letseduvate_id")
    @Expose
    private String letseduvateId;
    public final static Creator<GradeModel> CREATOR = new Creator<GradeModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GradeModel createFromParcel(android.os.Parcel in) {
            return new GradeModel(in);
        }

        public GradeModel[] newArray(int size) {
            return (new GradeModel[size]);
        }

    }
            ;

    protected GradeModel(android.os.Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.grade = ((String) in.readValue((String.class.getClassLoader())));
        this.letseduvateId = ((String) in.readValue((String.class.getClassLoader())));
    }

    public GradeModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getLetseduvateId() {
        return letseduvateId;
    }

    public void setLetseduvateId(String letseduvateId) {
        this.letseduvateId = letseduvateId;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(grade);
        dest.writeValue(letseduvateId);
    }

    public int describeContents() {
        return 0;
    }

}