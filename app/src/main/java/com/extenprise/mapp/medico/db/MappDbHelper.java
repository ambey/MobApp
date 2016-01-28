package com.extenprise.mapp.medico.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ambey on 23/7/15.
 */
public class MappDbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 13;
    public static final String DB_NAME = "Mapp.db";
    public static final String BLOB_TYPE = " BLOB";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String FLOAT_TYPE = " FLOAT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_SERV_PROV =
            "CREATE TABLE " + MappContract.ServiceProvider.TABLE_NAME + " (" +
                    MappContract.ServiceProvider._ID + " INTEGER PRIMARY KEY," +
                    MappContract.ServiceProvider.COLUMN_NAME_IMAGE + BLOB_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_FNAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_LNAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServiceProvider.COLUMN_NAME_PASSWD + TEXT_TYPE + ")";

    private static final String SQL_DELETE_SERV_PROV =
            "DROP TABLE IF EXISTS " + MappContract.ServiceProvider.TABLE_NAME;

    private static final String SQL_CREATE_SERVICE =
            "CREATE TABLE " + MappContract.Service.TABLE_NAME + " (" +
                    MappContract.Service._ID + " INTEGER PRIMARY KEY," +
                    MappContract.Service.COLUMN_NAME_SERVICE_NAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY + TEXT_TYPE + ")";

    private static final String SQL_DELETE_SERVICE =
            "DROP TABLE IF EXISTS " + MappContract.Service.TABLE_NAME;

    private static final String SQL_CREATE_SERV_PROV_HAS_SERV_HAS_SERV_PT =
            "CREATE TABLE " + MappContract.ServProvHasServPt.TABLE_NAME + " (" +
                    MappContract.ServProvHasServPt._ID + " INTEGER PRIMARY KEY," +
                    MappContract.ServProvHasServPt.COLUMN_NAME_SERV_PROV_PHONE + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERV_PT + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERVICE + INT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_START_TIME + INT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_END_TIME + INT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_CONSULTATION_FEE + FLOAT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_EXP + FLOAT_TYPE + COMMA_SEP +
                    MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS + TEXT_TYPE + ")";

    private static final String SQL_DELETE_SERV_PROV_HAS_SERV_HAS_SERV_PT =
            "DROP TABLE IF EXISTS " + MappContract.ServProvHasServPt.TABLE_NAME;

    private static final String SQL_CREATE_SERV_PT =
            "CREATE TABLE " + MappContract.ServicePoint.TABLE_NAME + " (" +
                    MappContract.ServicePoint._ID + " INTEGER PRIMARY KEY," +
                    MappContract.ServicePoint.COLUMN_NAME_EMAIL_ID + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServicePoint.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServicePoint.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServicePoint.COLUMN_NAME_ID_CITY + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServicePoint.COLUMN_NAME_PHONE + TEXT_TYPE + COMMA_SEP +
                    MappContract.ServicePoint.COLUMN_NAME_ALT_PHONE + TEXT_TYPE + ")";

    private static final String SQL_DELETE_SERV_PT =
            "DROP TABLE IF EXISTS " + MappContract.ServicePoint.TABLE_NAME;

    private static final String SQL_CREATE_CUSTOMER =
            "CREATE TABLE " + MappContract.Customer.TABLE_NAME + " (" +
                    MappContract.Customer._ID + " INTEGER PRIMARY KEY," +
                    MappContract.Customer.COLUMN_NAME_ID_CUSTOMER + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_IMAGE + BLOB_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_FNAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_LNAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_CELLPHONE + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_HEIGHT + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_DOB + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_AGE + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_WEIGHT + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_PIN_CODE + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_ID_CITY + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_ID_STATE + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_EMAIL_ID + TEXT_TYPE + COMMA_SEP +
                    MappContract.Customer.COLUMN_NAME_PASSWD + TEXT_TYPE + ")";

    private static final String SQL_DELETE_CUSTOMER =
            "DROP TABLE IF EXISTS " + MappContract.Customer.TABLE_NAME;

    private static final String SQL_CREATE_APPOINTMENT =
            "CREATE TABLE " + MappContract.Appointment.TABLE_NAME + " (" +
                    MappContract.Appointment._ID + " INTEGER PRIMARY KEY," +
                    MappContract.Appointment.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    MappContract.Appointment.COLUMN_NAME_FROM_TIME + TEXT_TYPE + COMMA_SEP +
                    MappContract.Appointment.COLUMN_NAME_FROM_TIME_STR + TEXT_TYPE + COMMA_SEP +
                    MappContract.Appointment.COLUMN_NAME_TO_TIME + TEXT_TYPE + COMMA_SEP +
                    MappContract.Appointment.COLUMN_NAME_ID_CUSTOMER + TEXT_TYPE + COMMA_SEP +
                    MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV_SERV_PT + TEXT_TYPE + ")";

    private static final String SQL_DELETE_APPOINTMENT =
            "DROP TABLE IF EXISTS " + MappContract.Appointment.TABLE_NAME;

    private static final String SQL_CREATE_RX =
            "CREATE TABLE " + MappContract.Prescription.TABLE_NAME + " (" +
                    MappContract.Prescription._ID + " INTEGER PRIMARY KEY," +
                    MappContract.Prescription.COLUMN_NAME_ID_APPOMT + INT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_ID_RX + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_SCANNED_COPY + BLOB_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_SR_NO + INT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_DRUG_NAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_DRUG_STRENGTH + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_DRUG_FORM + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_DOSE_QTY + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_COURSE_DUR + INT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_EMPTY_OR_FULL + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_TIMES_PER_DAY + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_TIMING + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_INTAKE_STEPS + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_ALT_DRUG_NAME + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_ALT_DRUG_STRENGTH + TEXT_TYPE + COMMA_SEP +
                    MappContract.Prescription.COLUMN_NAME_ALT_DRUG_FORM + TEXT_TYPE + ")";

    private static final String SQL_DELETE_RX =
            "DROP TABLE IF EXISTS " + MappContract.Prescription.TABLE_NAME;

    public MappDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SERV_PROV);
        db.execSQL(SQL_CREATE_SERV_PROV_HAS_SERV_HAS_SERV_PT);
        db.execSQL(SQL_CREATE_SERV_PT);
        db.execSQL(SQL_CREATE_SERVICE);
        db.execSQL(SQL_CREATE_CUSTOMER);
        db.execSQL(SQL_CREATE_APPOINTMENT);
        db.execSQL(SQL_CREATE_RX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SERV_PROV);
        db.execSQL(SQL_DELETE_SERV_PROV_HAS_SERV_HAS_SERV_PT);
        db.execSQL(SQL_DELETE_SERV_PT);
        db.execSQL(SQL_DELETE_SERVICE);
        db.execSQL(SQL_DELETE_CUSTOMER);
        db.execSQL(SQL_DELETE_APPOINTMENT);
        db.execSQL(SQL_DELETE_RX);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
