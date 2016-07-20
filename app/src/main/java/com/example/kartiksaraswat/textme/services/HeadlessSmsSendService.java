package com.example.kartiksaraswat.textme.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * Created by Kartik Saraswat on 20-07-2016.
 */
public class HeadlessSmsSendService extends IntentService {

    public HeadlessSmsSendService() {
        super(HeadlessSmsSendService.class.getName());

        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String action = intent.getAction();
        if (!TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(action)) {
            return;
        }

        Bundle extras = intent.getExtras();
    }

    private String getRecipients(Uri uri) {
        String base = uri.getSchemeSpecificPart();
        int pos = base.indexOf('?');
        return (pos == -1) ? base : base.substring(0, pos);
    }

    private void sendAndStoreTextMessage(ContentResolver contentResolver, String[] destinations, String message) {
        SmsManager smsManager = SmsManager.getDefault();

        Uri smsSentUri = Uri.parse("content://sms/sent");

        for (String destination : destinations) {
            smsManager.sendTextMessage(destination, null, message, null, null);

            ContentValues values = new ContentValues();
            values.put("address", destination);
            values.put("body", message);

            Uri uri = contentResolver.insert(smsSentUri, values);
        }
    }
}
