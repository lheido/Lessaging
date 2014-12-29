package fr.lessaging.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by lheido on 27/12/14.
 */
public class LessagingVibrator {

    private static Vibrator vibrator;

    public static final long[] deliveredPattern = {
            0, // Start immediately
            100, 100, 100, 100, 100, 100, 100, 100
    };

    private static void checkVibrator(Context context){
        if(vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    public static void delivered(Context context){
        checkVibrator(context);
        vibrator.vibrate(deliveredPattern, -1);
    }

    public static void error(Context context){
        checkVibrator(context);
        vibrator.vibrate(2000);
    }

    public static void notification(Context context){
        checkVibrator(context);
        vibrator.vibrate(1000);
    }
}
