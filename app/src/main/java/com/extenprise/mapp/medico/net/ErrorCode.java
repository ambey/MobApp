package com.extenprise.mapp.medico.net;

/**
 * Created by ambey on 30/11/15.
 */
public abstract class ErrorCode {
    public static final int ERROR_REQUEST_FAILED = 404;
    public static final int ERROR_NETWORK_PROBLEM = 410;
    public static final int ERROR_APPONT_CONFLICT = 411;
    public static final int ERROR_APPONT_ALREADY_BOOKED = 412;
    public static final int ERROR_INVALID_USER_OR_PASSWD = 413;
    public static final int ERROR_USER_EXISTS = 414;
    public static final int ERROR_SERVER_UNAVAILABLE = 415;
    public static final int ERROR_PHONE_EXISTS = 416;
    public static final int ERROR_WRONG_PASSWD = 417;
    public static final int ERROR_NO_RX_FOUND = 418;
    public static final int ERROR_NO_MATCHING_RESULT = 419;
    public static final int ERROR_NO_AVAILABLE_TIMESLOT = 420;
    public static final int ERROR_NO_WORKPLACE_FOUND = 421;
}
