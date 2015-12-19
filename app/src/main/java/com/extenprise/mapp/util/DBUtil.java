package com.extenprise.mapp.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.extenprise.mapp.customer.data.Customer;
import com.extenprise.mapp.data.Appointment;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;

import java.util.ArrayList;

/**
 * Created by ambey on 3/9/15.
 */
public abstract class DBUtil {
    public static Cursor getServProvAppointmentsCursor(MappDbHelper dbHelper, int serviceProvId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = getSelectClauseForAppointment() + " where ";

        String[] args = null;
        if (serviceProvId != -1 || date != null) {
            ArrayList<String> argList = new ArrayList<>();
            if (serviceProvId != -1) {
                argList.add("" + serviceProvId);
                query += MappContract.Appointment.TABLE_NAME + "." +
                        MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV_SERV_PT + "=? and ";
            }
            if (date != null) {
                argList.add(date);
                query += MappContract.Appointment.TABLE_NAME + "." +
                        MappContract.Appointment.COLUMN_NAME_DATE + "=? and ";
            }
            args = new String[argList.size()];
            for (int i = 0; i < argList.size(); i++) {
                args[i] = argList.get(i);
            }
        }

        query += MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment.COLUMN_NAME_ID_CUSTOMER + " = " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer._ID;

        return db.rawQuery(query, args);
    }

