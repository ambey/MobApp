package com.extenprise.mapp.util;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.extenprise.mapp.R;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ambey on 22/7/15.
 */
public abstract class Validator {
    public static boolean isEmailValid(String email) {
        //int i = email.indexOf("@");
        //int j = email.lastIndexOf(".");
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public static boolean isPhoneValid(String phoneNo) {
        /*if (phoneNo.length() != 10) {
            return false;
        }
        if (phoneNo.charAt(0) == '0') {
            return false;
        }
        if (!isOnlyDigits(phoneNo)) {
            return false;
        }
        return true;*/

        return phoneNo.length() == 10 && phoneNo.charAt(0) != '0' && isOnlyDigits(phoneNo);
    }

    public static boolean isOnlyDigits(String digits) {
        char[] carray = digits.toCharArray();
        for (char c : carray) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOnlyAlpha(String name) {
        char[] carray = name.toCharArray();
        for (char c : carray) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPinCodeValid(String pinCode) {
        return !(pinCode == null || pinCode.length() == 6);
    }

    /*private static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }*/

    public static boolean isEmailValid2(String email) {
        boolean isValid = false;

        //String emailregex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        //"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.(?:[A-Z]{2,}|com|org))*$"
        //Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\-_\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\-_\"]]+\.[a-zA-Z0-9[!#$%&'()*+,/\-_\"\.]]+");

        //String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
        //String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isValuePositive(String val) {
        try {
            int dose = Integer.parseInt(val);
            if (dose <= 0) {
                return false;
            }
        } catch (NumberFormatException x) {
            return false;
        }
        return true;
    }

    public static boolean isFeeValid(String val) {
        int i = val.lastIndexOf('.');
        return val.length() > 6 || !(val.length() > 4 && i == -1) && !(i != -1 && val.substring(i + 1).length() > 2);
    }
}
