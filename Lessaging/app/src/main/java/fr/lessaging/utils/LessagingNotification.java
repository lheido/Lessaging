package fr.lessaging.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import fr.lessaging.R;

/**
 * Created by lheido on 27/12/14.
 */
public class LessagingNotification {

    private static NotificationManager manager;

    private static void getManager(Context context){
        if(manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public static void cancel(Context context, int index){
        getManager(context);
        manager.cancel(index);
    }

    public static void cancelAll(Context context){
        getManager(context);
        manager.cancelAll();
    }

    public static void showNotification(Context context, String body, String name, String phone, PendingIntent pIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.lheido_sms_icon)
                .setTicker(body)
                .setContentTitle("" + name)
                .setContentText(body)
                .setPriority(2)
//                .addAction(R.drawable.send_sms, "Ouvrir", openConversationIntent)
                .setContentIntent(pIntent)
//                .setAutoCancel(true);
                .setAutoCancel(false);

        getManager(context);
        manager.notify(0, builder.build());
    }

    public static void playNotificationSound(Context context){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                if (UserPref.receiverVibrateIsEnabled(context)){
                    LessagingVibrator.notification(context);
                }
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                if(UserPref.receiverRingtoneIsEnabled(context)) {
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(context, notification);
                        r.play();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

}
