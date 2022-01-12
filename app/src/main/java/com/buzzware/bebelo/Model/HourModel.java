package com.buzzware.bebelo.Model;

public class HourModel {

    String dayName="";
    String startTime="";
    String endTime="";
    boolean isChildOfDay=false;

    public HourModel(String dayName, String startTime, String endTime, boolean isChildOfDay) {
        this.dayName = dayName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isChildOfDay = isChildOfDay;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isChildOfDay() {
        return isChildOfDay;
    }

    public void setChildOfDay(boolean childOfDay) {
        isChildOfDay = childOfDay;
    }
}