package fr.lessaging.message;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;

import fr.lessaging.utils.UserPref;

/**
 * Created by lheido on 08/12/14.
 */
public class MmsTask extends AsyncTask<Void, Message, Boolean> {

    private final String mms_uri = "content://mms";
    private final String[] projection = {"*"};
    private MessageTaskCallback callback;
    private long last_sms = -1;
    private String selection = "thread_id = ?";
    private ArrayList<String> selectionArgs = new ArrayList<String>();

    private final int conversationId;
    private Context context;
    protected WeakReference<FragmentActivity> act;

    public MmsTask(FragmentActivity activity, int conversationId, MessageTaskCallback callback){
        link(activity);
        this.conversationId = conversationId;
        this.callback = callback;
        selectionArgs.add(""+conversationId);
    }

    public MmsTask(FragmentActivity activity, int conversationId, long last_id, MessageTaskCallback callback) {
        link(activity);
        this.conversationId = conversationId;
        this.callback = callback;
        last_sms = last_id;
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
            Cursor allMms = context.getContentResolver().query(
                    Uri.parse(mms_uri),
                    projection,
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    "date DESC");

            if(allMms != null){
//                    for (int i = 0; i < allMms.getColumnCount(); i++) {
//                        Log.v("LheidoSMS Log MMS", ""+allMms.getColumnName(i));
//                    }
                int count = 0;
                while(count < UserPref.getMaxSms(context) && allMms.moveToNext()){
                    long mmsId = allMms.getLong(allMms.getColumnIndexOrThrow("_id"));
                    int read = allMms.getInt(allMms.getColumnIndexOrThrow("read"));
                    String senderAdd = getAddressNumber(mmsId);
                    if(senderAdd == null) senderAdd = MessageManager.getUserPhone(context);
//                        Log.v("LHEIDO SMS LOG MMS", "_id = "+mmsId+",\n sender = "+senderAdd);
                    Message mms = getMMSData(mmsId, senderAdd);
                    long date = allMms.getLong(allMms.getColumnIndex("date"));
                    Time t = new Time();
                    t.set(date);
                    mms.setDate(t);
                    publishProgress(mms);
                    //add_(_id, string, type, read, t, 1, liste);
                    count += 1;
                }
                allMms.close();
                if(count == 0 && last_sms == -1){
                    Time now = new Time();
                    now.setToNow();
                    Message sms = new Message(-1L, "Pas de mms", "1", 0,"1", now);
                    publishProgress(sms);
                }
                publishProgress();
            }
            return true;
        }
        return false;
    }

    private String getAddressNumber(long id) {
        String selectionAdd = "msg_id=" + id;
        String uriStr = MessageFormat.format("content://mms/{0}/addr", id);
        Uri uriAddress = Uri.parse(uriStr);
        Cursor cAdd = context.getContentResolver().query(uriAddress, null,
                selectionAdd, null, null);
        String name = null;
        if (cAdd != null) {
            if (cAdd.moveToFirst()) {
                do {
                    String number = cAdd.getString(cAdd.getColumnIndex("address"));
                    if (number != null) {
                        try {
                            Long.parseLong(number.replace("-", ""));
                            name = number;
                        } catch (NumberFormatException nfe) {
                            if (name == null) {
                                name = number;
                            }
                        }
                    }
                } while (cAdd.moveToNext());
            }
            cAdd.close();
        }
        return name;
    }

    private Message getMMSData(long mmsId, String sender) {
        Message mms = null;
        String selectionPart = "mid=" + mmsId;
        Uri uri = Uri.parse("content://mms/part");
        try{
            mms = new Message();
            Cursor cPart = context.getContentResolver().query(uri, new String[] {"*"}, selectionPart, null, null);
            if(cPart != null) {
//                    for (int i = 0; i < cPart.getColumnCount(); i++) {
//                        Log.v("LheidoSMS Log MMS content://mms/part", ""+cPart.getColumnName(i));
//                    }
                if (cPart.moveToFirst()) {
                    do {
                        mms.setId(mmsId);
                        mms.setSender(sender);
                        String partId = cPart.getString(cPart.getColumnIndex("_id"));
                        String type = cPart.getString(cPart.getColumnIndex("ct"));
                        if ("image/jpeg".equals(type) || "image/bmp".equals(type) ||
                                "image/gif".equals(type) || "image/jpg".equals(type) ||
                                "image/png".equals(type)) {
                            mms.setUriPicture(getMmsImageUri(partId));
                        }
                        if ("text/plain".equals(type)) {
                            String data = cPart.getString(cPart.getColumnIndex("_data"));
                            String body;
                            if (data != null) {
                                body = getMmsText(partId);
                            } else {
                                body = cPart.getString(cPart.getColumnIndex("text"));
                            }
                            mms.setBody(body);
                        }
                    } while (cPart.moveToNext());
                    cPart.close();
                }
            }
        }catch(Exception ex){ex.printStackTrace();}
        return mms;
    }

    private String getMmsText(String partId) {
        Uri partURI = Uri.parse("content://mms/part/" + partId);
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = context.getContentResolver().openInputStream(partURI);
            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                String temp = reader.readLine();
                while (temp != null) {
                    sb.append(temp);
                    temp = reader.readLine();
                }
            }
        } catch (IOException e) {e.printStackTrace();}
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {e.printStackTrace();}
            }
        }
        return sb.toString();
    }

    private Uri getMmsImageUri(String partId) {
        return Uri.parse("content://mms/part/" + partId);
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
}
