package com.extenprise.mapp.medico.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ambey on 15/9/15.
 */
public class SignInData implements Parcelable {
    public static final Creator<SignInData> CREATOR = new Creator<SignInData>() {
        @Override
        public SignInData createFromParcel(Parcel source) {
            return new SignInData(source);
        }

        @Override
        public SignInData[] newArray(int size) {
            return new SignInData[size];
        }
    };
    private String phone;
    private String passwd;

    public SignInData() {
        phone = "";
        passwd = "";
    }

    public SignInData(Parcel source) {
        String[] fields = new String[2];
        source.readStringArray(fields);
        phone = fields[0];
        passwd = fields[1];
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{phone, passwd});
    }
}
