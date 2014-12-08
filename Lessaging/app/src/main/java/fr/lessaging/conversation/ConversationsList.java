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
    private static ListenerCallback listener;

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
            if(listener != null){
                listener.onItemAdded();
            }
        }
    }

    public static int size(){
        if(!listIsNull() && !listIsEmpty()){
            return list.size();
        }
        return 0;
    }

    public static void moveIndexToTop(int index){
        if(listener != null){
            listener.onItemMovedToTop();
        }
    }

    public static void moveConversationToTop(Conversation conversation){
        if(listener != null){
            listener.onItemMovedToTop();
        }
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

    public static void setListener(ListenerCallback listener) {
        if(ConversationsList.listener != null && listener != null){
            if(AppConfig.DEBUG){
                Log.w("setListener", "Multiple calls to setListener method.");
            }
        }
        ConversationsList.listener = listener;
    }

    /**
     * Callback interface for loading conversations list.
     */
    public interface ConversationsListCallback{
        public void onConversationLoaded(Conversation conversation, int index);
        public void onFirstConversationLoaded(Conversation conversation, int index);
//        public void onConversationsListLoaded();
        public void isAlreadyLoaded();
    }

    /**
     * Callback interface for add/move action.
     * Must be set to null when activity/fragment call onPause or onDestroy methods.
     */
    public interface ListenerCallback{
        public void onItemAdded();
        public void onItemMovedToTop();
    }

}
