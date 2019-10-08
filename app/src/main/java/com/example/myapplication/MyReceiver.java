package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import static android.content.Context.MODE_PRIVATE;

public class MyReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PHONE_NBR = "PhoneNbr";

    String phoneNumber;
    private String msg, phoneNo, out1, out2, out3;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Get sharedPreferences to get the phone number
        SharedPreferences sharedPreferences = MainActivity.getInstance().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        phoneNumber = sharedPreferences.getString(PHONE_NBR, "+41 42 414 41 41");

        //If sms received
        if(intent.getAction() == SMS_RECEIVED) {

            Bundle dataBundle = intent.getExtras();

            if(dataBundle != null) {

                Object[] mypdu = (Object[])dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];

                for(int i = 0; i < mypdu.length; i++) {

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);
                    }
                    else {

                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg = message[i].getMessageBody();
                    phoneNo = message[i].getOriginatingAddress();
                }
                //Verify if the sms come from the right phone number
                if(phoneNo.equals(phoneNumber)) {

                    //Split the result sms to get OUT1 / OUT2 /OUT3
                    String[] splitMsgArray = msg.split(" |[ \\n]");

                    for(int i = 0; i < splitMsgArray.length; i++)
                    {
                        if(splitMsgArray[i].equals("OUT1:")) {

                            out1 = splitMsgArray[i + 1];
                        }
                        else if(splitMsgArray[i].equals("OUT2:")) {

                            out2 = splitMsgArray[i + 1];
                        }
                        else if(splitMsgArray[i].equals("OUT3:")) {

                            out3 = splitMsgArray[i + 1];
                        }
                    }

                    //Save all status locally
                    MainActivity.getInstance().saveStatus(out1, out2, out3);
                }
            }
        }
    }
}
