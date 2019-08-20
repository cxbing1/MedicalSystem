package com.xbcheng.medicalsystem.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.reflect.TypeToken;
import com.xbcheng.medicalsystem.Service.MedicineRemindService;
import com.xbcheng.medicalsystem.bean.BloodPressSugar;
import com.xbcheng.medicalsystem.bean.Message;
import com.xbcheng.medicalsystem.bean.RemindItem;
import com.xbcheng.medicalsystem.bean.SubRemindItem;
import com.xbcheng.medicalsystem.bean.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


public class JsonUtil  {

    private static User user;
    private static Message message;

    public static User getLocalUser(Context context){
            User localUser = null;
            String userJson = context.getSharedPreferences("USER_IMFORMATION", Context.MODE_PRIVATE).getString("user",null);
            if(userJson!=null){
                localUser = JSONObject.parseObject(userJson,User.class);
            }
        return localUser;
    }

    public static void reSetRemindNotifition(Context context){
        User localUser = getLocalUser(context);
        SharedPreferences sp = context.getSharedPreferences("LIST_JSON_"+(localUser==null?"defalt":localUser.getId()), Context.MODE_PRIVATE);
        String remindItemsJson = sp.getString("remindItemsJson",null);
        List<RemindItem> remindItems = remindItemsJson==null?new ArrayList<RemindItem>():JSONObject.parseObject(remindItemsJson,new TypeReference<List<RemindItem>>(){});
        MedicineRemindService.cleanAllnNotification();
        for(RemindItem remindItem : remindItems){
            if((remindItem.getDate().getTime()-System.currentTimeMillis())/(24*60*60*1000)==0){
                StringBuffer contentText = new StringBuffer("请服药：");
                for(SubRemindItem subRemindItem : remindItem.getSubRemindItems()){
                    contentText.append(subRemindItem.getName()+":"+subRemindItem.getDose()+"、");
                }
                MedicineRemindService.addNotifition(remindItem.getDate(),"来自服药提醒的通知",
                        "该吃药啦！",contentText.toString());
            }

        }

        String bloodPressSugarsJson = sp.getString("bloodPressSugarsJson",null);
        List<BloodPressSugar> bloodPressSugars = null;
        if(bloodPressSugarsJson==null){
            MedicineRemindService.addNotifition(new Date(System.currentTimeMillis()+10*1000),"来自服药提醒的通知",
                    "记录提醒","请及时记录今日血压血糖数据");
        }else{
            bloodPressSugars = JSONObject.parseObject(bloodPressSugarsJson,new TypeReference<List<BloodPressSugar>>(){});
            if((System.currentTimeMillis()-bloodPressSugars.get(bloodPressSugars.size()-1).getDate().getTime())/(24*60*60*1000)>0){
                MedicineRemindService.addNotifition(new Date(System.currentTimeMillis()+10*1000),"来自服药提醒的通知",
                        "记录提醒","请及时记录今日血压血糖数据");
            }
        }
    }


    public static User getUserFromServerById(int id) {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("userId",id);

        PostRequest postRequest = new PostRequest();
        postRequest.iniRequest("/android/getUserById",jsonObject);
        postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
            @Override
            public void onReceiveData(String strResult, int StatusCode)  {
                JSONObject resultJsonObject = JSONObject.parseObject(strResult);
                if(resultJsonObject.getInteger("code")==0){
                    String userJson = resultJsonObject.getString("msg");
                    if(userJson!=null){
                        user =  JSONObject.parseObject(userJson,User.class);
                    }

                }


            }
        });
        postRequest.execute();

        return user;
    }

    public static Message getMessageFromServerById(int id){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageId",id);

        PostRequest postRequest = new PostRequest();
        postRequest.iniRequest("/android/getMessageById",jsonObject);
        postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
            @Override
            public void onReceiveData(String strResult, int StatusCode) {
                JSONObject resultJsonObject = null;
                resultJsonObject = JSONObject.parseObject(strResult);
                if(resultJsonObject.getInteger("code")==0){
                    String messageJson = resultJsonObject.getString("msg");
                    if(messageJson!=null){
                        message =  JSONObject.parseObject(messageJson,Message.class);
                    }

                }


            }
        });
        postRequest.execute();

        return message;
    }
}
