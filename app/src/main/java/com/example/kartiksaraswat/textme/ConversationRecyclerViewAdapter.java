package com.example.kartiksaraswat.textme;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.kartiksaraswat.textme.sms.Sms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    List<Sms> smsList;
    Context context;
    public ConversationRecyclerViewAdapter(Context context, List<Sms> smsList){
        this.context = context;
        this.smsList = smsList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textBody;
        public TextView textTime;
        public LinearLayout linearLayout;
        public ImageView sentImageView;
        public ImageView deliveredImageView;
        public View view;
        public ViewHolder(View v) {
            super(v);
            this.view = v;
            textBody = (TextView)v.findViewById(R.id.text_body);
            textTime = (TextView)v.findViewById(R.id.text_time);
            linearLayout = (LinearLayout)v.findViewById(R.id.linear_layout);
            sentImageView =(ImageView)v.findViewById(R.id.sent_check);
            deliveredImageView =(ImageView)v.findViewById(R.id.delivered_check);
        }
    }

    @Override
    public ConversationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_message_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ConversationRecyclerViewAdapter.ViewHolder holder, int position) {
        Sms sms = smsList.get(position);
        holder.textBody.setText(sms.getBody());
        holder.textTime.setText(toMessageTime(sms.getTimeInMillisecond()));
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(sms.getSmsType() == Sms.SMS_TYPE_SENT){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setLayoutDirection(RelativeLayout.LAYOUT_DIRECTION_RTL);
            holder.linearLayout.setBackgroundResource(R.drawable.chat_right_corner);
            if(sms.getStatus() == Sms.SMS_STATUS_DELIVERED){
                holder.deliveredImageView.setVisibility(View.VISIBLE);
            } else if(sms.getStatus() == Sms.SMS_STATUS_SENT){
                holder.sentImageView.setVisibility(View.VISIBLE);
            }
        } else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.setLayoutDirection(RelativeLayout.LAYOUT_DIRECTION_LTR);
            holder.linearLayout.setBackgroundResource(R.drawable.chat_left_corner);
        }
        holder.linearLayout.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    private String toMessageTime(long timeInMillis){
        long diff = System.currentTimeMillis() - timeInMillis;
        long seconds_ = 1000;
        long minute_ = 60*seconds_;
        long hour_ = 60*minute_;
        long day_ = 24*hour_ ;

        if(diff > 2*day_){
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
            Date date = new Date(timeInMillis);
           return format.format(date);
        } else if(diff>day_){
            return diff/day_ +"h";
        } else if(diff > hour_){
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            Date date = new Date(timeInMillis);
            return format.format(date);
        } else if(diff > minute_){
            return diff/minute_+"m";
        } else if(diff>seconds_){
            return diff/seconds_+"s";
        }
        else return "";
    }
}
