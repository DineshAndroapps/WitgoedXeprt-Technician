package com.androapp.technician.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProLatLngModel implements Parcelable {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ProLatLngModelDatum> data = null;
    @SerializedName("code")
    @Expose
    private Integer code;
    public final static Creator<ProLatLngModel> CREATOR = new Creator<ProLatLngModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ProLatLngModel createFromParcel(android.os.Parcel in) {
            return new ProLatLngModel(in);
        }

        public ProLatLngModel[] newArray(int size) {
            return (new ProLatLngModel[size]);
        }

    };

    protected ProLatLngModel(android.os.Parcel in) {
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.data, (ProLatLngModelDatum.class.getClassLoader()));
        this.code = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public ProLatLngModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ProLatLngModelDatum> getData() {
        return data;
    }

    public void setData(List<ProLatLngModelDatum> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(message);
        dest.writeList(data);
        dest.writeValue(code);
    }

    public int describeContents() {
        return 0;
    }

}