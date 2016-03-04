package com.extenprise.mapp.medico.service.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ambey on 26/2/16.
 */
public class WeeklyWorkTiming implements Parcelable {
    public static final Creator<WeeklyWorkTiming> CREATOR = new Creator<WeeklyWorkTiming>() {
        @Override
        public WeeklyWorkTiming createFromParcel(Parcel in) {
            return new WeeklyWorkTiming(in);
        }

        @Override
        public WeeklyWorkTiming[] newArray(int size) {
            return new WeeklyWorkTiming[size];
        }
    };
    private Map<String, WorkTime> workTimeMap;
    private int idServProvHasServPt;

    public WeeklyWorkTiming() {
        workTimeMap = new HashMap<>();
    }

    protected WeeklyWorkTiming(Parcel in) {
        idServProvHasServPt = in.readInt();
        ArrayList<WorkTime> workTimes = in.readArrayList(ClassLoader.getSystemClassLoader());
        for (WorkTime workTime : workTimes) {
            workTimeMap.put(workTime.getWeekDay(), workTime);
        }
    }

    public Map<String, WorkTime> getWorkTimeMap() {
        return workTimeMap;
    }

    public void setWorkTimeMap(Map<String, WorkTime> workTimeMap) {
        this.workTimeMap = workTimeMap;
    }

    public boolean isValidWeeklyTime() {
        for (WorkTime time : workTimeMap.values()) {
            if (!time.isValidWorkTime()) {
                return false;
            }
        }
        return true;
    }

    public int getIdServProvHasServPt() {
        return idServProvHasServPt;
    }

    public void setIdServProvHasServPt(int idServProvHasServPt) {
        this.idServProvHasServPt = idServProvHasServPt;
    }

    public boolean addWorkTime(String day) {
        if (workTimeMap.get(day) == null) {
            workTimeMap.put(day, new WorkTime(day));
            return true;
        }
        return false;
    }

    public void removeWorkTime(String day) {
        workTimeMap.remove(day);
    }

    public WorkTime getWorkTime(String day) {
        return workTimeMap.get(day);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idServProvHasServPt);
        ArrayList<WorkTime> workTimeList = new ArrayList<>();
        workTimeList.addAll(workTimeMap.values());
        dest.writeList(workTimeList);
    }

    public class WorkTime {
        private String weekDay;
        private String from;
        private String to;
        private String from2;
        private String to2;

        private WorkTime(String weekDay) {
            this.weekDay = weekDay;
            from = "";
            to = "";
        }

        public boolean isValidWorkTime() {
            if ((from.equals("") || to.equals("")) &&
                    (from2.equals("") || to2.equals(""))) {
                return false;
            }
            if (!from.equals("")) {
                SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
                sdf.applyPattern("hh:mm");

                try {
                    Date fromTime = sdf.parse(from);
                    Date toTime = sdf.parse(to);
                    if (toTime.compareTo(fromTime) <= 0) {
                        return false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!from2.equals("")) {
                SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
                sdf.applyPattern("hh:mm");

                try {
                    Date fromTime = sdf.parse(from2);
                    Date toTime = sdf.parse(to2);
                    if (toTime.compareTo(fromTime) <= 0) {
                        return false;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getFrom2() {
            return from2;
        }

        public void setFrom2(String from2) {
            this.from2 = from2;
        }

        public String getTo2() {
            return to2;
        }

        public void setTo2(String to2) {
            this.to2 = to2;
        }

        public String getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(String weekDay) {
            this.weekDay = weekDay;
        }
    }
}
