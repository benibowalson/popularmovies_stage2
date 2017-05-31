package com.example.android.popularmoviesstage2.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 48101040 on 5/24/2017.
 */

public class Trailer implements Parcelable {

    public String id;
    public String languageCode;
    public String countryCode;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;

    public Trailer(String id, String languageCode, String coutryCode, String key, String name, String site, int size, String type){
        this.id = id;
        this.languageCode = languageCode;
        this.countryCode = coutryCode;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    private Trailer(Parcel in){
        id = in.readString();
        languageCode = in.readString();
        countryCode = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(languageCode);
        dest.writeString(countryCode);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);
    }

    public String toString(){
        return id + ", " + name;
    }

    public final static Creator<Trailer> CREATOR = new Creator<Trailer>(){
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int i){
            return new Trailer[i];
        }
    };

}
