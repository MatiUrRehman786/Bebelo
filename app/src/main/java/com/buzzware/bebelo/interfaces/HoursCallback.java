package com.buzzware.bebelo.interfaces;

import com.buzzware.bebelo.Model.HourModel;

public interface HoursCallback {
    void onItemClick(String dayName,int index,String addChildOrRemove);
    void onDataChange(String value,int index,boolean isStart);
}
