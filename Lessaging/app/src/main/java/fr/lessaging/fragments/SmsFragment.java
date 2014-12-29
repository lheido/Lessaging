package fr.lessaging.fragments;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.twotoasters.jazzylistview.JazzyListView;


import java.util.ArrayList;

import fr.lessaging.R;
import fr.lessaging.adapters.SmsAdapter;
import fr.lessaging.message.Message;
import fr.lessaging.message.MessageTaskCallback;
import fr.lessaging.message.SmsTask;
import fr.lessaging.receiver.MessageReceiver;


/**
 * Created by lheido on 31/10/14.
 */
public class SmsFragment extends SmsBaseFragment implements MessageReceiver.MessageReceiverCallback {

    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.conversation, container, false);
    }

    @Override
    protected com.twotoasters.jazzylistview.JazzyListView initList(View rootView) {
        return (JazzyListView)rootView.findViewById(R.id.list_conversation);
    }

    @Override
    protected void initConversationAdapter() {
        mAdapter = new SmsAdapter(context, phoneContact, Message_list);
    }

    @Override
    protected void initListOnItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        liste.requestFocus();
    }

    @Override
    protected void initListOnItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        int sdk = Build.VERSION.SDK_INT;
        if(sdk < Build.VERSION_CODES.HONEYCOMB){
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(Message_list.get(Message_list.size()-1-position).getBody());
        } else{
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("simple text", Message_list.get(Message_list.size()-1-position).getBody());
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, R.string.message_copy, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        MessageReceiver.removeCallback(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        MessageReceiver.addCallback(this);
    }

    @Override
    protected void load_conversation() {
        new SmsTask(getActivity(), conversationId, new MessageTaskCallback() {
            @Override
            public void onMessageLoaded(Message message) {
                add__(message, 1, true);
                liste.smoothScrollToPosition(liste.getBottom());
            }

            @Override
            public void onLoaded() {}
        }).execTask();
    }

    @Override
    protected void load_more_conversation(final long last_id, final int index, final int top,
                                          final int start_count) {
        new SmsTask(getActivity(), conversationId, last_id, new MessageTaskCallback() {
            @Override
            public void onMessageLoaded(Message message) {
                add__(message, -1, false);
            }

            @Override
            public void onLoaded() {
                swipeLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
                int finalposition = index + liste.getCount() - start_count;
                liste.setSelection(finalposition);
                liste.smoothScrollToPosition(finalposition - 1);
            }
        }).execTask();
    }

    @Override
    public void onReceivedNewSms(Message newMessage, String senderName) {
        if (PhoneNumberUtils.compare(newMessage.getSender(), phoneContact) || name.equals(senderName)) {
            add__(newMessage, 0, true);
            liste.smoothScrollToPosition(liste.getBottom());
        }
    }

    @Override
    public void onDelivered(long id) {
        if(id != -1){
            int k = 0;
            boolean find = false;
            while (!find && k < Message_list.size()) {
                if (id == Message_list.get(k).getId()) {
                    find = true;
                    Message_list.get(k).setRead(true);
                    mAdapter.notifyDataSetChanged();
                }
                k++;
            }
        }
    }
}
