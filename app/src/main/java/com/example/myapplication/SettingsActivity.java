package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PHONE_NBR = "PhoneNbr";
    private static final String CMD1 = "Command1";
    private static final String CMD2 = "Command2";
    private static final String CMD3 = "Command3";
    private static final String BTN_NAME1 = "btnName1";
    private static final String BTN_NAME2 = "btnName2";
    private static final String BTN_NAME3 = "btnName3";
    private static final String RESET_CMD = "CommandReset";
    private static final String REFRESH_CMD = "CommandRefresh";

    private Context context;
    Button btnSave;
    ImageButton btnHelp;
    private EditText etPhoneNbr, etCommand1, etCommand2, etCommand3, etCommandReset, etCommandRefresh, etBtnName1, etBtnName2, etBtnName3;
    String phoneNbr, command1, command2, command3, commandReset, commandRefresh, btnName1, btnName2, btnName3;
    String loadPhoneNbr, loadCommand1, loadCommand2, loadCommand3, loadCommandReset, loadCommandRefresh, loadBtnName1, loadBtnName2, loadBtnName3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this;

        etPhoneNbr = findViewById(R.id.etPhoneNbr);
        etCommand1 = findViewById(R.id.etCommand1);
        etCommand2 = findViewById(R.id.etCommand2);
        etCommand3 = findViewById(R.id.etCommand3);
        etBtnName1 = findViewById(R.id.etButtonName1);
        etBtnName2 = findViewById(R.id.etButtonName2);
        etBtnName3 = findViewById(R.id.etButtonName3);
        etCommandReset = findViewById(R.id.etCommandReset);
        etCommandRefresh = findViewById(R.id.etCommandRefresh);
        btnSave = findViewById(R.id.btnSave);
        btnHelp = findViewById(R.id.btnHelp);

        loadSettings();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveSettings();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("Help :");
                builder.setMessage("" +
                        "Use %numberInSeconds to set the timeout between the last command and the next command for Command 1, Command 2 and Command 3.\n\n" +
                        "If you don't need to set a timeout you have to write %0.\n\n" +
                        "Use %yourCommand after the timeout to execute a second command.\n\n" +
                        "For exemple:\n" +
                        "set out1 %10 %reset out1\n\n" +
                        "This command will -> send set out1 -> wait 10 seconds -> send reset out1.\n\n" +
                        "You can also set a timeout at the end of the last command:\n" +
                        "set out1 #0 %reset out1 %10\n\n" +
                        "This command will -> send set out1 -> No timeout -> send reset out1 -> wait 10 seconds");
                builder.setPositiveButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void saveSettings() {

        phoneNbr = etPhoneNbr.getText().toString();
        command1 = etCommand1.getText().toString();
        command2 = etCommand2.getText().toString();
        command3 = etCommand3.getText().toString();
        btnName1 = etBtnName1.getText().toString();
        btnName2 = etBtnName2.getText().toString();
        btnName3 = etBtnName3.getText().toString();
        commandReset = etCommandReset.getText().toString();
        commandRefresh = etCommandRefresh.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PHONE_NBR, phoneNbr);
        editor.putString(CMD1, command1);
        editor.putString(CMD2, command2);
        editor.putString(CMD3, command3);
        editor.putString(BTN_NAME1, btnName1);
        editor.putString(BTN_NAME2, btnName2);
        editor.putString(BTN_NAME3, btnName3);
        editor.putString(RESET_CMD, commandReset);
        editor.putString(REFRESH_CMD, commandRefresh);

        editor.apply();

        MainActivity.getInstance().loadSettings();
    }

    private void loadSettings() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loadPhoneNbr = sharedPreferences.getString(PHONE_NBR, "+41 42 414 41 41");
        loadCommand1 = sharedPreferences.getString(CMD1, "set out1 #1513");
        loadCommand2 = sharedPreferences.getString(CMD2, "set out2 #1513");
        loadCommand3 = sharedPreferences.getString(CMD3, "set out3 #1513");
        loadBtnName1 = sharedPreferences.getString(BTN_NAME1, "Button 1");
        loadBtnName2 = sharedPreferences.getString(BTN_NAME2, "Button 2");
        loadBtnName3 = sharedPreferences.getString(BTN_NAME3, "Button 3");
        loadCommandReset = sharedPreferences.getString(RESET_CMD, "reset out1 out2 #1513");
        loadCommandRefresh = sharedPreferences.getString(REFRESH_CMD, "status #1513");

        etPhoneNbr.setText(loadPhoneNbr);
        etCommand1.setText(loadCommand1);
        etCommand2.setText(loadCommand2);
        etCommand3.setText(loadCommand3);
        etBtnName1.setText(loadBtnName1);
        etBtnName2.setText(loadBtnName2);
        etBtnName3.setText(loadBtnName3);
        etCommandReset.setText(loadCommandReset);
        etCommandRefresh.setText(loadCommandRefresh);
    }
}
