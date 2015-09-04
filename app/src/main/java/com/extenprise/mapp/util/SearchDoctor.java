package com.extenprise.mapp.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.extenprise.mapp.R;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ambey on 23/7/15.
 */
public abstract class SearchDoctor {
    /**
     * Shows the progress UI and hides the login form.
     */

    private static Cursor cursor;
    //private static int cursorPosition;

    /*public static void setCursorPosition(int cursorPosition) {
        SearchDoctor.cursorPosition = cursorPosition;
    }*/

    public static Cursor getCursor() {
        return cursor;
    }

/*
    public static boolean closeCursor() {
        cursor.close();
        return true;
    }
*/


    public static boolean searchByAll(MappDbHelper dbHelper, String name, String clinic, String spec, String servCategory, String loc,
                                      String qualification, String exp, String startTime, String endTime,
                                      String availDay, String gender, String consultFee) {
        ArrayList<String> argList = new ArrayList<>();
        String whereClause = getWhereClause(name, clinic, spec, servCategory, loc, qualification,
                exp, startTime, endTime, availDay, gender, consultFee, argList);
        String[] selectionArgs = argList.toArray(new String[argList.size()]);
        String query = getQuery(whereClause);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery(query, selectionArgs);
        return (cursor.getCount() > 0);
    }

    private static String getQuery(String whereClause) {
        String query = "select " +
                MappContract.ServiceProvider.TABLE_NAME + "." + MappContract.ServiceProvider._ID + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_FNAME + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_LNAME + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_GENDER + ", " +
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF + ", " +
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME + ", " +
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME + ", " +
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE + ", " +
                MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + ", " +
                MappContract.ServProvHasServ.COLUMN_NAME_SERVICE_NAME + ", " +
                MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY + ", " +
                MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE + ", " +
                MappContract.ServicePoint.COLUMN_NAME_NAME + ", " +
                MappContract.ServicePoint.COLUMN_NAME_LOCATION + " from " +

                MappContract.ServiceProvider.TABLE_NAME + ", " +
                MappContract.ServProvHasServ.TABLE_NAME + ", " +
                MappContract.ServicePoint.TABLE_NAME + ", " +
                MappContract.ServProvHasServHasServPt.TABLE_NAME + " where ";

        if (!whereClause.equals("")) {
            query += whereClause + " and ";
        }

        query += MappContract.ServProvHasServ.COLUMN_NAME_ID_SERV_PROV + " = " +
                MappContract.ServiceProvider.TABLE_NAME + "." +
                MappContract.ServiceProvider._ID + " and " +

                MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PROV_HAS_SERV + " = " +
                MappContract.ServProvHasServ.TABLE_NAME + "." +
                MappContract.ServProvHasServ._ID + " and " +

                MappContract.ServProvHasServHasServPt.COLUMN_NAME_ID_SERV_PT + " = " +
                MappContract.ServicePoint.TABLE_NAME + "." +
                MappContract.ServicePoint._ID;

        return query;
    }

