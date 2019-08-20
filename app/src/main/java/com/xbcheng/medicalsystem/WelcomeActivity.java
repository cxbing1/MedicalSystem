package com.xbcheng.medicalsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xbcheng.medicalsystem.Service.MedicineRemindService;
import com.xbcheng.medicalsystem.Util.JsonUtil;
import com.xbcheng.medicalsystem.bean.RemindItem;
import com.xbcheng.medicalsystem.bean.SubRemindItem;
import com.xbcheng.medicalsystem.bean.User;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity {

    private  Handler handler = new Handler();
    private User localUser = null;
    private List<RemindItem> remindItems;
    private static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mContext = this;
        System.out.println("welcome...................");
        localUser = JsonUtil.getLocalUser(this);
        if(localUser==null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this,LoginRegisterActivity.class);
                    startActivity(intent);
                }
            },2000);
        }else{
            JsonUtil.reSetRemindNotifition(this);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            },2000);

        }

    }

    public static Context getContext(){
        return mContext;
    }


}
