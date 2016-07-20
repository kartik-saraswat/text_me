package com.example.kartiksaraswat.textme;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kartiksaraswat.textme.sms.Sms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kartik Saraswat on 12-07-2016.
 */
public class InboxRecyclerViewAdapter extends RecyclerView.Adapter<InboxRecyclerViewAdapter.ViewHolder> {

    private List<Sms> smsList = new ArrayList<>();
    private Context context;

    public InboxRecyclerViewAdapter(Context context, List<Sms> smsList){
        this.context = context;
        this.smsList.addAll(smsList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView contactImage;
        public TextView fromNameOrNumber;
        public TextView messageBodyView;
        public ViewHolder(View v) {
            super(v);
            contactImage = (ImageView)v.findViewById(R.id.contact_image);
            fromNameOrNumber = (TextView)v.findViewById(R.id.from);
            messageBodyView = (TextView)v.findViewById(R.id.msg_body);
        }
    }

    @Override
    public InboxRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msgtextview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(InboxRecyclerViewAdapter.ViewHolder holder, int position) {
        Sms sms = smsList.get(position);
        holder.contactImage.setImageResource(R.drawable.contact_default);
        holder.fromNameOrNumber.setText(sms.getFromName());
        holder.messageBodyView.setText(sms.getBody());
        holder.messageBodyView.setMaxLines(2);
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public void updateItems(List<Sms> newList){
        smsList.clear();
        smsList.addAll(newList);
        notifyDataSetChanged();
    }
}
