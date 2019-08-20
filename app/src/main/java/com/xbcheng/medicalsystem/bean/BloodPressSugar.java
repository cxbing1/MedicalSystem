package com.xbcheng.medicalsystem.bean;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BloodPressSugar {
    private Date date;//记录时间
    private int DBP;//舒张压
    private int SBP;//收缩压
    private float bloodSugar;//血糖

    public BloodPressSugar() {
    }

    public BloodPressSugar(Date date, int DBP, int SBP, float bloodSugar) {
        this.date = date;
        this.DBP = DBP;
        this.SBP = SBP;
        this.bloodSugar = bloodSugar;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDBP() {
        return DBP;
    }

    public void setDBP(int DBP) {
        this.DBP = DBP;
    }

    public int getSBP() {
        return SBP;
    }

    public void setSBP(int SBP) {
        this.SBP = SBP;
    }

    public float getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(float bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getFormatDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}
