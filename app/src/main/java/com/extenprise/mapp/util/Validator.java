package com.extenprise.mapp.util;

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

        //return !(i == -1 || i == 0 || j == -1 || j <= (i + 1) || (j + 2) >= email.length());
    }
    /*public static boolean isEmailValid(String email) {
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }*/

    public static boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public static boolean isPhoneValid(String phoneNo) {
        //return !(phoneNo == null || phoneNo.length() < 10);
        return !(phoneNo == null || phoneNo.length() > 13 || phoneNo.length() < 10);
        //return android.util.Patterns.PHONE.matcher(phoneNo).matches(); /^([0|\+[0-9]{1,5})?([7-9][0-9]{9})$/
    }

    public static boolean isPinCodeValid(String pinCode) {
        //String reg = "^d{5}(?:[-s]d{4})?$";
        return !(pinCode == null || pinCode.length() == 6);
    }

    private static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;

    }

    public static boolean isEmailValid2(String email){
        boolean isValid = false;

        //String emailregex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        //"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.(?:[A-Z]{2,}|com|org))*$"
        //Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\-_\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\-_\"]]+\.[a-zA-Z0-9[!#$%&'()*+,/\-_\"\.]]+");

        //String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";
        //String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            isValid = true;
        }
        return isValid;
    }

}
