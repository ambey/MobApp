package com.extenprise.mapp.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;

/**
 * Created by ambey on 23/7/15.
 */
public abstract class SearchServProv {
    /**
     * Shows the progress UI and hides the login form.
     */

    private static Cursor cursor;
    //private static int cursorPosition;

    /*public static void setCursorPosition(int cursorPosition) {
        SearchServProv.cursorPosition = cursorPosition;
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


/*
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

        */
/*if (!availDay.equals("")) {
            if(availDay.contains(",")) {
                availDay = availDay.replaceAll(",", "%");
            }
            //availDay = "%" + availDay + "%";
            whereClause += MappContract.ServProvHasServHasServPt.COLUMN_NAME_WEEKLY_OFF + " like ? ";
            argList.add("%" + availDay + "%");
        }*//*


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
                argList.add(consultFee.replaceAll("<", "").trim());
            } else if(consultFee.contains("-")) {
                String range[] = consultFee.split("-");
                whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + " >= ? ";
                argList.add(range[0]);
                whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + " <= ? ";
                argList.add(range[1]);
            } else if(consultFee.contains(">")) {
                whereClause += "and " + MappContract.ServProvHasServHasServPt.COLUMN_NAME_CONSULTATION_FEE + " > ? ";
                argList.add(consultFee.replaceAll(">", "").trim());
            }
        }

        if (whereClause.startsWith("and")) {
            return whereClause.substring(3);
        }
        return whereClause;
    }
*/

/*
    public static ServProvHasServHasServPt getSPSSPT(MappDbHelper dbHelper) {

        String id = "";
        String whereClause = MappContract.ServiceProvider.TABLE_NAME + "." +
                MappContract.ServiceProvider._ID + " = ? ";

        if(LoginHolder.servLoginRef != null) {
            id = "" + LoginHolder.servLoginRef.getIdServiceProvider();
        }

        ServProvHasServHasServPt spsspt = new ServProvHasServHasServPt();
        ServProvHasService sps = new ServProvHasService();
        Service s = new Service();
        ServicePoint sp = new ServicePoint();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {
                id
        };
        Cursor c = db.rawQuery(getQuery(whereClause), selectionArgs);

        int count = c.getCount();
        if (count > 0) {
            c.moveToFirst();
            spsspt.setWeeklyOff(c.getString(0));
            spsspt.setStartTime(Integer.parseInt(c.getString(1)));
            spsspt.setEndTime(Integer.parseInt(c.getString(2)));
            spsspt.setServPointType(c.getString(3));
            spsspt.setConsultFee(Float.parseFloat(c.getString(4)));

            s.setSpeciality(c.getString(6));
            sps.setExperience(Float.parseFloat(c.getString(7)));
            s.setServCatagory(c.getString(8));
            sps.setService(s);

            sp.setEmailId(c.getString(9));
            sp.setCity(c.getString(10));
            sp.setPhone(c.getString(11));
            sp.setAltPhone(c.getString(12));
            sp.setName(c.getString(13));
            sp.setLocation(c.getString(14));

            spsspt.setServicePoint(sp);
            spsspt.setServProvHasService(sps);
        }
        cursor = c;
        c.close();
        return spsspt;
    }
*/

    public static boolean viewWorkPlaces(MappDbHelper dbHelper) {
        String id = "";
        String whereClause = MappContract.ServiceProvider.TABLE_NAME + "." +
                MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + " = ? ";

        if (LoginHolder.servLoginRef != null) {
            id = "" + LoginHolder.servLoginRef.getPhone();
        }
        String[] selectionArgs = {
                id
        };
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery(getQuery(whereClause), selectionArgs);
        return (cursor.getCount() > 0);
    }

    private static String getQuery(String whereClause) {
        String query = select_from_where();

        if (!whereClause.equals("")) {
            query += whereClause + " and ";
        }

        query += MappContract.ServiceProvider.TABLE_NAME + "." + MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + " = " +
                MappContract.ServProvHasServPt.COLUMN_NAME_SERV_PROV_PHONE + " and " +
                MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERV_PT + " = " +
                MappContract.ServicePoint.TABLE_NAME + "." +
                MappContract.ServicePoint._ID + " and " +
                MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERVICE + " = " +
                MappContract.Service.TABLE_NAME + "." + MappContract.Service._ID;

        return query;
    }

    private static String select_from_where() {

        return "select " +

                MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS + ", " +
                MappContract.ServProvHasServPt.COLUMN_NAME_START_TIME + ", " +
                MappContract.ServProvHasServPt.COLUMN_NAME_END_TIME + ", " +
                MappContract.ServProvHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE + ", " +
                MappContract.ServProvHasServPt.COLUMN_NAME_CONSULTATION_FEE + ", " +

                MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY + "," +
                MappContract.Service.TABLE_NAME + "." + MappContract.Service.COLUMN_NAME_SERVICE_NAME + "," +

                MappContract.ServicePoint.TABLE_NAME + "." + MappContract.ServicePoint.COLUMN_NAME_EMAIL_ID + ", " +
                MappContract.ServicePoint.COLUMN_NAME_ID_CITY + ", " +
                MappContract.ServicePoint.TABLE_NAME + "." + MappContract.ServicePoint.COLUMN_NAME_PHONE + ", " +
                MappContract.ServicePoint.COLUMN_NAME_ALT_PHONE + ", " +
                MappContract.ServicePoint.TABLE_NAME + "." + MappContract.ServicePoint.COLUMN_NAME_NAME + ", " +
                MappContract.ServicePoint.COLUMN_NAME_LOCATION + ", " +

                MappContract.ServiceProvider.TABLE_NAME + "." + MappContract.ServiceProvider._ID + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_FNAME + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_LNAME + ", " +
                MappContract.ServiceProvider.TABLE_NAME + "." + MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID + ", " +
                MappContract.ServiceProvider.TABLE_NAME + "." + MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION + ", " +
                MappContract.ServiceProvider.COLUMN_NAME_GENDER

                + " from " +
                MappContract.ServiceProvider.TABLE_NAME + ", " +
                MappContract.Service.TABLE_NAME + ", " +
                MappContract.ServicePoint.TABLE_NAME + ", " +
                MappContract.ServProvHasServPt.TABLE_NAME + " where ";
    }

}