    private static String getWhereClause(String name, String clinic, String spec, String servCategory, String loc, String qualification,
                                         String exp, String startTime, String endTime,
                                         String availDay, String gender, String consultFee, ArrayList<String> argList) {

        name = name.trim();

        String whereClause = "";
        String fnm = name, lnm = name;
        boolean bracket = false;

        if (!name.equals("")) {
            if (name.contains(" ")) {
                String[] nm = name.split(" ", 2);
                fnm = nm[0];
                if (nm.length > 1) {
                    lnm = nm[1].trim();
                }
            }
            bracket = true;

            if (fnm.equals(lnm)) {
                whereClause += " ((lower(" + MappContract.ServiceProvider.COLUMN_NAME_FNAME + ") like ? or lower(" +
                        MappContract.ServiceProvider.COLUMN_NAME_LNAME + ") like ?) ";

            } else {
                whereClause += " ((lower(" + MappContract.ServiceProvider.COLUMN_NAME_FNAME + ") like ? and lower(" +
                        MappContract.ServiceProvider.COLUMN_NAME_LNAME + ") like ?) ";
            }
            argList.add("%" + fnm.toLowerCase() + "%");
            argList.add("%" + lnm.toLowerCase() + "%");
        }

        if (!name.equals("") && name.equals(clinic)) {
            whereClause += "or lower(" + MappContract.ServicePoint.COLUMN_NAME_NAME + ") like ? ";
            argList.add("%" + clinic.toLowerCase() + "%");

        } else if (!clinic.equals("")) {
            if (!name.equals("")) {
                whereClause += "and ";
            }
            whereClause += " lower(" + MappContract.ServicePoint.COLUMN_NAME_NAME + ") like ?  ";
            argList.add("%" + clinic.toLowerCase() + "%");
        }

        if (bracket) {
            whereClause += ") ";
        }

        if (!spec.equals("")) {
            whereClause += "and lower(" + MappContract.ServProvHasServ.COLUMN_NAME_SPECIALITY + ") = ? ";
            argList.add(spec.toLowerCase());
        }
        if (!servCategory.equals("")) {
            whereClause += "and lower(" + MappContract.ServProvHasServ.COLUMN_NAME_SERVICE_CATAGORY + ") = ? ";
            argList.add(servCategory.toLowerCase());
        }
        if (!loc.equals("")) {
            whereClause += "and lower(" + MappContract.ServicePoint.COLUMN_NAME_LOCATION + ") like ? ";
            argList.add("%" + loc.toLowerCase() + "%");
        }
        if (!exp.equals("")) {
            whereClause += "and " + MappContract.ServProvHasServ.COLUMN_NAME_EXPERIENCE + ">=? ";
            argList.add(exp);
        }
        if (!startTime.equals("")) {
            whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_START_TIME + "<=? ";
            argList.add("" + UIUtility.getMinutes(startTime));
        }
        if (!endTime.equals("")) {
            whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_END_TIME + ">=? ";
            argList.add("" + UIUtility.getMinutes(endTime));
        }

        if (!availDay.equals("")) {
            whereClause += "and ";

            if (availDay.contains(",")) {
                //availDay = availDay.replaceAll(",", "%");
                //availDay = "%" + availDay + "%";
                whereClause += "( ";
                String[] days = availDay.trim().split(",");
                for(int i=0; i<days.length; i++) {
                    whereClause += MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF + " like ? ";
                    argList.add("%" + days[i] + "%");
                    if(i+1 < days.length) {
                        whereClause += " or ";
                    }
                }
                whereClause += " )";
            } else {
                whereClause += MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF + " like ? ";
                argList.add("%" + availDay + "%");
            }
        }

        /*if (!availDay.equals("")) {
            if(availDay.contains(",")) {
                availDay = availDay.replaceAll(",", "%");
            }
            //availDay = "%" + availDay + "%";
            whereClause += MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF + " like ? ";
            argList.add("%" + availDay + "%");
        }*/

        if(!qualification.equals("")) {
            whereClause += "and lower(" + MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION + ") like ? ";
            argList.add("%" + qualification.toLowerCase() + "%");
        }
        if(!gender.equals("")) {
            whereClause += "and " + MappContract.ServiceProvider.COLUMN_NAME_GENDER + " = ? ";
            argList.add(gender);
        }
        if(!consultFee.equals("")) {
            if(consultFee.contains("<")) {
                whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + " < ? ";
                argList.add(consultFee.replaceAll("<", ""));
            } else if(consultFee.contains("-")) {
                String range[] = consultFee.split("-");
                whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + " >= ? ";
                argList.add(range[0]);
                whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + " <= ? ";
                argList.add(range[1]);
            } else if(consultFee.contains(">")) {
                whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + " <= ? ";
                argList.add(consultFee.replaceAll(">", ""));
            }
        }

        if (whereClause.startsWith("and")) {
            return whereClause.substring(3);
        }
        return whereClause;
    }

}
