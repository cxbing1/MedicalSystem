package com.xbcheng.medicalsystem;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.xbcheng.medicalsystem.Util.JsonUtil;
import com.xbcheng.medicalsystem.Util.PostRequest;
import com.xbcheng.medicalsystem.bean.User;


import java.util.ArrayList;
import java.util.List;

public class RelativeActivity extends AppCompatActivity {

    private ListView listView;
    private PostRequest postRequest;
    private User user;
    private List<User> relativeList;
    FloatingActionButton addRelativeFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relative);
        listView = findViewById(R.id.lv_relative);
        addRelativeFab = findViewById(R.id.bt_add_relative);
        final View viewDialog = getLayoutInflater().inflate(R.layout.dialog_add_relative,null);
        final EditText editRelativeName = viewDialog.findViewById(R.id.et_add_relative);
        user = JsonUtil.getLocalUser(RelativeActivity.this);
        JSONObject jsonObject = new JSONObject();
        postRequest = new PostRequest();
        relativeList = new ArrayList<>();
        jsonObject.put("userId",user.getId());

        postRequest.iniRequest("/android/getRelativeList",jsonObject);
        postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
            @Override
            public void onReceiveData(String strResult, int StatusCode) {
                if(StatusCode==200){
                    JSONObject resultJsonObject = JSON.parseObject(strResult);
                    if(resultJsonObject.getInteger("code")==0){
                        String relativesJson = resultJsonObject.getString("msg");
                        if(relativesJson!=null){
                            relativeList = JSONObject.parseObject(relativesJson,new TypeReference<List<User>>(){});
                            listView.setAdapter(new ArrayAdapter<User>(RelativeActivity.this,android.R.layout.simple_list_item_1,relativeList));
                        }
                    }
                }else{
                    Toast.makeText(RelativeActivity.this,"请求错误"+StatusCode,Toast.LENGTH_SHORT).show();
                }


            }
        });
        postRequest.execute();

        listView.setEmptyView(findViewById(R.id.tv_relative_empty));
        final AlertDialog dialog = new AlertDialog.Builder(RelativeActivity.this)
                .setTitle("关联亲属")
                .setView(viewDialog)
                .setPositiveButton("确定",null)
                .setNegativeButton("取消",null).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String relativeName = editRelativeName.getText().toString().trim();
                        if(relativeName==null){
                            Toast.makeText(RelativeActivity.this,"用户名不能为空！",Toast.LENGTH_LONG).show();
                        }else{
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("relativeName",relativeName);
                            jsonObject.put("userId",user.getId());

                            postRequest.iniRequest("/android/tryBecomeRelative",jsonObject);
                            postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
                                @Override
                                public void onReceiveData(String strResult, int StatusCode) {
                                    if(StatusCode==200){
                                        JSONObject resultJsonObject = JSON.parseObject(strResult);
                                        if(resultJsonObject.getInteger("code")==1){
                                            Toast.makeText(RelativeActivity.this,resultJsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                                        }else if(resultJsonObject.getInteger("code")==0){
                                            Toast.makeText(RelativeActivity.this,resultJsonObject.getString("msg"),Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }else{
                                        Toast.makeText(RelativeActivity.this,"请求错误"+StatusCode,Toast.LENGTH_SHORT);
                                    }


                                }
                            });
                            postRequest.execute();
                        }

                    }
                });
            }
        });
        addRelativeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }



}
