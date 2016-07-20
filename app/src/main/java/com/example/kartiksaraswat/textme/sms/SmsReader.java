package com.example.kartiksaraswat.textme.sms;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.widget.ImageView;

import com.example.kartiksaraswat.textme.contact.ContactUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kartik Saraswat on 17-07-2016.
 */
public class SmsReader {

    private List<Sms> permanentList = new ArrayList<>();
    private List<Sms> smsList = new ArrayList<>();
    private Map<Integer,Boolean> threadSeenMap = new HashMap<>();
    private Context context;

    public SmsReader(Context context){
        this.context = context;
    }

    public void addSms(Sms sms){
        permanentList.add(sms);
        threadSeenMap.put(sms.getSmsThreadId(), false);
    }

    private boolean readConversations(String query){
        permanentList.clear();
        threadSeenMap.clear();
        try{
            Uri uri = Uri.parse("content://sms/");
            String[] reqCols = {"_id", "thread_id", "address", "date", "body", "type"};
            Cursor c = context.getContentResolver().query(uri, reqCols, null, null, "date DESC");
            int indexId = c.getColumnIndex("_id");
            int indexThreadId = c.getColumnIndexOrThrow("thread_id");
            int indexAddress = c.getColumnIndexOrThrow("address");
            int indexDate = c.getColumnIndexOrThrow("date");
            int indexBody = c.getColumnIndexOrThrow("body");
            int indexType = c.getColumnIndexOrThrow("type");
            if (!c.moveToFirst()) return false;
            do {
                Sms sms = new Sms();
                sms.setSmsId(c.getInt(indexId));
                sms.setSmsThreadId(c.getInt(indexThreadId));
                sms.setAddress(c.getString(indexAddress));
                sms.setTimeInMillisecond(c.getLong(indexDate));
                sms.setBody(c.getString(indexBody));
                sms.setFromName(ContactUtil.getNameFromNumber(context, sms.getAddress()));
                sms.setSmsType(c.getInt(indexType));
                addSms(sms);
            } while (c.moveToNext());

            if(c!=null && !c.isClosed()){
                c.close();
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

   public void refresh(){
        readConversations("");
    }

    public List<Sms> getSmsList(String query){
        if(permanentList.size()>0) {
            for (int i = 0, k = 0; i < permanentList.size() && k < 50; i++) {
                int threadId = permanentList.get(i).getSmsThreadId();
                if (!threadSeenMap.get(threadId)) {
                    smsList.add(permanentList.get(i));
                    threadSeenMap.put(threadId, true);
                    k++;
                }
            }
        }
        return smsList;
    }

    public List<Sms> searchQuery(String query){
        List<Sms> smsList = new ArrayList<>();
        if(permanentList.size()>0) {
            for (int i = 0, k = 0; i < permanentList.size() && k < 50; i++) {
                if(permanentList.get(i).getBody().contains(query)){
                    smsList.add(permanentList.get(i));
                }
            }
        }
        return smsList;
    }
}
