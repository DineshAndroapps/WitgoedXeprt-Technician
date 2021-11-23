package com.androapp.technician.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AcademicYearModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("session_year")
    @Expose
    private String sessionYear;
    @SerializedName("default")
    @Expose
    private Boolean _default;
    public final static Creator<AcademicYearModel> CREATOR = new Creator<AcademicYearModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public AcademicYearModel createFromParcel(android.os.Parcel in) {
            return new AcademicYearModel(in);
        }

        public AcademicYearModel[] newArray(int size) {
            return (new AcademicYearModel[size]);
        }

    }
            ;

    protected AcademicYearModel(android.os.Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.sessionYear = ((String) in.readValue((String.class.getClassLoader())));
        this._default = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
    }

    public AcademicYearModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSessionYear() {
        return sessionYear;
    }

    public void setSessionYear(String sessionYear) {
        this.sessionYear = sessionYear;
    }

    public Boolean getDefault() {
        return _default;
    }

    public void setDefault(Boolean _default) {
        this._default = _default;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(sessionYear);
        dest.writeValue(_default);
    }

    public int describeContents() {
        return 0;
    }

}