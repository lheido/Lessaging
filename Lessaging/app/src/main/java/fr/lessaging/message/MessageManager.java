package fr.lessaging.message;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import fr.lessaging.utils.AppConfig;

/**
 * Created by lheido on 27/12/14.
 */
public class MessageManager {

    public static final String ACTION_DELIVERED_SMS = "fr.lessaging.delivered";
    public static final String ACTION_SENT_SMS = "fr.lessaging.sent_sms";
    public static final String ARG_SMS_DELIVERED = "delivered_message_id";
    private static final String ARG_SMS_SENT = "sent_message_id";


    public static void send(Context context, Message message, String address){
        SmsManager manager = SmsManager.getDefault();

        Intent iDelivered = new Intent(ACTION_DELIVERED_SMS);
        iDelivered.putExtra(ARG_SMS_DELIVERED, message.getId());
        Intent iSent = new Intent(ACTION_SENT_SMS);
        iSent.putExtra(ARG_SMS_SENT, message.getId());
        ArrayList<PendingIntent> piSent = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> piDelivered = new ArrayList<PendingIntent>();
        piSent.add(PendingIntent.getBroadcast(context, 0, iSent, PendingIntent.FLAG_UPDATE_CURRENT));
        piDelivered.add(PendingIntent.getBroadcast(context, 0, iDelivered, PendingIntent.FLAG_UPDATE_CURRENT));

        ArrayList<String> bodyPart = manager.divideMessage(message.getBody());
        if(AppConfig.DEBUG){
            Log.w("MessageManager.send", "bodyPart size = "+bodyPart.size());
            int[] len = SmsMessage.calculateLength(message.getBody(), false);
            Log.w("MessageManager.send", "number of sms required : "+ len[0]);
            Log.w("MessageManager.send", "number of code units used : "+ len[1]);
            Log.w("MessageManager.send", "number of code units remaining until the next message : "+ len[2]);
        }

        manager.sendMultipartTextMessage(address, null, bodyPart, piSent, piDelivered);
    }

    public static void send(Context context, String messageBody, Bitmap image, String addressToSend){}

    public static void store(Context context, String phoneContact, Message sms, long thread_id){
        try {
            ContentValues values = new ContentValues();
            values.put("address", sms.getSender());
            values.put("body", sms.getBody());
            values.put("read", false);
            values.put("type", (!PhoneNumberUtils.compare(phoneContact, sms.getSender())) ? 2 : 1);
            values.put("status", 32);
            if(thread_id != -1)
                values.put("thread_id", thread_id);
            values.put("date", sms.getDateNormalize());
            Uri uri_id = context.getContentResolver().insert(Uri.parse("content://sms"), values);
            long id = -1L;
            if(uri_id != null) {
                id = Long.parseLong(uri_id.getLastPathSegment());
            }
            sms.setId(id);
        } catch (Exception ex) {
            Toast.makeText(context, "store_sms\n" + ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
