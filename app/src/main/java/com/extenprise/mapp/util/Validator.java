package com.extenprise.mapp.util;

/**
 * Created by ambey on 22/7/15.
 */
public abstract class Validator {
    public static boolean isEmailValid(String email) {
        int i = email.indexOf("@");
        int j = email.lastIndexOf(".");

        return !(i == -1 || i == 0 || j == -1 || j <= (i + 1) || (j + 2) >= email.length());
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public static boolean isPhoneValid(String phoneNo) {
        return !(phoneNo == null || phoneNo.length() < 10);
    }
}
