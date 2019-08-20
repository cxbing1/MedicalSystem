package com.xbcheng.medicalsystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.xbcheng.medicalsystem.Adapter.RemindItemAdapter;
import com.xbcheng.medicalsystem.Service.MedicineRemindService;
import com.xbcheng.medicalsystem.Util.JsonUtil;
import com.xbcheng.medicalsystem.bean.RemindItem;
import com.xbcheng.medicalsystem.bean.SubRemindItem;
import com.xbcheng.medicalsystem.bean.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static Context mContext = null;
    ExpandableListView expandableListView;
    RemindItemAdapter remindItemAdapter;
    TextView tvUserName;
    ImageView ivHeadImage;
    List<RemindItem> remindItems;
    List<RemindItem> todayRemindItems;
    User user;

    public static Context getContext(){
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this,AddItemActivity.class);
                startActivityForResult(intent,0);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        tvUserName =navigationView.getHeaderView(0).findViewById(R.id.tv_user_name);
        ivHeadImage = navigationView.getHeaderView(0).findViewById(R.id.iv_head_image);
        ////
        expandableListView = findViewById(R.id.remind_listview);
        expandableListView.setEmptyView(findViewById(R.id.tv_empty));
        mContext = this;

        init(savedInstanceState);

    }

    public void init(Bundle savedInstanceState){
        /*String userJson = getSharedPreferences("USER_IMFORMATION",Context.MODE_PRIVATE).getString("user",null);
        if(userJson!=null){
            user = JSONObject.parseObject(userJson,User.class);
        }else{
            startActivityForResult(new Intent(MainActivity.this,LoginRegisterActivity.class),1);
        }*/
        if(getIntent().getStringExtra("fromLoginMsg")!=null){
            Toast.makeText(MainActivity.this,getIntent().getStringExtra("fromLoginMsg"),Toast.LENGTH_SHORT);
        }
        user = JsonUtil.getLocalUser(this);
        String remindItemsJson = null;
        if(savedInstanceState==null){
            SharedPreferences sp = getSharedPreferences("LIST_JSON_"+(user==null?"defalt":user.getId()),Context.MODE_PRIVATE);
            remindItemsJson = sp.getString("remindItemsJson",null);
            System.out.println("从SharedPreferences取出的remindItemsJson为："+remindItemsJson);
        }else{
            remindItemsJson = savedInstanceState.getString("remindItemsJson");
            System.out.println("从savedInstanceState取出的remindItemsJson为："+remindItemsJson);
        }

        if(remindItemsJson!=null){
            remindItems = JSONObject.parseObject(remindItemsJson,new TypeReference<List<RemindItem>>(){});
        }


        if(remindItems==null){
            remindItems = new ArrayList<>();
        }


        if(todayRemindItems==null||todayRemindItems.size()==0){
            todayRemindItems = new ArrayList<>();
            for(RemindItem remindItem : remindItems){
                if((remindItem.getDate().getTime()-System.currentTimeMillis())/(24*60*60*1000)==0){
                    todayRemindItems.add(remindItem);
                }
            }

            Collections.sort(todayRemindItems, new Comparator<RemindItem>() {
                @Override
                public int compare(RemindItem o1, RemindItem o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
        }

        System.out.println("todayRemindItems:"+todayRemindItems);



        remindItemAdapter = new RemindItemAdapter(this,todayRemindItems);
        expandableListView.setAdapter(remindItemAdapter);
        for(int i=0;i<todayRemindItems.size();i++){
            int j=0;
            for(SubRemindItem subRemindItem : todayRemindItems.get(i).getSubRemindItems()){
                if(!subRemindItem.isTake()){
                    break;
                }
                j++;
            }
            if(j<todayRemindItems.get(i).getSubRemindItems().size()){
                expandableListView.expandGroup(i);
            }
        }


        if(user!=null){
            tvUserName.setText(user.getName());
            Glide.with(this).load("http://10.147.171.166:8081/wenwen/images/res/"+user.getHeadUrl()).into(ivHeadImage);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("remindItemsJson",JSONObject.toJSONString(remindItems));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("activity 销毁:"+JSONObject.toJSONString(remindItems));
        if(remindItems!=null&&remindItems.size()>0){
            SharedPreferences.Editor editor = getSharedPreferences("LIST_JSON_"+(user==null?"defalt":user.getId()),Context.MODE_PRIVATE).edit();
            editor.putString("remindItemsJson",JSONObject.toJSONString(remindItems));
            editor.commit();
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode==0){
            if(data==null){
                return;
            }
        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();

                Bundle resultDate = data.getExtras();

                String medicineName = resultDate.getString("medicineName");
                String interval = resultDate.getString("interval");
                String beginTime = resultDate.getString("beginTime");
                String continueTime = resultDate.getString("continueTime");
                String perDayTime = resultDate.getString("perDayTime");
                String perTimeDose = resultDate.getString("perTimeDose");

                if(remindItems==null){
                    remindItems = new ArrayList<>();
                }
                System.out.println("返回信息"+medicineName+interval+beginTime+continueTime+perDayTime+perTimeDose);
                String[] perDayTimes = perDayTime.split(";");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = null;
                try {
                        date = sdf1.parse(beginTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for(int i=0;i<Integer.parseInt(continueTime);i=i+1+Integer.parseInt(interval)){

                    for(int j=0;j<perDayTimes.length;j++){
                        Date dateTime = null;
                        try {
                            dateTime = sdf2.parse(sdf1.format(date)+" "+perDayTimes[j]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        int k=0;
                        for(k = 0;k<remindItems.size();k++){
                            if(remindItems.get(k).getDate().equals(dateTime)){
                                break;
                            }
                        }

                        if(k>=remindItems.size()){
                            RemindItem remindItem = new RemindItem();
                            List<SubRemindItem> subRemindItemList = new ArrayList<>();
                            remindItem.setDate(dateTime);
                            subRemindItemList.add(new SubRemindItem(false,medicineName,perTimeDose));
                            remindItem.setSubRemindItems(subRemindItemList);
                            remindItems.add(remindItem);
                            if((dateTime.getTime()-System.currentTimeMillis())/(24*60*60*1000)==0){
                                todayRemindItems.add(remindItem);
                            }
                        }else{
                            remindItems.get(k).getSubRemindItems().add(new SubRemindItem(false,medicineName,perTimeDose));
                        }


                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(calendar.DATE,Integer.parseInt(interval)+1);
                    date = calendar.getTime();


                }

                remindItemAdapter.notifyDataSetChanged();
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

            }

            /*if(requestCode==1||requestCode==2){

                if(resultCode==3){
                    MainActivity.this.finish();
                }else{
                    init(null);
                    if(resultCode==1){
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    }

                    if(resultCode==2){
                        Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }
                }




            }*/



    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this,RelativeActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(MainActivity.this,ChartActivity.class);
            intent.putExtra("user",JSONObject.toJSONString(user));
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MainActivity.this,MessageActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            SharedPreferences.Editor editor = getSharedPreferences("USER_IMFORMATION",Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
            if(remindItems!=null&&remindItems.size()>0){
                SharedPreferences.Editor listEditor = getSharedPreferences("LIST_JSON_"+(user==null?"defalt":user.getId()),Context.MODE_PRIVATE).edit();
                listEditor.putString("remindItemsJson",JSONObject.toJSONString(remindItems));
                listEditor.commit();
            }
            remindItems=null;
            todayRemindItems=null;
            //startActivityForResult(new Intent(MainActivity.this,LoginRegisterActivity.class),2);
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
