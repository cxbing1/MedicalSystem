package com.xbcheng.medicalsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.reflect.TypeToken;
import com.xbcheng.medicalsystem.Util.PostRequest;
import com.xbcheng.medicalsystem.bean.BloodPressSugar;
import com.xbcheng.medicalsystem.bean.Message;
import com.xbcheng.medicalsystem.bean.RemindItem;
import com.xbcheng.medicalsystem.bean.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartActivity extends AppCompatActivity {

    private LineChartView bloodPressLineChartView;
    private LineChartView bloodSugarLineChartView;
    private LineChartData bloodPressLineChartData;
    private LineChartData bloodSugarLineChartData;
    private RelativeLayout relativeLayout;

    private EditText todaySBPet;
    private EditText todayDBPet;

    private EditText todayBloodSugarEt;

    private TextView messageTv;

    Button saveTodayDataBt;

    private List<BloodPressSugar> bloodPressSugars;

    User user;

    private boolean hasAxes = true;       //是否有轴，x和y轴
    private boolean hasAxesNames = true;   //是否有轴的名字
    private boolean hasLines = true;       //是否有线（点和点连接的线）
    private boolean hasPoints = true;       //是否有点（每个值的点）
    private ValueShape shape = ValueShape.CIRCLE;    //点显示的形式，圆形，正方向，菱形
    private boolean isFilled = false;                //是否是填充
    private boolean hasLabels = false;               //每个点是否有名字
    private boolean isCubic = false;                 //是否是立方的，线条是直线还是弧线
    private boolean hasLabelForSelected = false;       //每个点是否可以选择（点击效果）
    private boolean pointsHaveDifferentColor;           //线条的颜色变换
    private boolean hasGradientToTransparent = false;      //是否有梯度的透明


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        bloodPressLineChartView = findViewById(R.id.blood_press_line_chart);
        bloodSugarLineChartView = findViewById(R.id.blood_sugar_line_chart);
        todaySBPet = findViewById(R.id.et_today_sbp);
        todayDBPet = findViewById(R.id.et_today_dbp);
        todayBloodSugarEt = findViewById(R.id.et_today_blood_sugar);
        saveTodayDataBt = findViewById(R.id.bt_save_today_blood_data);
        relativeLayout = findViewById(R.id.rl_today_date);
        messageTv = findViewById(R.id.tv_message);



        String userJson = getSharedPreferences("USER_IMFORMATION",Context.MODE_PRIVATE).getString("user",null);
        if(userJson!=null){
            user = JSONObject.parseObject(userJson,User.class);
        }else{
            Intent intent = new Intent(ChartActivity.this,LoginRegisterActivity.class);
            startActivity(intent);
        }

        String bloodPressSugarsJson = null;
        String messageJson = getIntent().getStringExtra("message");
        if(messageJson!=null){
            Message message = JSONObject.parseObject(messageJson,Message.class);
            JSONObject contentJsonObject = JSON.parseObject(message.getContent());
            if(contentJsonObject.getString("data")!=null){
                bloodPressSugarsJson = contentJsonObject.getString("data");
                System.out.println("从message取出的bloodPressSugarsJson为："+bloodPressSugarsJson);
            }

            relativeLayout.setVisibility(View.INVISIBLE);
            messageTv.setVisibility(View.VISIBLE);
            messageTv.setText(contentJsonObject.getString("msg"));


        }else if(savedInstanceState==null){
            SharedPreferences sp = getSharedPreferences("LIST_JSON_"+(user==null?"defalt":user.getId()),Context.MODE_PRIVATE);
            bloodPressSugarsJson = sp.getString("bloodPressSugarsJson",null);
            System.out.println("从SharedPreferences取出的bloodPressSugarsJson为："+bloodPressSugarsJson);
        }else{
            bloodPressSugarsJson = savedInstanceState.getString("bloodPressSugarsJson");
            System.out.println("从savedInstanceState取出的bloodPressSugarsJson为："+bloodPressSugarsJson);
        }

        if(bloodPressSugarsJson!=null){
            bloodPressSugars = JSONObject.parseObject(bloodPressSugarsJson,new TypeReference<List<BloodPressSugar>>(){});
        }

        if(bloodPressSugars==null){
            bloodPressSugars = new ArrayList<>();
        }
        saveTodayDataBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(todaySBPet.getText())||TextUtils.isEmpty(todayDBPet.getText())
                        ||TextUtils.isEmpty(todayBloodSugarEt.getText())){
                    Toast.makeText(ChartActivity.this,"请将信息填写完整！",Toast.LENGTH_SHORT).show();
                    return;

                }

                int DBP = Integer.parseInt(todayDBPet.getText().toString());
                int SBP = Integer.parseInt(todaySBPet.getText().toString());
                float bloodSugar = Float.parseFloat(todayBloodSugarEt.getText().toString());
                /*if((bloodPressSugars.get(bloodPressSugars.size()-1).getDate().getTime()-System.currentTimeMillis())/(24*60*60*1000)==0){
                    bloodPressSugars.remove(bloodPressSugars.size()-1);
                }*/
                bloodPressSugars.add(new BloodPressSugar(new Date(),DBP,SBP,bloodSugar));
                System.out.println(bloodPressSugars);
                Toast.makeText(ChartActivity.this,"记录成功！",Toast.LENGTH_SHORT).show();
                if(DBP>=90||SBP>=140||bloodSugar>=6.1){
                    JSONObject jsonObject  = new JSONObject();
                    jsonObject.put("userId",user.getId());
                    jsonObject.put("data",JSONObject.toJSONString(bloodPressSugars));
                    PostRequest postRequest = new PostRequest();
                    postRequest.iniRequest("/android/sendRemindMessage",jsonObject);
                    postRequest.execute();
                }

                Intent intent = new Intent(ChartActivity.this,ChartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });


        List<AxisValue> mAxisXValues = new ArrayList<>();
        List<AxisValue> mAxisYValues = new ArrayList<>();

        List<AxisValue> mAxisYValuesOfSugar = new ArrayList<>();

        //
        for(int i=0;i<bloodPressSugars.size();i++){
            mAxisXValues.add(new AxisValue(i*(12/bloodPressSugars.size()))
                    .setLabel(bloodPressSugars.get(i).getFormatDate()));
        }

        for (int i = 0; i < 150; i++) {
            mAxisYValues.add(new AxisValue(i).setLabel(i+""));
        }

        for(int i=0;i<10;i++){
            mAxisYValuesOfSugar.add(new AxisValue(i).setLabel(i+""));
        }


        //
        List<Line> pressLines = new ArrayList<>();
        List<Line> sugarLines = new ArrayList<>();
        for(int i=0;i<3;i++ ){
            List<PointValue> pointValues = new ArrayList<>();
            Line line = new Line(pointValues);
            line.setStrokeWidth(1);
            //line.setHasLabelsOnlyForSelected(true);
            line.setHasLabels(true);
            line.setCubic(true);
            for(int j=0;j<bloodPressSugars.size();j++){
                if(i==0){
                    pointValues.add(new PointValue((12/bloodPressSugars.size())*j,bloodPressSugars.get(j).getDBP()));
                    line.setColor(Color.RED);
                }else if(i==1){
                    pointValues.add(new PointValue((12/bloodPressSugars.size())*j,bloodPressSugars.get(j).getSBP()));
                    line.setColor(Color.BLUE);
                }else if(i==2){
                    pointValues.add(new PointValue((12/bloodPressSugars.size())*j,bloodPressSugars.get(j).getBloodSugar()).setLabel(String.valueOf(bloodPressSugars.get(j).getBloodSugar())));
                    line.setColor(Color.GREEN);
                }

            }

            if(i<2){
                pressLines.add(line);
            }else{
                sugarLines.add(line);
            }

        }
        bloodPressLineChartData = new LineChartData(pressLines);
        bloodPressLineChartView.setLineChartData(bloodPressLineChartData);

        bloodSugarLineChartData = new LineChartData(sugarLines);
        bloodSugarLineChartView.setLineChartData(bloodSugarLineChartData);



        //
        if(hasAxes){
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            Axis axisYOfSugar = new Axis().setHasLines(true);

            if(hasAxesNames){
                axisX.setName("时间");
                axisY.setName("血压(mmHg)");
                axisYOfSugar.setName("血糖(mmol/L)");
            }

            axisX.setTextSize(8);
            axisX.setTextColor(Color.BLACK);
            axisX.setHasTiltedLabels(true);
            axisX.setHasLines(true);
            axisX.setValues(mAxisXValues);

            axisY.setTextSize(10);
            axisY.setTextColor(Color.RED);
            axisY.setValues(mAxisYValues);


            axisYOfSugar.setTextSize(10);
            axisYOfSugar.setTextColor(Color.RED);
            axisYOfSugar.setValues(mAxisYValuesOfSugar);

            bloodPressLineChartData.setAxisXBottom(axisX);
            bloodPressLineChartData.setAxisYLeft(axisY);

            bloodSugarLineChartData.setAxisXBottom(axisX);
            bloodSugarLineChartData.setAxisYLeft(axisYOfSugar);

        }


    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("bloodPressSugarsJson",JSONObject.toJSONString(bloodPressSugars));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.out.println("activity 销毁:"+JSONObject.toJSONString(bloodPressSugars));
        if(getIntent().getStringExtra("message")==null&&bloodPressSugars!=null&&bloodPressSugars.size()>0){
            SharedPreferences.Editor editor = getSharedPreferences("LIST_JSON_"+(user==null?"defalt":user.getId()), Context.MODE_PRIVATE).edit();
            editor.putString("bloodPressSugarsJson",JSONObject.toJSONString(bloodPressSugars));
            editor.commit();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("activity 暂停:"+JSONObject.toJSONString(bloodPressSugars));
        if(getIntent().getStringExtra("message")==null&&bloodPressSugars!=null&&bloodPressSugars.size()>0){
            SharedPreferences.Editor editor = getSharedPreferences("LIST_JSON_"+(user==null?"defalt":user.getId()), Context.MODE_PRIVATE).edit();
            editor.putString("bloodPressSugarsJson",JSONObject.toJSONString(bloodPressSugars));
            editor.commit();
        }


    }
}
