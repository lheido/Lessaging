package fr.lessaging.conversation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import fr.lessaging.utils.AppConfig;

/**
 * Created by lheido on 05/12/14.
 */
public class ConversationsList {

    private static ArrayList<Conversation> list;
    private static boolean isFirstLoaded;
    private static int i;

    public static void load(final Context context, final ConversationsListCallback callback){
        if (listIsNull()) {
            list = new ArrayList<>();
        }
        if (listIsEmpty()){
            //populate list
            isFirstLoaded = false;
            i = 0;
            new AsyncTask<Void, Conversation, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    final String[] projection = new String[] {"_id", "date", "message_count", "recipient_ids", "read", "type"};
                    Uri uri = Uri.parse("content://mms-sms/conversations?simple=true");
                    Cursor query = context.getContentResolver().query(uri, projection, null, null, "date DESC");
                    if(query != null) {
                        if (query.moveToFirst()) {
                            do {
                                Conversation conversation = Conversation.getConversationInfo(context, query);
                                publishProgress(conversation);
                            } while (query.moveToNext());
                        }
                        query.close();
                    }
                    return true;
                }

                @Override
                protected void onProgressUpdate (Conversation... prog){
                    Conversation conversation = prog[0];
                    list.add(conversation);
                    if(callback != null){
                        callback.onConversationLoaded(conversation, i);
                        if(!isFirstLoaded){
                            isFirstLoaded = true;
                            callback.onFirstConversationLoaded(conversation, i);
                        }
                    }
                    i++;
                }

            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            callback.isAlreadyLoaded();
        }
    }

    public static Conversation get(int index){
        if(!listIsNull() && !listIsEmpty()) {
            return list.get(index);
        }
        return null;
    }

    public static void add(Conversation conversation){
        if(!listIsNull() && !listIsEmpty()) {
            list.add(conversation);
        }
    }

    public static int size(){
        if(!listIsNull() && !listIsEmpty()){
            return list.size();
        }
        return 0;
    }

    public static void moveIndexToTop(int index){

    }

    public static void moveConversationToTop(Conversation conversation){

    }

    private static boolean listIsNull(){
        boolean result = true;
        if (list != null) {
            result = false;
        } else if (AppConfig.DEBUG) {
            Log.v("ConversationsList", "list = null");
        }
        return result;
    }

    private static boolean listIsEmpty(){
        boolean result = true;
        if (!list.isEmpty()) {
            result = false;
        } else if (AppConfig.DEBUG) {
            Log.v("ConversationsList", "list is empty");
        }
        return result;
    }

    /**
     * Callback interface
     */
    public interface ConversationsListCallback{
        public void onConversationLoaded(Conversation conversation, int index);
        public void onFirstConversationLoaded(Conversation conversation, int index);
//        public void onConversationsListLoaded();
        public void isAlreadyLoaded();
    }

}
