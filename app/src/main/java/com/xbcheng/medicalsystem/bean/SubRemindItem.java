package com.xbcheng.medicalsystem.bean;

public class SubRemindItem {

    private boolean take;//是否已服用
    private String name;//药品名称
    private String dose;//药品剂量


    public SubRemindItem(){};

    public SubRemindItem(boolean take, String name, String dose) {
        this.take = take;
        this.name = name;
        this.dose = dose;
    }



    public boolean isTake() {
        return take;
    }

    public void setTake(boolean take) {
        this.take = take;
    }

    public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDose() {
            return dose;
        }

        public void setDose(String dose) {
            this.dose = dose;
        }
}
