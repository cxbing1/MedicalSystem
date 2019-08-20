package com.xbcheng.medicalsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.reflect.TypeToken;
import com.xbcheng.medicalsystem.Util.JsonUtil;
import com.xbcheng.medicalsystem.Util.PostRequest;
import com.xbcheng.medicalsystem.bean.Message;
import com.xbcheng.medicalsystem.bean.User;


import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private ListView listView;
    private PostRequest postRequest;
    private User user;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        user = JsonUtil.getLocalUser(MessageActivity.this);
        listView = findViewById(R.id.lv_message);

        if(messageList==null){
            messageList = new ArrayList<>();
        }

        final JSONObject jsonObject = new JSONObject();
        if(user!=null){
            jsonObject.put("userId",user.getId());

        }
        postRequest = new PostRequest();
        postRequest.iniRequest("/android/getMessageList",jsonObject);
        postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
            @Override
            public void onReceiveData(String strResult, int StatusCode) {
                if(StatusCode==200){
                    JSONObject resultJsonObject = JSON.parseObject(strResult);
                    System.out.println("strResult"+strResult);
                    System.out.println("msg"+resultJsonObject.getString("msg"));
                    if(resultJsonObject.getInteger("code")==0){
                        String messagesJson = resultJsonObject.getString("msg");
                        if(messagesJson!=null){
                            messageList = JSONObject.parseObject(messagesJson,new TypeReference<List<Message>>(){});
                            listView.setAdapter(new ArrayAdapter<Message>(MessageActivity.this,android.R.layout.simple_list_item_1,messageList));

                        }

                    }
                }else{
                    Toast.makeText(MessageActivity.this,"请求错误"+StatusCode,Toast.LENGTH_SHORT).show();
                }

            }
        });
        postRequest.execute();
        listView.setEmptyView(findViewById(R.id.tv_message_empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Message message = messageList.get(position);
                JSONObject contentJsonObject = null;
                try{
                    contentJsonObject = JSON.parseObject(message.getContent());
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

                if(contentJsonObject!=null&&contentJsonObject.getInteger("code")!=null){
                    if(contentJsonObject.getInteger("code")==-2){
                        if(message.getHasRead()==0){
                            new AlertDialog.Builder(MessageActivity.this)
                                    .setTitle("关联请求")
                                    .setNegativeButton("取消",null)
                                    .setPositiveButton("通过", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            jsonObject.put("relativeId",message.getFromId());
                                            jsonObject.put("messageId",message.getId());

                                            postRequest.iniRequest("/android/agreeBecomeRelative",jsonObject);
                                            postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
                                                @Override
                                                public void onReceiveData(String strResult, int StatusCode) {
                                                    if(StatusCode==200){
                                                        JSONObject resultJsonObject = JSON.parseObject(strResult);
                                                        if(resultJsonObject.getInteger("code")==0){
                                                            message.setHasRead(1);
                                                        }
                                                        Toast.makeText(MessageActivity.this,resultJsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(MessageActivity.this,"请求错误"+StatusCode,Toast.LENGTH_SHORT);
                                                    }

                                                }
                                            });
                                            postRequest.execute();

                                        }
                                    }).show();
                        }else if(message.getHasRead()==1){
                            new AlertDialog.Builder(MessageActivity.this)
                                    .setTitle("已通过")
                                    .setPositiveButton("确定",null)
                                    .setNegativeButton("取消",null).show();
                        }

                    }else if((contentJsonObject.getInteger("code")==-3)){
                        Intent intent = new Intent(MessageActivity.this,ChartActivity.class);
                        intent.putExtra("message",JSONObject.toJSONString(message));
                        startActivity(intent);
                    }
                }


            }
        });


    }
}
