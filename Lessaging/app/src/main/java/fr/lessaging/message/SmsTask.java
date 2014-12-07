package fr.lessaging.message;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import fr.lessaging.utils.UserPref;

/**
 * Created by lheido on 07/12/14.
 */
public class SmsTask extends AsyncTask<Void, Message, Boolean> {

    private MessageTaskCallback callback;
    private long last_sms = -1;
    protected WeakReference<FragmentActivity> act = null;
    private Context context = null;
    private int conversationId;
    private final String sms_uri = "content://sms";
    private final String[] projection = {"*"};
    private String selection = "thread_id = ?";
    private ArrayList<String> selectionArgs = new ArrayList<String>();


    public SmsTask(FragmentActivity activity, int id, MessageTaskCallback callback){
        link(activity);
        conversationId = id;
        this.callback = callback;
        selectionArgs.add("" + conversationId);
    }

    public SmsTask(FragmentActivity activity, int id, long last_id_sms, MessageTaskCallback callback){
        link(activity);
        conversationId = id;
        this.callback = callback;
        last_sms = last_id_sms;
        selection = "thread_id = ? AND _id < ?";
        selectionArgs.add("" + conversationId);
        selectionArgs.add("" + last_sms);
    }

    @Override
    protected void onPreExecute () {
        if(act.get() != null){
            context = act.get().getApplicationContext();
        }
    }

    @Override
    protected void onPostExecute (Boolean result) {
        if (act.get() != null) {
            if(!result)
                Toast.makeText(context, "Problème génération conversation", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(act.get() != null){
            try{
                Uri uri = Uri.parse(sms_uri);
                Cursor query = context.getContentResolver().query(
                        uri,
                        projection,
                        selection,
                        selectionArgs.toArray(new String[selectionArgs.size()]),
                        "date DESC");
                int count = 0;
                if(query != null){
                    long _id; String body; String type; int status; long date; String sender;
                    while(count < UserPref.getMaxSms(context) && query.moveToNext()){
                        _id = query.getLong(query.getColumnIndexOrThrow("_id"));
                        body = query.getString(query.getColumnIndexOrThrow("body"));
//                            type = query.getString(query.getColumnIndexOrThrow("type"));
//                            Log.v("LOG type", "type = "+type+", body = "+body);
                        sender = query.getString(query.getColumnIndexOrThrow("address"));
                        if(sender == null) sender = getUserPhone(context);
                        status = query.getInt(query.getColumnIndexOrThrow("status"));
                        date = query.getLong(query.getColumnIndexOrThrow("date"));
                        Time t = new Time();
                        t.set(date);
                        Message sms = new Message(_id, body, sender, status, t);
                        publishProgress(sms);
                        count += 1;
                    }
                    query.close();
                    if(count == 0 && last_sms == -1){
                        Time now = new Time();
                        now.setToNow();
                        Message sms = new Message(-1L, "Pas de sms", "1", 0, now);
                        publishProgress(sms);
                    }
                    publishProgress();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

            return true;
        }
        return false;
    }

    @Override
    protected void onProgressUpdate (Message... prog){
        if (act.get() != null) {
            if (callback != null) {
                if (prog.length > 0) {
                    callback.onMessageLoaded(prog[0]);
                } else {
                    callback.onLoaded();
                }
            }
        }
    }

    public void link (FragmentActivity pActivity) {
        act = new WeakReference<FragmentActivity>(pActivity);
    }

    public void execTask(){
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
    }

    public static String getUserPhone(Context context){
        TelephonyManager telemamanger = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telemamanger.getLine1Number();
    }
}
