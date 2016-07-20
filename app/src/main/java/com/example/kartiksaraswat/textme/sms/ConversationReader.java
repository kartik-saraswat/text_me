package com.example.kartiksaraswat.textme.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.example.kartiksaraswat.textme.contact.ContactUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik Saraswat on 20-07-2016.
 */
public class ConversationReader {

    public static int getStatus(int smsType, int smsStatus, int smsRead){
        if(smsType == Sms.SMS_TYPE_INBOX){
            return (smsRead==1)?Sms.SMS_STATUS_READ:Sms.SMS_STATUS_UNKNOWN;
        } else if(smsType == Sms.SMS_TYPE_INBOX){
            switch (smsStatus){
                case Telephony.TextBasedSmsColumns.STATUS_COMPLETE: return Sms.SMS_TYPE_SENT;
                case Telephony.TextBasedSmsColumns.STATUS_FAILED: return Sms.SMS_STATUS_FAILED;
                case Telephony.TextBasedSmsColumns.STATUS_PENDING: return Sms.SMS_STATUS_UNKNOWN;
                default: return Sms.SMS_STATUS_UNKNOWN;
            }
        }
        return Sms.SMS_STATUS_UNKNOWN;
    }

    public static List<Sms> getConversationSms(Context context, int threadId){
        List<Sms> smsList = new ArrayList<>();
        try{
            Uri uri = Uri.parse("content://sms/");
            String[] reqCols = {"_id", "thread_id", "address", "date", "body","type","status","read"};
            Cursor c = context.getContentResolver().query(uri, reqCols, "thread_id=?", new String[]{threadId+""}, "date DESC");
            int indexId = c.getColumnIndexOrThrow("_id");
            int indexThreadId = c.getColumnIndexOrThrow("thread_id");
            int indexAddress = c.getColumnIndexOrThrow("address");
            int indexDate = c.getColumnIndexOrThrow("date");
            int indexBody = c.getColumnIndexOrThrow("body");
            int indexType = c.getColumnIndexOrThrow("type");
            int indexStatus = c.getColumnIndexOrThrow("status");
            int indexRead = c.getColumnIndexOrThrow("read");

            if (!c.moveToFirst()) throw new Exception("Empty Thread Data");
            do {
                Sms sms = new Sms();
                sms.setSmsId(c.getInt(indexId));
                sms.setSmsThreadId(c.getInt(indexThreadId));
                sms.setAddress(c.getString(indexAddress));
                sms.setTimeInMillisecond(c.getLong(indexDate));
                sms.setBody(c.getString(indexBody));
                sms.setSmsType(c.getInt(indexType));
                sms.setFromName(ContactUtil.getNameFromNumber(context, sms.getAddress()));
                sms.setStatus(getStatus(sms.getSmsType(), c.getInt(indexStatus), c.getInt(indexRead)));
                System.out.println("THREAD_TYPE_SMS " + sms.getSmsType() + sms.getFromName());
                smsList.add(sms);
            } while (c.moveToNext());

            if(c!=null && !c.isClosed()){
                c.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return smsList;
    }
}
