package com.example.kartiksaraswat.textme;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.example.kartiksaraswat.textme.sms.Sms;
import com.example.kartiksaraswat.textme.sms.SmsReader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Sms> smsList = new ArrayList<>();
    private SmsReader smsReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        new LoadInboxList().execute();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void handleComposeMessage() {
        Intent intent = new Intent(this, ComposeMessageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final android.support.v7.widget.SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                reloadSmsList();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_new_message){
            handleComposeMessage();
        }
        return true;
    }

    public void handleItemClicked(View view){
        int position = mRecyclerView.getChildAdapterPosition(view);
        Sms sms = smsList.get(position);
        int threadId = sms.getSmsThreadId();
        System.out.println("Thread Id Is : " + threadId + " address : " + sms.getAddress());
        view.animate().rotationXBy(360f).setDuration(300L).start();

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("threadId",threadId);
        intent.putExtra("address",sms.getAddress());
        intent.putExtra("name",sms.toString());
        startActivity(intent);
    }

    void loadSmsIntoRecyclerView(){
        mAdapter = new InboxRecyclerViewAdapter(this, smsList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        List<Sms> newList = smsReader.searchQuery(query);
        ((InboxRecyclerViewAdapter) mAdapter).updateItems(newList);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText==null ||newText.isEmpty()){
            ((InboxRecyclerViewAdapter)mAdapter).updateItems(smsList);
            return true;
        }
        return false;
    }

    public boolean reloadSmsList() {
        ((InboxRecyclerViewAdapter)mAdapter).updateItems(smsList);
        return true;
    }

    private class LoadInboxList extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            smsReader = new SmsReader(MainActivity.this);
            smsReader.refresh();
            smsList = smsReader.getSmsList("");
            return "OK";
        }

        @Override
        protected void onPostExecute(String result) {
            loadSmsIntoRecyclerView();
        }
    }
}
