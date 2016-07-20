package com.example.kartiksaraswat.textme;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kartiksaraswat.textme.sms.ConversationReader;
import com.example.kartiksaraswat.textme.sms.Sms;
import com.example.kartiksaraswat.textme.sms.SmsReader;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int threadId;
    private String address;
    private String name;
    private SmsReader smsReader;
    List<Sms> smsList = new ArrayList<>();

    private  EditText quickEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.threadId = getIntent().getIntExtra("threadId", -1);
        this.address = getIntent().getStringExtra("address");
        this.name = getIntent().getStringExtra("name");
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.conversation_toolbar);
        toolbar.setTitle(name);
        toolbar.setLogo(R.drawable.contact_default);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.conversation_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        smsReader = new SmsReader(this);
        smsList = ConversationReader.getConversationSms(this,this.threadId);
        mAdapter = new ConversationRecyclerViewAdapter(this, smsList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        mLayoutManager.scrollToPosition(0);
    }

    public void handleOnClickSendMessage(View view){
        quickEditText = (EditText)findViewById(R.id.quick_edit_text);
        String body = quickEditText.getText().toString();
        final Sms sms = new Sms();
        sms.setSmsThreadId(threadId);
        sms.setAddress(address);
        sms.setBody(body);
        sms.setTimeInMillisecond(System.currentTimeMillis());
        sms.setSmsType(Sms.SMS_TYPE_SENT);
        smsList.add(0,sms);
        mAdapter.notifyItemInserted(0);
        PendingIntent sentPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);

        PendingIntent deliveredPendingIntent =
                PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK :
                        ContentValues values = new ContentValues();
                        values.put("thread_id",sms.getSmsThreadId());
                        values.put("address", sms.getAddress());
                        values.put("body", sms.getBody());
                        values.put("date", System.currentTimeMillis() + "");
                        getBaseContext().getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);
                        sms.setStatus(Sms.SMS_STATUS_SENT);
                        mAdapter.notifyItemChanged(0);
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, "Oops something bad happened", Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter("SMS_SENT"));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        sms.setStatus(Sms.SMS_STATUS_DELIVERED);
                        mAdapter.notifyItemChanged(0);
                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, "Oops something bad happened", Toast.LENGTH_SHORT).show();
                }
            }
        }, new IntentFilter("SMS_DELIVERED"));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(address, null, body, sentPendingIntent, deliveredPendingIntent);
        quickEditText.setText("");
        mRecyclerView.scrollToPosition(0);
    }
}