    public static ArrayList<Appointment> getServProvAppointments(MappDbHelper dbHelper, int serviceProvId, String date) {
        Cursor cursor = getServProvAppointmentsCursor(dbHelper, serviceProvId, date);
        ArrayList<Appointment> appointments = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                cursor.moveToNext();
                Appointment appointment = new Appointment();
                appointment.setFrom(cursor.getInt(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_FROM_TIME)));
                //appointment.setDate(cursor.getString(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_DATE)));
                appointment.setIdAppointment(cursor.getInt(cursor.getColumnIndex(MappContract.Appointment.TABLE_NAME + "." +
                        MappContract.Appointment._ID)));
                Customer customer = new Customer();
                customer.setIdCustomer(cursor.getInt(cursor.getColumnIndex(MappContract.Customer.TABLE_NAME + "." +
                        MappContract.Customer._ID)));
                customer.setGender(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_GENDER)));
                customer.setHeight(cursor.getInt(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_HEIGHT)));
                customer.setWeight(cursor.getFloat(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_WEIGHT)));
                customer.setfName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_FNAME)));
                customer.setlName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_LNAME)));
                customer.setAge(cursor.getInt(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_AGE)));
                customer.getSignInData().setPhone(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_CELLPHONE)));
                appointment.setIdCustomer(customer.getIdCustomer());
                appointments.add(appointment);
            } while (!cursor.isLast());
        }
        cursor.close();
        return appointments;
    }

    public static Cursor getAppointmentCursor(MappDbHelper dbHelper, int appontId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = getSelectClauseForAppointment() + " where ";

        String[] args = null;
        if (appontId != -1) {
            args = new String[1];
            args[0] = "" + appontId;
            query += MappContract.Appointment.TABLE_NAME + "." +
                    MappContract.Appointment._ID + "=? and ";
        }

        query += MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment.COLUMN_NAME_ID_CUSTOMER + " = " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer._ID;

        return db.rawQuery(query, args);
    }

    public static Appointment getAppointment(MappDbHelper dbHelper, int appontId) {
        Cursor cursor = getAppointmentCursor(dbHelper, appontId);
        Appointment appointment = null;
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            appointment = new Appointment();
            appointment.setFrom(cursor.getInt(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_FROM_TIME)));
            //appointment.setDate(cursor.getString(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_DATE)));
            appointment.setIdAppointment(cursor.getInt(cursor.getColumnIndex("_id")));
            Customer customer = new Customer();
            customer.setIdCustomer(cursor.getInt(cursor.getColumnIndex("CUST_ID")));
            customer.setGender(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_GENDER)));
            customer.setHeight(cursor.getInt(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_HEIGHT)));
            customer.setWeight(cursor.getFloat(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_WEIGHT)));
            customer.setfName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_FNAME)));
            customer.setlName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_LNAME)));
            customer.setAge(cursor.getInt(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_AGE)));
            customer.getSignInData().setPhone(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_CELLPHONE)));
            appointment.setIdCustomer(customer.getIdCustomer());
        }
        cursor.close();
        return appointment;
    }

    public static Cursor getOtherServProvAppointmentsCursor(MappDbHelper dbHelper, int serviceProvId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MappContract.Appointment._ID,
                MappContract.Appointment.COLUMN_NAME_FROM_TIME,
                MappContract.Appointment.COLUMN_NAME_DATE
        };
        String[] args = null;
        String selection = null;
        if (serviceProvId != -1) {
            args = new String[1];
            args[0] = "" + serviceProvId;

            selection = MappContract.Appointment.COLUMN_NAME_ID_SERV_PROV_SERV_PT + "<>?";
        }

        return db.query(MappContract.Appointment.TABLE_NAME, projection, selection
                , args, null, null, null);
    }

    public static ArrayList<Appointment> getOtherServProvAppointments(MappDbHelper dbHelper, int serviceProvId) {
        Cursor cursor = getOtherServProvAppointmentsCursor(dbHelper, serviceProvId);
        ArrayList<Appointment> appointments = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                cursor.moveToNext();
                Appointment appointment = new Appointment();
                appointment.setFrom(cursor.getInt(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_FROM_TIME)));
                //appointment.setDate(cursor.getString(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_DATE)));
                appointment.setIdAppointment(cursor.getInt(cursor.getColumnIndex(MappContract.Appointment._ID)));
                appointments.add(appointment);
            } while (!cursor.isLast());
        }
        cursor.close();
        return appointments;
    }

    public static ArrayList<Appointment> getOtherAppointments(MappDbHelper dbHelper, int appontId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MappContract.Appointment._ID,
                MappContract.Appointment.COLUMN_NAME_FROM_TIME,
                MappContract.Appointment.COLUMN_NAME_DATE
        };
        String[] args = null;
        String selection = null;
        if (appontId != -1) {
            args = new String[1];
            args[0] = "" + appontId;

            selection = MappContract.Appointment._ID + "<>?";
        }

        Cursor cursor = db.query(MappContract.Appointment.TABLE_NAME, projection, selection
                , args, null, null, null);
        ArrayList<Appointment> appointments = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                cursor.moveToNext();
                Appointment appointment = new Appointment();
                appointment.setFrom(cursor.getInt(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_FROM_TIME)));
                //appointment.setDate(cursor.getString(cursor.getColumnIndex(MappContract.Appointment.COLUMN_NAME_DATE)));
                appointment.setIdAppointment(cursor.getInt(cursor.getColumnIndex(MappContract.Appointment._ID)));
                appointments.add(appointment);
            } while (!cursor.isLast());
        }
        cursor.close();
        return appointments;
    }

    public static Customer getCustomer(MappDbHelper dbHelper, int custId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                MappContract.Customer._ID,
                MappContract.Customer.COLUMN_NAME_FNAME,
                MappContract.Customer.COLUMN_NAME_LNAME,
                MappContract.Customer.COLUMN_NAME_AGE,
                MappContract.Customer.COLUMN_NAME_CELLPHONE,
                MappContract.Customer.COLUMN_NAME_HEIGHT,
                MappContract.Customer.COLUMN_NAME_WEIGHT,
                MappContract.Customer.COLUMN_NAME_GENDER
        };
        String[] args = null;
        String selection = null;
        if (custId != -1) {
            args = new String[1];
            args[0] = "" + custId;

            selection = MappContract.Appointment._ID + "=?";
        }

        Cursor cursor = db.query(true, MappContract.Customer.TABLE_NAME, projection, selection, args, null, null, null, null);
        Customer customer = null;
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            customer = new Customer();
            customer.setIdCustomer(custId);
            customer.setfName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_FNAME)));
            customer.setlName(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_LNAME)));
            customer.getSignInData().setPhone(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_CELLPHONE)));
            customer.setAge(cursor.getInt(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_AGE)));
            customer.setWeight(cursor.getFloat(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_WEIGHT)));
            customer.setHeight(cursor.getFloat(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_HEIGHT)));
            customer.setGender(cursor.getString(cursor.getColumnIndex(MappContract.Customer.COLUMN_NAME_GENDER)));
        }
        cursor.close();
        return customer;
    }

    public static Cursor getRxCursor(MappDbHelper dbHelper, int appontId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = MappContract.Prescription.COLUMN_NAME_ID_APPOMT + "=?";
        String[] args = {"" + appontId};
        return db.query(MappContract.Prescription.TABLE_NAME, null,
                selection, args, null, null, null);
    }

