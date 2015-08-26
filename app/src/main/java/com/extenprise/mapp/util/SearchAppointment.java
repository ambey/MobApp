package com.extenprise.mapp.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;

import java.util.ArrayList;
import java.util.Map;

public abstract class SearchAppointment {

    private static Cursor cursor;

    public static Cursor getCursor() {
        return cursor;
    }

    public static boolean searchAppointment(MappDbHelper dbHelper, String date, int idDoc) {

        String [] selectionArgs = { "" + idDoc, date };
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery(getQuery(), selectionArgs);
        return (cursor.getCount() > 0);
    }

    private static String getQuery() {
        String query = "select " +
                    MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment._ID + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_FNAME + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_LNAME + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_AGE + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_WEIGHT +
                " from " +
                MappContract.Appointment.TABLE_NAME + ", " +
                MappContract.Customer.TABLE_NAME + " where " +

                MappContract.Appointment.TABLE_NAME + "." +
                MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV + "=? and " +
                MappContract.Appointment.TABLE_NAME + "." +
                MappContract.Appointment.COLUMN_NAME_DATE + "=? and " +

                MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment.COLUMN_NAME_ID_CUSTOMER + " = " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_ID_CUSTOMER;

        return query;
    }

}
