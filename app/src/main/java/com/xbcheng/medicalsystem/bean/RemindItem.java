package com.xbcheng.medicalsystem.bean;

import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RemindItem {

    Date date;//服药时间

    private List<SubRemindItem> subRemindItems;//时间下的服药信息集合

    public RemindItem(){};

    public RemindItem(Date date, List<SubRemindItem> subRemindItems) {
        this.date = date;
        this.subRemindItems = subRemindItems;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<SubRemindItem> getSubRemindItems() {
        return subRemindItems;
    }

    public void setSubRemindItems(List<SubRemindItem> subRemindItems) {
        this.subRemindItems = subRemindItems;
    }

    public String getFormatDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm EE");
        return sdf.format(date);
    }
}