/*
    public static Rx getRx(MappDbHelper dbHelper, int appontId) {
        Cursor cursor = getRxCursor(dbHelper, appontId);
        if (cursor.getCount() <= 0) {
            return null;
        }
        Rx rx = new Rx();
        cursor.moveToFirst();
        rx.setDate(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_DATE)));
        rx.setId(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_ID_RX)));
        while (!cursor.isAfterLast()) {
            RxItem item = new RxItem();
            item.setSrno(cursor.getInt(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_SR_NO)));
            item.setDrugName(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_DRUG_NAME)));
            item.setDrugStrength(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_DRUG_STRENGTH)));
            item.setDrugForm(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_DRUG_FORM)));
            item.setDoseQty(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_DOSE_QTY)));
            item.setCourseDur(cursor.getInt(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_COURSE_DUR)));
            boolean beforeMeal =
                    cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_DRUG_NAME)).equalsIgnoreCase("empty");
            item.setBeforeMeal(beforeMeal);
            item.setInTakeSteps(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_INTAKE_STEPS)));
            item.setAltDrugName(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_ALT_DRUG_NAME)));
            item.setAltDrugStrength(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_ALT_DRUG_STRENGTH)));
            item.setAltDrugForm(cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_ALT_DRUG_FORM)));
            String[] timesPerDay =
                    cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_TIMES_PER_DAY)).split("-");
            item.setMorning(!timesPerDay[0].equals("0"));
            item.setAfternoon(!timesPerDay[1].equals("0"));
            item.setEvening(!timesPerDay[2].equals("0"));
            String[] timing =
                    cursor.getString(cursor.getColumnIndex(MappContract.Prescription.COLUMN_NAME_TIMING)).split("|");
            item.setmTime(timing[0].equals("") ? "Time" : timing[0]);
            item.setaTime(timing[1].equals("") ? "Time" : timing[1]);
            item.seteTime(timing[2].equals("") ? "Time" : timing[2]);
            rx.addItem(item);
            cursor.moveToNext();
        }
        cursor.close();
        return rx;
    }
*/

    public static int deleteRx(MappDbHelper dbHelper, String rxId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = MappContract.Prescription.COLUMN_NAME_ID_RX + "=?";
        String[] args = {"" + rxId};
        return db.delete(MappContract.Prescription.TABLE_NAME, MappContract.Prescription.COLUMN_NAME_ID_RX + "=?", args);
    }

    public static Cursor getSpecialityList(MappDbHelper dbHelper, String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY + "=?";
        String[] args = {category};
        return db.query(MappContract.Service.TABLE_NAME, new String[]{MappContract.Service.COLUMN_NAME_SERVICE_NAME},
                selection, args, null, null, null);
    }

    public static ArrayList<String> getSpecOfCategory(MappDbHelper dbHelper, String servCategory) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] selectionArgs = {servCategory};
        String query = "select DISTINCT " + MappContract.Service.COLUMN_NAME_SERVICE_NAME + " from " +
                MappContract.Service.TABLE_NAME + " where " +
                MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY + " = ? ";

        Cursor cursor = db.rawQuery(query, selectionArgs);

        ArrayList<String> specs = new ArrayList<>();
        specs.add("Select Speciality");

        if (cursor.getCount() > 0) {
            do {
                cursor.moveToNext();
                specs.add(cursor.getString(cursor.getColumnIndex(MappContract.Service.COLUMN_NAME_SERVICE_NAME)));
            } while (!cursor.isLast());
        }
        cursor.close();
        return specs;
        //setNewSpec(activity, specs, speciality);
    }


    private static String getSelectClauseForAppointment() {
        return "select " +
                MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment._ID + " AS _id, " +
                MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment.COLUMN_NAME_DATE + ", " +
                MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment.COLUMN_NAME_FROM_TIME + ", " +
                MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment.COLUMN_NAME_FROM_TIME_STR + ", " +
                MappContract.Appointment.TABLE_NAME + "." + MappContract.Appointment.COLUMN_NAME_TO_TIME + ", " +

                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer._ID + " AS CUST_ID, " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_FNAME + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_LNAME + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_AGE + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_DOB + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_GENDER + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_LOCATION + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_PIN_CODE + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_CELLPHONE + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_WEIGHT + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_HEIGHT + ", " +
                MappContract.Customer.TABLE_NAME + "." + MappContract.Customer.COLUMN_NAME_EMAIL_ID +
                " from " +
                MappContract.Appointment.TABLE_NAME + ", " +
                MappContract.Customer.TABLE_NAME;
    }
}
