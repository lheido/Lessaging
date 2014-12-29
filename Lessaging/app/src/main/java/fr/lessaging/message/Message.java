package fr.lessaging.message;

import android.content.Context;
import android.net.Uri;
import android.text.format.Time;

import fr.lessaging.R;

/**
 * Created by lheido on 05/12/14.
 */
public class Message {
    private int type;
    private String sender = null;
    private String body_ = null;
    private Uri img_ = null;
    private Time date_ = null;
    private boolean read_ = false;
    private long _id = -1;

    public Message(){}

    public Message(long _id, String body, String sender, int deli, String type,Time t){
        this(_id, body, sender, deli, type);
        this.date_ = t;
    }

    public Message(long _id, String body, String sender, int deli, String type, long date){
        this(_id, body, sender, deli, type);
        this.date_ = new Time();
        this.date_.set(date);
    }

    public Message(long _id, String body, String sender, int deli, String type){
        this._id = _id;
        this.body_ = body;
        this.sender = sender;
        this.read_ = deli == 0;
        this.type = Integer.parseInt(type);
    }

    public static String formatDate(Context context, Time date){
        int time_dd = date.monthDay;
        int time_MM = date.month;
        Time now = new Time();
        now.setToNow();
        int c_dd = now.monthDay;
        int c_MM = now.month;
        if(time_MM == c_MM){
            if(time_dd == c_dd)
                return date.format(context.getResources().getString(R.string.date_format_today));
            else
                return date.format(context.getResources().getString(R.string.date_format_current_month));
        }
        return date.format(context.getResources().getString(R.string.date_format));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDate(Time date){
        this.date_ = date;
    }
    public void setDate(long date){
        this.date_ = new Time();
        this.date_.set(date);
    }
    public String getDate(Context context){
        return formatDate(context, this.date_);
    }

    public long getDateNormalize(){
        return this.date_.normalize(false);
    }

    public void setBody(String string) {
        this.body_ = string != null ? string : "";
    }

    public String getBody() {
        return this.body_;
    }

    public Uri getUriPicture() {
        return this.img_;
    }

    public void setUriPicture(Uri pict){
        this.img_ = pict;
    }

    public String getSender(){
        return this.sender;
    }

    public void setSender(String s){
        this.sender = s;
    }

    public boolean isRead() {
        return this.read_;
    }

    public void setRead(boolean b) {
        this.read_ = b;
    }

    public void setId(long sms_id) {
        this._id = sms_id;
    }

    public long getId(){
        return this._id;
    }
}
