package com.xbcheng.medicalsystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    private EditText medicineName;
    private EditText interval;
    private EditText beginTime;
    private EditText continueTime;
    private EditText perDayTime;
    private EditText perTimeDose;
    private Button addCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        medicineName = findViewById(R.id.et_add_medicine_name);
        interval = findViewById(R.id.et_interval);
        beginTime = findViewById(R.id.et_begin_time);
        continueTime = findViewById(R.id.et_continue_time);
        perDayTime = findViewById(R.id.et_perday_time);
        perTimeDose = findViewById(R.id.et_pertime_dose);
        addCompleted = findViewById(R.id.bt_add_complete);

        perDayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] times = new String[24];

                for(int i=0;i<24;i++){
                    if(i<10){
                        times[i] = "0"+i+":00";
                    }else{
                        times[i] = i+":00";
                    }
                }

                final boolean[] selected = new boolean[24];

                AlertDialog dialog = new AlertDialog.Builder(AddItemActivity.this).setTitle("请选择服药时间")
                                            .setNegativeButton("取消",null)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    StringBuffer sb = new StringBuffer();
                                                    for(int i=0;i<24;i++){
                                                        if(selected[i]){
                                                            sb.append(times[i]+";");
                                                        }
                                                    }

                                                    perDayTime.setText(sb.toString());
                                                }
                                            }).setMultiChoiceItems(times, null, new DialogInterface.OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                    selected[which] = isChecked;
                                                }
                                            }).show();
            }
        });

        addCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(medicineName.getText())||TextUtils.isEmpty(interval.getText())
                    ||TextUtils.isEmpty(beginTime.getText())||TextUtils.isEmpty(continueTime.getText())
                        ||TextUtils.isEmpty(perDayTime.getText())||TextUtils.isEmpty(perTimeDose.getText())){
                    Toast.makeText(AddItemActivity.this,"请将信息填写完整！",Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("medicineName",medicineName.getText().toString());
                bundle.putString("interval",interval.getText().toString());
                bundle.putString("beginTime",beginTime.getText().toString());
                bundle.putString("continueTime",continueTime.getText().toString());
                bundle.putString("perDayTime",perDayTime.getText().toString());
                bundle.putString("perTimeDose",perTimeDose.getText().toString());
                intent.putExtras(bundle);

                AddItemActivity.this.setResult(0,intent);
                AddItemActivity.this.finish();
            }
        });





        beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickDlg();
            }
        });
    }



    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                AddItemActivity.this.beginTime.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }


}
