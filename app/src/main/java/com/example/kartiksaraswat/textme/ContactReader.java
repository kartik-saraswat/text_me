package com.example.kartiksaraswat.textme;

import android.app.LoaderManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.kartiksaraswat.textme.contact.Contact2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik Saraswat on 19-07-2016.
 */
public class ContactReader{

    private Context context;

    public ContactReader(Context context){
        this.context = context;
    }

    public void  loadContactList(String nameLike, List<Contact2> contact2List){
        contact2List.clear();
        Cursor people, phones;
        try {
            String searchCriteria = "" + ContactsContract.Contacts.HAS_PHONE_NUMBER +">0";
            searchCriteria += " AND "+ContactsContract.Contacts.DISPLAY_NAME +" LIKE ?";
            String[] searchArgs = new String[]{ nameLike+"%"};
            people = context.getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI, null, searchCriteria, searchArgs, null);
            if(people.moveToFirst()){
                do{
                    String contactName = people.getString(people
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactId = people.getString(people
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    String searchCriteria2 = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = ?";
                    String[] searchArgs2 = new String[]{contactId};

                    phones = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, searchCriteria2, searchArgs2, null);

                    if(phones.moveToFirst()){
                        do{
                            try {
                                String phoneNumber = phones
                                        .getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                Contact2 contact = new Contact2(contactName, phoneNumber);
                                contact2List.add(contact);
                            } catch (NullPointerException npe){
                                npe.printStackTrace();
                            }
                        }while(phones.moveToNext());
                        phones.close();
                    }
                }while(people.moveToNext());
                people.close();
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }
}
