package com.extenprise.mapp.db;

import android.provider.BaseColumns;

/**
 * Created by ambey on 17/7/15.
 */
public class MappContract {
    public MappContract() {}

    public static abstract class ServiceProvider implements BaseColumns {
        public static final String TABLE_NAME = "ServiceProvider";

        public static final String COLUMN_NAME_FNAME = "fName";
        public static final String COLUMN_NAME_LNAME = "lName";
        public static final String COLUMN_NAME_EMAIL_ID = "emailId";
        public static final String COLUMN_NAME_PASSWD = "passwd";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_QUALIFICATION = "qualification";
        public static final String COLUMN_NAME_CONSULTATION_FEE = "consultationFee";
    }

    public static abstract class ServProvHasServ implements BaseColumns {
        public static final String TABLE_NAME = "ServProvHasServ";

        public static final String COLUMN_NAME_ID_SERV_PROV = "idServProv";
        public static final String COLUMN_NAME_SERVICE_NAME = "serviceName";
        public static final String COLUMN_NAME_SPECIALITY = "speciality";
        public static final String COLUMN_NAME_EXPERIENCE = "experience";
    }

    public static abstract class ServProvHasServHasServPt implements BaseColumns {
        public static final String TABLE_NAME = "ServProvHasServHasServPt";

        public static final String COLUMN_NAME_ID_SERV_PROV_HAS_SERV = "idServProvHasServ";
        public static final String COLUMN_NAME_ID_SERV_PT = "idServPt";
        public static final String COLUMN_NAME_SERVICE_POINT_TYPE = "servicePointType";
        public static final String COLUMN_NAME_WEEKLY_OFF = "weeklyOff";
        public static final String COLUMN_NAME_START_TIME = "startTime";
        public static final String COLUMN_NAME_END_TIME = "endTime";
    }

    public static abstract class ServicePoint implements BaseColumns {
        public static final String TABLE_NAME = "ServicePoint";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_ALT_PHONE = "altPhone";
        public static final String COLUMN_NAME_EMAIL_ID = "emailID";
        public static final String COLUMN_NAME_ID_CITY = "idCity";
    }

    public static abstract class Customer implements BaseColumns {
        public static final String TABLE_NAME = "Customer";

        public static final String COLUMN_NAME_FNAME = "fName";
        public static final String COLUMN_NAME_LNAME = "lName";
        public static final String COLUMN_NAME_PASSWD = "passwd";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_ALT_PHONE = "altPhone";
        public static final String COLUMN_NAME_EMAIL_ID = "emailID";
        public static final String COLUMN_NAME_ID_CITY = "idCity";
        public static final String COLUMN_NAME_DOB = "dob";
    }

    public static abstract class Appointment implements BaseColumns {
        public static final String TABLE_NAME = "Appointment";

        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_FROM_TIME = "fromTime";
        public static final String COLUMN_NAME_TO_TIME = "toTime";
        public static final String COLUMN_NAME_ID_SERV_PT = "idServPt";
        public static final String COLUMN_NAME_ID_CUSTOMER = "idCustomer";
        public static final String COLUMN_NAME_SERVICE_POINT_TYPE = "servicePointType";
        public static final String COLUMN_NAME_ID_SERV_PROV = "idServProv";
        public static final String COLUMN_NAME_SERVICE_NAME = "serviceName";
        public static final String COLUMN_NAME_SPECIALITY = "speciality";
    }
}
