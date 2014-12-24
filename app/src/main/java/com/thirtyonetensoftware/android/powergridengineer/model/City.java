package com.thirtyonetensoftware.android.powergridengineer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * City
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public class City implements Parcelable {

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };

    private int id;

    private String name;

    private Country country;

    private Region region;

    public City(int id, String name, Country country, Region region) {
        setId(id);
        setName(name);
        setCountry(country);
        setRegion(region);
    }

    private City(Parcel in) {
        setId(in.readInt());
        setName(in.readString());
        setCountry(Country.valueOf(in.readInt()));
        setRegion(Region.valueOf(in.readInt()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( ((Object) this).getClass() != obj.getClass() ) {
            return false;
        }

        City other = (City) obj;
        return this.id == other.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getName());
        dest.writeInt(getCountry().getValue());
        dest.writeInt(getRegion().getValue());
    }
}
