package com.example.kartiksaraswat.textme;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kartiksaraswat.textme.contact.Contact2;
import com.example.kartiksaraswat.textme.contact.ContactCompletionTextView;
import com.tokenautocomplete.FilteredArrayAdapter;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComposeMessageActivity extends Activity implements View.OnClickListener {

    private ContactCompletionTextView contactCompletionTextView;
    private EditText editMessageField;
    private ArrayAdapter<Contact2> contact2ArrayAdapter;
    private final List<Contact2> contact2List = new ArrayList<>();
    private ContactReader contactReader = new ContactReader(this);
    private List<BroadcastReceiver> broadcastReceivers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message2);

        contactCompletionTextView = (ContactCompletionTextView)findViewById(R.id.contact_search_view);
        editMessageField = (EditText)findViewById(R.id.edit_message_field);
        Button button = (Button)findViewById(R.id.send_button);
        button.setOnClickListener(this);

        if(savedInstanceState == null){
            contactCompletionTextView.setPrefix("To: ");
            processIntentData(getIntent());
        }

        new ContactLoader().execute();
    }

    private void processIntentData(Intent intent) {
        if(intent == null){
            return;
        } else if(Intent.ACTION_SENDTO.equals(intent.getAction())){
            String address = Uri.decode(intent.getDataString());
            address = address.replace("-","").replace("smsto:","").replace("sms:","");
            contactCompletionTextView.append(" "+address);
            if(intent.hasExtra("sms_body")){
                editMessageField.setText(intent.getStringExtra("sms_body"));
            }
        } else if(Intent.ACTION_SEND.equals(intent.getAction())){
            if(intent.hasExtra("address")){
                contactCompletionTextView.append(" "+ intent.getStringExtra("address"));
            }

            if(intent.hasExtra("sms_body")){
                editMessageField.setText(intent.getStringExtra("sms_body"));
            }
        }
    }

    private void unregisterReceivers(){
        for(BroadcastReceiver br : broadcastReceivers){
            try {
                unregisterReceiver(br);
            } catch (Exception e){
                System.out.print("ILLEGAL STATE");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        broadcastReceivers.clear();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceivers();
    }

    @Override
    public void onStop(){
        super.onStop();
        unregisterReceivers();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_button){
            handleMessageSendButton();
            finish();
        }
    }

    private void handleMessageSendButton(){
        List<Contact2> contactList = contactCompletionTextView.getObjects();
        String body = ((EditText)findViewById(R.id.edit_message_field)).getText().toString();
        Iterator<Contact2> iterator = contactList.iterator();
        List<String> failedList = new ArrayList<>();
        while(iterator.hasNext()){
            Contact2 contact2 = iterator.next();
            sendSms(contact2.getName(), contact2.getNumber().replace(" ",""), body, failedList);
        }
    }

    private void sendSms(final String name, final String phoneNumber, final String body, final List<String> failedList){
        try {
            String SMS_SENT = "SMS_SENT";
            String SMS_DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPendingIntent =
                    PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
            PendingIntent deliveredPendingIntent =
                    PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

            SendBroadcastReceiver sendBroadcastReceiver = new SendBroadcastReceiver(name, phoneNumber, body, failedList);
            DeliveryBroadcastReceiver deliveryBroadcastReceiver = new DeliveryBroadcastReceiver(name, phoneNumber, body, failedList);
            registerReceiver(sendBroadcastReceiver, new IntentFilter(SMS_SENT));
            registerReceiver(deliveryBroadcastReceiver, new IntentFilter(SMS_DELIVERED));
            broadcastReceivers.add(sendBroadcastReceiver);
            broadcastReceivers.add(deliveryBroadcastReceiver);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, body, sentPendingIntent, deliveredPendingIntent);

            ContentValues values = new ContentValues();
            values.put("address", phoneNumber);
            values.put("body", body);
            values.put("date", System.currentTimeMillis() + "");
            getBaseContext().getContentResolver().insert(
                    Uri.parse("content://sms/sent"), values);

        } catch (Exception e){
            System.out.println(getClass().getName() + "\n" + e);
        }
    }

    private class SendBroadcastReceiver extends BroadcastReceiver{
        private String name,phoneNumber,body;
        private List<String> failedList;

        public SendBroadcastReceiver(String name, String phoneNumber, String body, List<String> failedList){
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.body = body;
            this.failedList = failedList;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS sent",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure cause", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "No pdu provided", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private class DeliveryBroadcastReceiver extends BroadcastReceiver{

        private String name,phoneNumber,body;
        private List<String> failedList;

        public DeliveryBroadcastReceiver(String name, String phoneNumber, String body, List<String> failedList){
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.body = body;
            this.failedList = failedList;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS delivered",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    failedList.add(name);
                    Toast.makeText(getBaseContext(), "Oops not delivered", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadAdapter(){
        contact2ArrayAdapter = new FilteredArrayAdapter<Contact2>(this,R.layout.custom_contact2_view, contact2List) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.custom_contact2_view, parent, false);
                }

                Contact2 contact2 = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(contact2.getName());
                ((TextView)convertView.findViewById(R.id.number)).setText(contact2.getNumber());

                return convertView;
            }

            @Override
            protected boolean keepObject(Contact2 obj, String mask) {
                mask = mask.toLowerCase();
                return obj.getName().toLowerCase().startsWith(mask) || obj.getNumber().toLowerCase().startsWith(mask);
            }
        };
    }

    private class ContactLoader extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String[] params) {
            contactReader.loadContactList("", contact2List);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            loadAdapter();
            contactCompletionTextView.setAdapter(contact2ArrayAdapter);
        }
    }
}