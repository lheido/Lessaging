package fr.lessaging.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by lheido on 06/12/14.
 */
public class UserPref {

    public static final String DELETE_OLD_SMS_KEY = "delete_old_sms";
    public static final String HIDE_KEYBOARD_KEY = "hide_keyboard";
    public static final String FIRST_UPPER_LETTER_KEY = "first_upper_letter";
    public static final String DRAWER_START_OPENED_KEY = "drawer_start_opened";
    public static final String RECEIVER_NOTIFICATION_KEY = "receiver_notification";
    public static final String RECEIVER_VIBRATE_KEY = "receiver_vibrate";
    public static final String RECEIVER_VIBRATE_DELIVERED_KEY = "receiver_vibrate_delivered";
    public static final String RECEIVER_RINGTONE_KEY = "receiver_ringtone";

    public static final String SMS_ONLOAD_KEY = "sms_onload";
    public static final String TEXT_SIZE_KEY = "text_size";
    public static final String OLD_MESSAGE_NUM_KEY = "limit_old_sms";
    public static final String CONVERSATION_JAZZYEFFECT_KEY = "conversation_jazzyeffect";
    public static final String LIST_CONVERSATIONS_JAZZYEFFECT_KEY = "list_conversations_jazzyeffect";

    public static final int maxSms = 21;
    public static final  float textSize = 13.0F;
    public static final  int oldMessageNum = 500;
    public static final  int conversationEffect = 14;
    public static final  int listConversationEffect = 14;

    private static SharedPreferences userPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getMaxSms(Context context) {
        return Integer.parseInt(userPref(context).getString(SMS_ONLOAD_KEY, ""+maxSms));
    }

    public static float getTextSize(Context context) {
        return Float.parseFloat(userPref(context).getString(TEXT_SIZE_KEY, ""+textSize));
    }

    public static int getOldMessageNum(Context context) {
        return Integer.parseInt(userPref(context).getString(OLD_MESSAGE_NUM_KEY, ""+oldMessageNum));
    }

    public static int getConversationEffect(Context context) {
        return Integer.parseInt(userPref(context).getString(CONVERSATION_JAZZYEFFECT_KEY, ""+conversationEffect));
    }

    public static int getListConversationEffect(Context context) {
        return Integer.parseInt(userPref(context).getString(LIST_CONVERSATIONS_JAZZYEFFECT_KEY, ""+listConversationEffect));
    }

    public static boolean deleteOldSmsIsEnabled(Context context){
        return userPref(context).getBoolean(DELETE_OLD_SMS_KEY, false);
    }
    public static boolean hideKeyboardIsEnabled(Context context){
        return userPref(context).getBoolean(HIDE_KEYBOARD_KEY, true);
    }
    public static boolean firstUpperLetterIsEnabled(Context context){
        return userPref(context).getBoolean(FIRST_UPPER_LETTER_KEY, true);
    }
    public static boolean drawerStartOpenedIsEnabled(Context context){
        return userPref(context).getBoolean(DRAWER_START_OPENED_KEY, true);
    }
    public static boolean receiverNotificationIsEnabled(Context context){
        return userPref(context).getBoolean(RECEIVER_NOTIFICATION_KEY, true);
    }
    public static boolean receiverVibrateIsEnabled(Context context){
        return userPref(context).getBoolean(RECEIVER_VIBRATE_KEY, true);
    }
    public static boolean receiverVibrateDelyveredIsEnabled(Context context){
        return userPref(context).getBoolean(RECEIVER_VIBRATE_DELIVERED_KEY, true);
    }
    public static boolean receiverRingtoneIsEnabled(Context context){
        return userPref(context).getBoolean(RECEIVER_RINGTONE_KEY, true);
    }
}
