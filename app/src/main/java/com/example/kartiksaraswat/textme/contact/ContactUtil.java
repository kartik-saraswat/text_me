package com.example.kartiksaraswat.textme.contact;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by Kartik Saraswat on 20-07-2016.
 */
public class ContactUtil {

    public static String getNameFromNumber(Context context, String phone){
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
}
