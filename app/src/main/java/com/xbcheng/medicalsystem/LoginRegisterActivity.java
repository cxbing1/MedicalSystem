package com.xbcheng.medicalsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xbcheng.medicalsystem.Util.JsonUtil;
import com.xbcheng.medicalsystem.Util.PostRequest;
import com.xbcheng.medicalsystem.bean.User;



public class LoginRegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPassword;

    private Button btLogin;
    private Button btRegister;

    private PostRequest postRequest;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        etName = findViewById(R.id.et_name);
        etPassword = findViewById(R.id.et_password);

        btLogin = findViewById(R.id.bt_login);
        btRegister = findViewById(R.id.bt_register);
        postRequest = new PostRequest();
        sharedPreferences = getSharedPreferences("USER_IMFORMATION",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //setResult(3);

        /*String userJson = sharedPreferences.getString("user",null);
        if(userJson!=null){
            Intent intent = new Intent(LoginRegisterActivity.this,MainActivity.class);
            startActivity(intent);
        }*/

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etName.getText())||TextUtils.isEmpty(etPassword.getText())){
                    Toast.makeText(LoginRegisterActivity.this,"用户名或密码为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username",etName.getText().toString());
                jsonObject.put("password",etPassword.getText().toString());



                postRequest.iniRequest("/android/login",jsonObject);
                postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
                    @Override
                    public void onReceiveData(String strResult, int StatusCode)  {
                        if(StatusCode==200){
                            JSONObject resultJSONObject = JSON.parseObject(strResult);
                            if(resultJSONObject.getInteger("code")==1){
                                Toast toast = Toast.makeText(LoginRegisterActivity.this,resultJSONObject.getString("msg"),Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }else if(resultJSONObject.getInteger("code")==0){
                                editor.putString("user",resultJSONObject.get("msg").toString());
                                editor.commit();
                                JsonUtil.reSetRemindNotifition(LoginRegisterActivity.this);
                                Intent intent = new Intent(LoginRegisterActivity.this,MainActivity.class);
                                intent.putExtra("fromLoginMsg","登录成功");
                                startActivity(intent);
                                //setResult(1);
                            }
                        }else{
                            Toast.makeText(LoginRegisterActivity.this,"请求错误"+StatusCode,Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                postRequest.execute();

            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etName.getText())||TextUtils.isEmpty(etPassword.getText())){
                    Toast.makeText(LoginRegisterActivity.this,"用户名或密码为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username",etName.getText().toString());
                jsonObject.put("password",etPassword.getText().toString());


                postRequest.iniRequest("/android/regiser",jsonObject);
                postRequest.setOnReceiveDataListener(new PostRequest.OnReceiveDataListener() {
                    @Override
                    public void onReceiveData(String strResult, int StatusCode) {
                        if(StatusCode==200){
                            JSONObject resultJSONObject = JSON.parseObject(strResult);
                            System.out.println("strResult:"+strResult+resultJSONObject.getInteger("code"));
                            if(resultJSONObject.getInteger("code")==1){
                                Toast toast = Toast.makeText(LoginRegisterActivity.this,resultJSONObject.getString("msg"),Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER,0,0);
                                toast.show();
                            }else if(resultJSONObject.getInteger("code")==0){
                                editor.putString("user",resultJSONObject.get("msg").toString());
                                editor.commit();
                                Intent intent = new Intent(LoginRegisterActivity.this,MainActivity.class);
                                intent.putExtra("fromLoginMsg","注册成功");
                                startActivity(intent);
                                //setResult(2);
                                //LoginRegisterActivity.this.finish();
                            }
                        }else{
                            Toast.makeText(LoginRegisterActivity.this,"请求错误"+StatusCode,Toast.LENGTH_SHORT).show();
                        }



                    }
                });

                postRequest.execute();
            }
        });
    }


}
