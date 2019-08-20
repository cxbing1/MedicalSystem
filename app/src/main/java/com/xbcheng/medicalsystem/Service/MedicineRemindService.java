package com.xbcheng.medicalsystem.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.xbcheng.medicalsystem.ChartActivity;
import com.xbcheng.medicalsystem.MainActivity;
import com.xbcheng.medicalsystem.R;
import com.xbcheng.medicalsystem.WelcomeActivity;

import java.sql.SQLOutput;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MedicineRemindService extends Service {

    static Timer timer = null;

    //@androidx.annotation.Nullable

    public static void cleanAllnNotification(){
        NotificationManager notificationManager = (NotificationManager) WelcomeActivity
                .getContext().getSystemService(NOTIFICATION_SERVICE);

        notificationManager.cancelAll();

        if(timer!=null){
            timer.cancel();
            timer = null;

        }
    }

    public static void addNotifition(Date date,String tickerText,String contentTitle,String contentText){
        if(date.getTime()-System.currentTimeMillis()<=0){
            return;
        }
        Intent intent = new Intent(WelcomeActivity.getContext(),MedicineRemindService.class);
        intent.putExtra("delayTime",date.getTime()-System.currentTimeMillis());
        intent.putExtra("tickerText",tickerText);
        intent.putExtra("contentTitle",contentTitle);
        intent.putExtra("contentText",contentText);

        WelcomeActivity.getContext().startService(intent);
        System.out.println("添加提醒服务..."+(date.getTime()-System.currentTimeMillis()));

    }

    @Override
    public int onStartCommand(final Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        long delayTime = intent.getLongExtra("delayTime",0);
        System.out.println("提醒时间"+delayTime);
        if(timer==null){
            timer = new Timer();
        }

        timer.schedule(new TimerTask(){
            public void run(){
                System.out.println("Time's up!");
            }
        }, 5*1000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NotificationManager nm = (NotificationManager) MedicineRemindService.this.getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(MedicineRemindService.this);
                String contentTitle = intent.getStringExtra("contentTitle");
                Intent notifitionIntent = new Intent(MedicineRemindService.this,MainActivity.class);
                if(contentTitle.equals("记录提醒")){
                    notifitionIntent = new Intent(MedicineRemindService.this, ChartActivity.class);
                }
                PendingIntent contentIntent = PendingIntent.getActivity(MedicineRemindService.this,1,
                        notifitionIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(contentIntent)
                        .setTicker(intent.getStringExtra("tickerText"))
                        .setSmallIcon(R.drawable.ic_menu_slideshow)
                        .setContentTitle(intent.getStringExtra("contentTitle"))
                        .setContentText(intent.getStringExtra("contentText"))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setVibrate(new long[] { 0, 2000, 1000, 4000 });

                Notification notification = builder.build();
                nm.notify((int)System.currentTimeMillis(),notification);

                System.out.println("通知栏该显示了！");

            }
        },delayTime);


        System.out.println("添加提醒服务...");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("addNotification===========create=======");
        Log.e("addNotification", "===========create=======");
    }

    @Override
    public void onStart(Intent intent,int startId){
        System.out.println("addNotification===========create=======");
    }


    @Override
    public void onDestroy() {
        Log.e("addNotification", "===========destroy=======");
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
