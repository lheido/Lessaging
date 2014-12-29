package fr.lessaging.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import fr.lessaging.R;
import fr.lessaging.conversation.Conversation;
import fr.lessaging.conversation.ConversationsList;
import fr.lessaging.message.Message;
import fr.lessaging.message.MessageManager;
import fr.lessaging.utils.LessagingVibrator;
import fr.lessaging.utils.UserPref;

/**
 * Created by lheido on 27/12/14.
 */
public class MessageReceiver extends BroadcastReceiver {

    public static final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";
    public static final String ACTION_RECEIVE_MMS = "android.provider.Telephony.MMS_RECEIVED";

    private static ArrayList<MessageReceiverCallback> mCallbacks;

    private static void checkCallbacks(){
        if(mCallbacks == null){
            mCallbacks = new ArrayList<>();
        }
    }

    public static void addCallback(MessageReceiverCallback callback) {
        checkCallbacks();
        mCallbacks.add(callback);
    }

    public static void removeCallback(MessageReceiverCallback callback){
        checkCallbacks();
        mCallbacks.remove(callback);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(MessageManager.ACTION_DELIVERED_SMS)){
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    Bundle extras = intent.getExtras();
                    long id = extras.getLong(MessageManager.ARG_SMS_DELIVERED, -1);
                    if(id != -1){
                        ContentValues values = new ContentValues();
                        values.put("status", 0);
                        try{
                            context.getContentResolver().update(Uri.parse("content://sms/" + id), values, null, null);
                            Toast.makeText(context, "Message remis", Toast.LENGTH_SHORT).show();
                            if (UserPref.receiverVibrateDelyveredIsEnabled(context)) {
                                LessagingVibrator.delivered(context);
                            }
                            checkCallbacks();
                            for (MessageReceiverCallback callback : mCallbacks){
                                callback.onDelivered(id);
                            }
                        }catch (Exception ex){
                            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                default:
                    Toast.makeText(context, "Erreur, message non remis", Toast.LENGTH_SHORT).show();
                    if (UserPref.receiverVibrateDelyveredIsEnabled(context)) {
                        LessagingVibrator.error(context);
                    }
                    break;
            }
        } else if(action.equals(MessageManager.ACTION_SENT_SMS)){
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS sent",
                            Toast.LENGTH_SHORT).show();
                    errorVibration(context);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "Generic failure",
                            Toast.LENGTH_SHORT).show();
                    errorVibration(context);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "No service",
                            Toast.LENGTH_SHORT).show();
                    errorVibration(context);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "Null PDU",
                            Toast.LENGTH_SHORT).show();
                    errorVibration(context);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context, "Radio off",
                            Toast.LENGTH_SHORT).show();
                    errorVibration(context);
                    break;
                default:
                    Toast.makeText(context, "Erreur, le message n'a pas était envoyé", Toast.LENGTH_SHORT).show();
                    errorVibration(context);
                    break;
            }
        } else if(action.equals(ACTION_RECEIVE_SMS)){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i = 0; i<pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                String body;
                if(messages.length > -1){
                    body = "";
                    for(SmsMessage x : messages){
                        body += x.getMessageBody();
                    }
                    long date = messages[0].getTimestampMillis();
                    String phone = messages[0].getDisplayOriginatingAddress();
                    String senderName = Conversation.retrieveContactName(context, phone);
                    Message newMessage = new Message(-1, body, phone, 1, date);
                    LessagingVibrator.notification(context);
                    String toastMessage = String.format(context.getResources().getString(R.string.receive_new_sms_toast), senderName);
                    Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
                    // get contact position in conversationList
                    int size = ConversationsList.size();
                    if(size != -1) {
                        int i = 0;
                        while (i < size && !PhoneNumberUtils.compare(ConversationsList.get(i).getPhone(), phone)) {
                            i++;
                        }
                        if (i < size && PhoneNumberUtils.compare(ConversationsList.get(i).getPhone(), phone)) {
                            ConversationsList.moveIndexToTop(i);
                        } else {
                            // not in conversationsList
                            try {
                                long threadId = Conversation.getOrCreateThreadId(context, phone);
                                ConversationsList.add(0, new Conversation(senderName, phone, 1, "" + threadId));
                            }catch (Exception ex){
                                Toast.makeText(context,
                                        "Error: new conversation not added into conversation list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    checkCallbacks();
                    for (MessageReceiverCallback callback : mCallbacks){
                        callback.onReceivedNewSms(newMessage, senderName);
                    }
                }
            }
        }
//        else if(action.equals(ACTION_RECEIVE_MMS)){
//
//        }
    }

    public static void errorVibration(Context context){
        if (UserPref.receiverVibrateDelyveredIsEnabled(context)) {
            LessagingVibrator.error(context);
        }
    }

    public interface MessageReceiverCallback{
        void onReceivedNewSms(Message newMessage, String senderName);
        void onDelivered(long id);
    }


}
