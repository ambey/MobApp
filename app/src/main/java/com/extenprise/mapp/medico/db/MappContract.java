package com.extenprise.mapp.medico.db;

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
        public static final String COLUMN_NAME_CELLPHONE = "phone";
        public static final String COLUMN_NAME_PASSWD = "passwd";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_QUALIFICATION = "qualification";
        public static final String COLUMN_NAME_REGISTRATION_NUMBER = "registrationNumber";
        public static final String COLUMN_NAME_IMAGE = "img";
    }

    public static abstract class Service implements BaseColumns {
        public static final String TABLE_NAME = "Service";

        public static final String COLUMN_NAME_SERVICE_NAME = "name";
        public static final String COLUMN_NAME_SERVICE_CATAGORY = "category";
    }

    public static abstract class ServProvHasServPt implements BaseColumns {
        public static final String TABLE_NAME = "ServProvHasServPt";

        public static final String COLUMN_NAME_SERV_PROV_PHONE = "servProvPhone";
        public static final String COLUMN_NAME_ID_SERV_PT = "idServPt";
        public static final String COLUMN_NAME_ID_SERVICE = "idService";
        public static final String COLUMN_NAME_SERVICE_POINT_TYPE = "servicePointType";
        public static final String COLUMN_NAME_WORKING_DAYS = "workingDays";
        public static final String COLUMN_NAME_START_TIME = "startTime";
        public static final String COLUMN_NAME_END_TIME = "endTime";
        public static final String COLUMN_NAME_EXP = "experience";
        public static final String COLUMN_NAME_CONSULTATION_FEE = "consultFee";
    }

    public static abstract class ServicePoint implements BaseColumns {
        public static final String TABLE_NAME = "ServicePoint";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_PINCODE = "pincode";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_ALT_PHONE = "altPhone";
        public static final String COLUMN_NAME_EMAIL_ID = "emailID";
        public static final String COLUMN_NAME_ID_CITY = "idCity";
    }

    public static abstract class Customer implements BaseColumns {
        public static final String TABLE_NAME = "Customer";
        public static final String COLUMN_NAME_ID_CUSTOMER = "idCustomer";
        public static final String COLUMN_NAME_FNAME = "fName";
        public static final String COLUMN_NAME_LNAME = "lName";
        public static final String COLUMN_NAME_PASSWD = "passwd";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_AGE = "age";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_PIN_CODE = "pinCode";
        public static final String COLUMN_NAME_CELLPHONE = "cellphone";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_EMAIL_ID = "emailID";
        public static final String COLUMN_NAME_ID_STATE = "idState";
        public static final String COLUMN_NAME_ID_CITY = "idCity";
        public static final String COLUMN_NAME_DOB = "dob";
        public static final String COLUMN_NAME_IMAGE = "img";
    }

    public static abstract class Appointment implements BaseColumns {
        public static final String TABLE_NAME = "Appointment";

        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_FROM_TIME_STR = "fromTimeStr";
        public static final String COLUMN_NAME_FROM_TIME = "fromTime";
        public static final String COLUMN_NAME_TO_TIME = "toTime";
        public static final String COLUMN_NAME_ID_CUSTOMER = "idCustomer";
        public static final String COLUMN_NAME_ID_SERV_PROV_SERV_PT = "idServProvHasServPt";
    }

    public static abstract class Report implements BaseColumns {
        public static final String TABLE_NAME = "Report";

        public static final String COLUMN_NAME_ID_APPMT = "idAppmt";
        public static final String COLUMN_NAME_REPORT_TYPE = "reportType";
        public static final String COLUMN_NAME_ID_REPORT = "idReport";
    }

    public static abstract class Prescription implements BaseColumns {
        public static final String TABLE_NAME = "Prescription";

        public static final String COLUMN_NAME_ID_APPOMT = "idAppont";
        public static final String COLUMN_NAME_ID_RX = "idRx";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_SR_NO = "srNo";
        public static final String COLUMN_NAME_DRUG_NAME = "dName";
        public static final String COLUMN_NAME_DRUG_STRENGTH = "dStrength";
        public static final String COLUMN_NAME_DRUG_FORM = "dForm";
        public static final String COLUMN_NAME_DOSE_QTY = "doseQty";
        public static final String COLUMN_NAME_COURSE_DUR = "courseDur";
        public static final String COLUMN_NAME_TIMES_PER_DAY = "timesPerDay";
        public static final String COLUMN_NAME_TIMING = "timing";
        public static final String COLUMN_NAME_EMPTY_OR_FULL = "emptyOrFull";
        public static final String COLUMN_NAME_INTAKE_STEPS = "intakeSteps";
        public static final String COLUMN_NAME_ALT_DRUG_NAME = "altDName";
        public static final String COLUMN_NAME_ALT_DRUG_STRENGTH = "altDStrength";
        public static final String COLUMN_NAME_ALT_DRUG_FORM  = "altDForm";
        public static final String COLUMN_NAME_SCANNED_COPY = "scannedCopy";
    }

}
