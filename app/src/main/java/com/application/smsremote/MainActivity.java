package com.application.smsremote;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "statusState";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PHONE_NBR = "PhoneNbr";
    private static final String CMD1 = "Command1";
    private static final String CMD2 = "Command2";
    private static final String CMD3 = "Command3";
    private static final String BTN_LABEL1 = "btnLabe1";
    private static final String BTN_LABEL2 = "btnLabe2";
    private static final String BTN_LABEL3 = "btnLabe3";
    private static final String RESET_CMD = "CommandReset";
    private static final String REFRESH_CMD = "CommandRefresh";

    private static MainActivity instance;

    final Handler handler = new Handler();
    private final static int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    private final static int RECEIVE_SMS_PERMISSION_REQUEST_CODE = 0;
    private ImageButton btn1, btn2, btn3, btn1Clicked, btn2Clicked, btn3Clicked, btnReset, btnRefresh, btnSettings;
    private TextView refreshHour, refreshDate, whiteBtnNbr1, whiteBtnNbr2, whiteBtnNbr3, btnLabel1, btnLabel2, btnLabel3;
    private ImageView imgGreenStatus1, imgGreenStatus2, imgGreenStatus3, imgRedStatus1, imgRedStatus2, imgRedStatus3;
    private String phoneNumber;
    private int timeout = 0;
    private int commandsCount = 0;
    private int actualButton;
    private String[] cmd1Array, cmd2Array, cmd3Array;
    String cmd1;
    String cmd2;
    String cmd3 ;
    String loadBtnName1, loadBtnName2, loadBtnName3;
    private String msgRefresh;
    private String msgReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {


            }
            else {

                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_SMS}, RECEIVE_SMS_PERMISSION_REQUEST_CODE);
            }
        }

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn1Clicked = findViewById(R.id.btn1Clicked);
        btn2Clicked = findViewById(R.id.btn2Clicked);
        btn3Clicked = findViewById(R.id.btn3Clicked);
        btnLabel1 = findViewById(R.id.btnLabel1);
        btnLabel2 = findViewById(R.id.btnLabel2);
        btnLabel3 = findViewById(R.id.btnLabel3);
        whiteBtnNbr1 = findViewById(R.id.whiteBtnNbr1);
        whiteBtnNbr2 = findViewById(R.id.whiteBtnNbr2);
        whiteBtnNbr3 = findViewById(R.id.whiteBtnNbr3);
        btnReset = findViewById(R.id.btnReset);
        btnSettings = findViewById(R.id.btnSettings);
        btnRefresh = findViewById(R.id.btnRefresh);
        refreshHour = findViewById(R.id.refreshHour);
        refreshDate = findViewById(R.id.refreshDate);
        imgGreenStatus1 = findViewById(R.id.imgGreenStatus1);
        imgGreenStatus2 = findViewById(R.id.imgGreenStatus2);
        imgGreenStatus3 = findViewById(R.id.imgGreenStatus3);
        imgRedStatus1 = findViewById(R.id.imgRedStatus1);
        imgRedStatus2 = findViewById(R.id.imgRedStatus2);
        imgRedStatus3 = findViewById(R.id.imgRedStatus3);


        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btnReset.setEnabled(false);
        btnRefresh.setEnabled(false);

        if (checkedPermission(Manifest.permission.SEND_SMS)) {

            btn1.setEnabled(true);
            btn2.setEnabled(true);
            btn3.setEnabled(true);
            btnReset.setEnabled(true);
            btnRefresh.setEnabled(true);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actualButton = 1;

                btn1.setEnabled(false);
                btn2.setEnabled(false);
                btn3.setEnabled(false);

                btn1.setVisibility(View.INVISIBLE);
                whiteBtnNbr1.setVisibility(View.INVISIBLE);
                btn1Clicked.setVisibility(View.VISIBLE);

                //Get cmd to send and create timers

                for(int x = 0; x < cmd1Array.length; x++) {

                    if(x == 0) {

                        sendMessage(phoneNumber, cmd1Array[x]);

                        if(x == (cmd1Array.length - 1)) {

                            timeout = 5000;
                            Timer myTimer2 = new Timer();
                            MyTask2 task2 = new MyTask2();
                            myTimer2.schedule(task2, timeout);
                        }
                    }
                    else if(x % 2 == 0) {

                        Timer myTimer = new Timer();
                        MyTask task = new MyTask();
                        String timeToWait1 = cmd1Array[x-1].trim();
                        timeout = timeout + (Integer.parseInt(timeToWait1) * 1000);
                        myTimer.schedule(task, timeout);

                        if(x == (cmd1Array.length - 1)) {

                            Timer myTimer2 = new Timer();
                            MyTask2 task2 = new MyTask2();
                            myTimer2.schedule(task2, timeout);
                        }
                    }
                    else if(x % 2 != 0 && x == (cmd1Array.length - 1)) {

                        String timeToWait1 = cmd1Array[x].trim();
                        timeout = timeout + (Integer.parseInt(timeToWait1) * 1000);
                        Timer myTimer2 = new Timer();
                        MyTask2 task2 = new MyTask2();
                        myTimer2.schedule(task2, timeout);
                    }
                }
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage(phoneNumber, msgReset);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actualButton = 2;

                btn1.setEnabled(false);
                btn2.setEnabled(false);
                btn3.setEnabled(false);

                btn2.setVisibility(View.INVISIBLE);
                whiteBtnNbr2.setVisibility(View.INVISIBLE);
                btn2Clicked.setVisibility(View.VISIBLE);

                //Get cmd to send and create timers

                for(int x = 0; x < cmd2Array.length; x++) {

                    if(x == 0) {

                        sendMessage(phoneNumber, cmd2Array[x]);

                        if(x == (cmd2Array.length - 1)) {

                            timeout = 5000;
                            Timer myTimer2 = new Timer();
                            MyTask2 task2 = new MyTask2();
                            myTimer2.schedule(task2, timeout);
                        }
                    }
                    else if(x % 2 == 0) {

                        Timer myTimer = new Timer();
                        MyTask task = new MyTask();
                        String timeToWait2 = cmd2Array[x-1].trim();
                        timeout = timeout + (Integer.parseInt(timeToWait2) * 1000);
                        myTimer.schedule(task, timeout);

                        if(x == (cmd2Array.length - 1)) {

                            Timer myTimer2 = new Timer();
                            MyTask2 task2 = new MyTask2();
                            myTimer2.schedule(task2, timeout);
                        }
                    }
                    else if(x % 2 != 0 && x == (cmd2Array.length - 1)) {

                        String timeToWait2 = cmd2Array[x].trim();
                        timeout = timeout + (Integer.parseInt(timeToWait2) * 1000);
                        Timer myTimer2 = new Timer();
                        MyTask2 task2 = new MyTask2();
                        myTimer2.schedule(task2, timeout);
                    }
                }
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actualButton = 3;

                btn1.setEnabled(false);
                btn2.setEnabled(false);
                btn3.setEnabled(false);

                btn3.setVisibility(View.INVISIBLE);
                whiteBtnNbr3.setVisibility(View.INVISIBLE);
                btn3Clicked.setVisibility(View.VISIBLE);

                //Get cmd to send and create timers

                for(int x = 0; x < cmd3Array.length; x++) {

                    if(x == 0) {

                        sendMessage(phoneNumber, cmd3Array[x]);

                        if(x == (cmd3Array.length - 1)) {

                            timeout = 5000;
                            Timer myTimer2 = new Timer();
                            MyTask2 task2 = new MyTask2();
                            myTimer2.schedule(task2, timeout);
                        }
                    }
                    else if(x % 2 == 0) {

                        Timer myTimer = new Timer();
                        MyTask task = new MyTask();
                        String timeToWait3 = cmd3Array[x-1].trim();
                        timeout = timeout + (Integer.parseInt(timeToWait3) * 1000);
                        myTimer.schedule(task, timeout);

                        if(x == (cmd3Array.length - 1)) {

                            Timer myTimer2 = new Timer();
                            MyTask2 task2 = new MyTask2();
                            myTimer2.schedule(task2, timeout);
                        }
                    }
                    else if(x % 2 != 0 && x == (cmd3Array.length - 1)) {

                        String timeToWait3 = cmd3Array[x].trim();
                        timeout = timeout + (Integer.parseInt(timeToWait3) * 1000);
                        Timer myTimer2 = new Timer();
                        MyTask2 task2 = new MyTask2();
                        myTimer2.schedule(task2, timeout);
                    }
                }
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage(phoneNumber, msgRefresh);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        loadStatus();
        loadSettings();
    }

    public static MainActivity getInstance() {

        return instance;
    }

    private boolean checkedPermission(String permission) {

        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    btn1.setEnabled(true);
                    btn2.setEnabled(true);
                    btn3.setEnabled(true);
                    btnReset.setEnabled(true);
                    btnRefresh.setEnabled(true);
                }
                break;
            case RECEIVE_SMS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Toast.makeText(this, "Thank you for permitting !", Toast.LENGTH_LONG).show();
                }
                else {

                    Toast.makeText(this, "Permission error !", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void sendMessage(String phoneNumber, String msg) {

        if (checkedPermission(Manifest.permission.SEND_SMS)) {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, msg, null, null);

        } else {

            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeStatus(int statusNbr, String status) {

        switch (statusNbr) {
            case 1:
                if(status.equals("ON")) {
                    imgGreenStatus1.setVisibility(View.VISIBLE);
                    imgRedStatus1.setVisibility(View.INVISIBLE);
                }
                else if(status.equals("OFF")) {
                    imgGreenStatus1.setVisibility(View.INVISIBLE);
                    imgRedStatus1.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if(status.equals("ON")) {
                    imgGreenStatus2.setVisibility(View.VISIBLE);
                    imgRedStatus2.setVisibility(View.INVISIBLE);
                }
                else if(status.equals("OFF")) {
                    imgGreenStatus2.setVisibility(View.INVISIBLE);
                    imgRedStatus2.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                if(status.equals("ON")) {
                    imgGreenStatus3.setVisibility(View.VISIBLE);
                    imgRedStatus3.setVisibility(View.INVISIBLE);
                }
                else if(status.equals("OFF")) {
                    imgGreenStatus3.setVisibility(View.INVISIBLE);
                    imgRedStatus3.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void saveStatus(String status1, String status2, String status3){

        //Save all status locally

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String text = status1 + "," + status2 + "," + status3 + "," + formatter.format(date);

        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        loadStatus();
    }

    public void loadStatus() {

        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while((text = br.readLine()) != null) {

                sb.append(text).append("\n");
            }

            String[] splitArray = sb.toString().split(",|[ \\n]");

            for(int i = 0; i < splitArray.length; i++) {

                if(i  < 3) {
                    changeStatus(i + 1, splitArray[i]);
                }
            }

            refreshHour.setText(splitArray[4]);
            refreshDate.setText(splitArray[3]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadSettings() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString(PHONE_NBR, "+41 42 414 41 41");
        cmd1 = sharedPreferences.getString(CMD1, "set out1 #1513");
        cmd2 = sharedPreferences.getString(CMD2, "set out2 #1513");
        cmd3 = sharedPreferences.getString(CMD3, "set out3 #1513");
        loadBtnName1 = sharedPreferences.getString(BTN_LABEL1, "Button 1");
        loadBtnName2 = sharedPreferences.getString(BTN_LABEL2, "Button 2");
        loadBtnName3 = sharedPreferences.getString(BTN_LABEL3, "Button 3");
        msgReset = sharedPreferences.getString(RESET_CMD, "reset out1 out2 #1513");
        msgRefresh = sharedPreferences.getString(REFRESH_CMD, "status #1513");

        cmd1Array = cmd1.split("%");
        cmd2Array = cmd2.split("%");
        cmd3Array = cmd3.split("%");

        btnLabel1.setText(loadBtnName1);
        btnLabel2.setText(loadBtnName2);
        btnLabel3.setText(loadBtnName3);
    }

    private class MyTask extends TimerTask {

        public void run()  {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    commandsCount = commandsCount + 2;

                    switch (actualButton) {

                        case 1:
                            sendMessage(phoneNumber, cmd1Array[commandsCount]);
                            break;
                        case 2:
                            sendMessage(phoneNumber, cmd2Array[commandsCount]);
                            break;
                        case 3:
                            sendMessage(phoneNumber, cmd3Array[commandsCount]);
                            break;
                    }
                }
            });
        }
    }

    private class MyTask2 extends TimerTask {

        public void run()  {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    //Active button and change the visibility of the buttons images

                    btn1.setEnabled(true);
                    btn2.setEnabled(true);
                    btn3.setEnabled(true);
                    btn1.setVisibility(View.VISIBLE);
                    whiteBtnNbr1.setVisibility(View.VISIBLE);
                    btn1Clicked.setVisibility(View.INVISIBLE);
                    btn2.setVisibility(View.VISIBLE);
                    whiteBtnNbr2.setVisibility(View.VISIBLE);
                    btn2Clicked.setVisibility(View.INVISIBLE);
                    btn3.setVisibility(View.VISIBLE);
                    whiteBtnNbr3.setVisibility(View.VISIBLE);
                    btn3Clicked.setVisibility(View.INVISIBLE);
                    timeout = 0;
                    commandsCount = 0;
                }
            });
        }
    }
}
