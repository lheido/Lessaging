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

    public static void load(final Context context, final ConversationsListCallback callback){
        if (listIsNull()) {
            list = new ArrayList<Conversation>();
        }
        if (listIsEmpty()){
            //populate list
            isFirstLoaded = false;
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
                                list.add(conversation);
                                if(callback != null){
                                    callback.onConversationLoaded(conversation);
                                    if(!isFirstLoaded){
                                        isFirstLoaded = true;
                                        callback.onFirstConversationLoaded(conversation);
                                    }
                                }
                            } while (query.moveToNext());
                        }
                        query.close();
                    }
                    return true;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        public abstract void onConversationLoaded(Conversation conversation);
        public abstract void onFirstConversationLoaded(Conversation conversation);
    }

}
