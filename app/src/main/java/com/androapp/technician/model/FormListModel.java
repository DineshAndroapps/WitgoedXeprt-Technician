package com.androapp.technician.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormListModel implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("source_name")
    @Expose
    private String sourceName;
    @SerializedName("sub_name")
    @Expose
    private String subName;

    public boolean isChecked = false;

    public final static Creator<FormListModel> CREATOR = new Creator<FormListModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FormListModel createFromParcel(android.os.Parcel in) {
            return new FormListModel(in);
        }

        public FormListModel[] newArray(int size) {
            return (new FormListModel[size]);
        }

    }
            ;

    protected FormListModel(android.os.Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.sourceName = ((String) in.readValue((String.class.getClassLoader())));
        this.subName = ((String) in.readValue((String.class.getClassLoader())));
    }

    public FormListModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(sourceName);
        dest.writeValue(subName);
    }

    public int describeContents() {
        return 0;
    }

}