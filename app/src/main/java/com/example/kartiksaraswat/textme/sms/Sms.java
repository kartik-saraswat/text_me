package com.example.kartiksaraswat.textme.sms;

import android.graphics.Bitmap;

/**
 * Created by Kartik Saraswat on 16-07-2016.
 */
public class Sms implements Comparable{

    public static final int SMS_TYPE_ALL = 0;
    public static final int SMS_TYPE_INBOX = 1;
    public static final int SMS_TYPE_SENT = 2;
    public static final int SMS_TYPE_DRAFT = 3;
    public static final int SMS_TYPE_OUTBOX = 4;
    public static final int SMS_TYPE_FAILED = 5;

    public static final int SMS_STATUS_UNKNOWN = 0;
    public static final int SMS_STATUS_READ = 1;
    public static final int SMS_STATUS_FAILED = 2;
    public static final int SMS_STATUS_SENT = 3;
    public static final int SMS_STATUS_DELIVERED = 4;

    int smsId;
    int smsThreadId;
    String address;
    String fromName;

    String toNumber;
    String toName;
    long timeInMillisecond;
    String body;
    int status = SMS_STATUS_UNKNOWN;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int smsType = SMS_TYPE_ALL;

    public int getSmsId() {
        return smsId;
    }

    public void setSmsId(int smsId) {
        this.smsId = smsId;
    }

    public int getSmsThreadId() {
        return smsThreadId;
    }

    public void setSmsThreadId(int smsThreadId) {
        this.smsThreadId = smsThreadId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimeInMillisecond() {
        return timeInMillisecond;
    }

    public void setTimeInMillisecond(long timeInMillisecond) {
        this.timeInMillisecond = timeInMillisecond;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public int getSmsType() {
        return smsType;
    }

    public void setSmsType(int smsType) {
        this.smsType = smsType;
    }

    @Override
    public int hashCode(){
        return getSmsId();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Sms){
            return ((Sms)o).getSmsId() == this.getSmsId();
        }
        return false;
    }

    @Override
    public int compareTo(Object another) {
        if(another instanceof Sms){
            Sms s = (Sms)another;
            return (this.getTimeInMillisecond() < s.getTimeInMillisecond())?-1:1;
        }
        return 1;
    }

    public String toString(){
        if(fromName == null ||fromName.isEmpty()){
            return address;
        } else
            return fromName;
    }
}
