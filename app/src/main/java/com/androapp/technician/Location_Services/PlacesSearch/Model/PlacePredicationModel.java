package com.androapp.technician.Location_Services.PlacesSearch.Model;
public class PlacePredicationModel
{
    String description , place_id , highLigt , lat , lng , city , state , country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getHighLigt() {
        return highLigt;
    }

    public void setHighLigt(String highLigt) {
        this.highLigt = highLigt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }


    @Override
    public String toString() {
        return "PlacePredicationModel{" +
                "description='" + description + '\'' +
                ", place_id='" + place_id + '\'' +
                ", highLigt='" + highLigt + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
