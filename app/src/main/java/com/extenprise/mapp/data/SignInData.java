package com.extenprise.mapp.data;

import com.extenprise.mapp.util.EncryptUtil;

/**
 * Created by ambey on 15/9/15.
 */
public class SignInData {
    private String phone;
    private String passwd;

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
}
