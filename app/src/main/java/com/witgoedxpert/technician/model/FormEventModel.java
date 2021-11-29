package com.witgoedxpert.technician.model;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FormEventModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("is_tieup")
    @Expose
    private Boolean isTieup;
    @SerializedName("branch")
    @Expose
    private Integer branch;
    @SerializedName("contact_source_id")
    @Expose
    private Integer contactSourceId;
    @SerializedName("create_by")
    @Expose
    private Object createBy;
    @SerializedName("update_by")
    @Expose
    private List<Object> updateBy = null;
    public final static Creator<FormEventModel> CREATOR = new Creator<FormEventModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FormEventModel createFromParcel(android.os.Parcel in) {
            return new FormEventModel(in);
        }

        public FormEventModel[] newArray(int size) {
            return (new FormEventModel[size]);
        }

    };

    protected FormEventModel(android.os.Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.eventName = ((String) in.readValue((String.class.getClassLoader())));
        this.isTieup = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.branch = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.contactSourceId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.createBy = ((Object) in.readValue((Object.class.getClassLoader())));
        in.readList(this.updateBy, (java.lang.Object.class.getClassLoader()));
    }

    public FormEventModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Boolean getIsTieup() {
        return isTieup;
    }

    public void setIsTieup(Boolean isTieup) {
        this.isTieup = isTieup;
    }

    public Integer getBranch() {
        return branch;
    }

    public void setBranch(Integer branch) {
        this.branch = branch;
    }

    public Integer getContactSourceId() {
        return contactSourceId;
    }

    public void setContactSourceId(Integer contactSourceId) {
        this.contactSourceId = contactSourceId;
    }

    public Object getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Object createBy) {
        this.createBy = createBy;
    }

    public List<Object> getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(List<Object> updateBy) {
        this.updateBy = updateBy;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(eventName);
        dest.writeValue(isTieup);
        dest.writeValue(branch);
        dest.writeValue(contactSourceId);
        dest.writeValue(createBy);
        dest.writeList(updateBy);
    }

    public int describeContents() {
        return 0;
    }

}