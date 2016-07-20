package com.example.kartiksaraswat.textme.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.kartiksaraswat.textme.MainActivity;
import com.example.kartiksaraswat.textme.R;
import com.example.kartiksaraswat.textme.sms.Sms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik Saraswat on 19-07-2016.
 */
public class SmsReceiver extends BroadcastReceiver {
    private final static String TAG = "SMS_RECEIVED";
    private List<Sms> smsList = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages = null;
        smsList.clear();
        if (myBundle != null)
        {
            Object [] pdus = (Object[]) myBundle.get("pdus");

            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                }
                else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                Sms sms = new Sms();
                sms.setTimeInMillisecond(messages[i].getTimestampMillis());
                sms.setAddress(messages[i].getOriginatingAddress());
                sms.setBody(messages[i].getMessageBody());
                sms.setFromName(getNameFromNumber(context, sms.getAddress()));
                smsList.add(sms);
                Log.d(TAG, sms.toString());
            }

            showSmsNotification(context);
        }
    }

    private String getNameFromNumber(Context context,String phone){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phone;
        } else{
            String contactName = null;
            if(cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                if(contactName.trim().isEmpty()){
                    contactName = phone;
                }
            } else {
                contactName = phone;
            }

            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

            return contactName;
        }
    }

    private void showSmsNotification(Context context){
        String title = smsList.size()==1? "New Message" : smsList.size() + "New messages";
        String subject = "";

        for(Sms sms : smsList){
            subject+=sms.toString();
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent =
                PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        Notification smsNotification  = new Notification.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(subject)
                .setContentIntent(pIntent)
                .build();
        notificationManager.notify(0, smsNotification);
    }
}
