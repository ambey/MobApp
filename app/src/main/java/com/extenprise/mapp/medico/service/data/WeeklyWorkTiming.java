package com.extenprise.mapp.medico.service.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ambey on 26/2/16.
 */
public class WeeklyWorkTiming {
    private Map<String, WorkTime> workTimeMap;
    private int idServProvHasServPt;

    public WeeklyWorkTiming() {
        workTimeMap = new HashMap<>();
    }

    public Map<String, WorkTime> getWorkTimeMap() {
        return workTimeMap;
    }

    public void setWorkTimeMap(Map<String, WorkTime> workTimeMap) {
        this.workTimeMap = workTimeMap;
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
