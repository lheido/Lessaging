package fr.lessaging.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.twotoasters.jazzylistview.JazzyListView;

import java.util.ArrayList;

import fr.lessaging.R;
import fr.lessaging.adapters.SmsBaseAdapter;
import fr.lessaging.conversation.Conversation;
import fr.lessaging.message.Message;
import fr.lessaging.utils.UserPref;

/**
 * Created by lheido on 30/10/14.
 */
public abstract class SmsBaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ARG_CONVERSATION_ID = "conversation_id";
    public static final String ARG_CONVERSATION_COUNT = "conversation_count";
    public static final String ARG_CONTACT_PHONE = "contact_phone";
    public static final String ARG_CONVERSATION_NUMBER = "conversation_number";
    public static final String ARG_CONTACT_NAME = "contact_name";

    protected Context context;
    protected String name;
    protected String phoneContact;
    protected int conversationId; // id for database
    protected long conversation_nb_sms;
    protected JazzyListView liste;
    protected int list_conversationId; // id for global conversations list
    protected ArrayList<Message> Message_list;
    protected SmsBaseAdapter mAdapter;
    protected SwipeRefreshLayout swipeLayout;

    public static void setArgs(SmsBaseFragment fragment, Conversation conversation, int position){
        Bundle args = new Bundle();
        args.putInt(ARG_CONVERSATION_NUMBER, position);
        args.putString(ARG_CONTACT_NAME, conversation.getContactName());
        args.putString(ARG_CONTACT_PHONE, conversation.getPhone());
        args.putInt(ARG_CONVERSATION_ID, Integer.parseInt(conversation.getConversationId()));
        args.putLong(ARG_CONVERSATION_COUNT, conversation.getNbSms());
        fragment.setArguments(args);
    }

    public SmsBaseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = initRootView(inflater, container);
        // init context, view, etc...
        context = getActivity();
        name = getArguments().getString(ARG_CONTACT_NAME);
        phoneContact = getArguments().getString(ARG_CONTACT_PHONE);
        conversationId = getArguments().getInt(ARG_CONVERSATION_ID);
        conversation_nb_sms = getArguments().getLong(ARG_CONVERSATION_COUNT);
        list_conversationId = getArguments().getInt(ARG_CONVERSATION_NUMBER);
        Message_list = new ArrayList<Message>();
        liste = initList(rootView);
        liste.setTransitionEffect(UserPref.getConversationEffect(context));
        liste.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        liste.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                initListOnItemLongClick(adapterView, view, position, id);
                return true;
            }
        });
        liste.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                initListOnItemClick(adapterView, view, position, id);
            }
        });
        initConversationAdapter();
        liste.setAdapter(mAdapter);
//        load_conversation();
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        return rootView;
    }

    public String getPhoneContact() {
        return phoneContact;
    }

    public void add__(Message sms, int position, boolean notify){
        if(position != 0){
            Message_list.add(sms);
        } else{
            Message_list.add(0, sms);
        }
        if(notify)
            mAdapter.notifyDataSetChanged();
    }

    public void userAddSms(Message sms, int position){
        add__(sms, position, true);
        conversation_nb_sms += 1;
        liste.smoothScrollToPosition(liste.getBottom());
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateFragment();
    }

    public void updateFragment(){
        if(liste != null) {
            liste.setTransitionEffect(UserPref.getConversationEffect(context));
        }
        if(Message_list == null){
            Message_list = new ArrayList<>();
        } else {
            Message_list.clear();
        }
        load_conversation();
    }

    @Override
    public void onRefresh() {
        try {
            long last_id = Message_list.get(Message_list.size() - 1).getId();
            // save index and top position
            final int index = liste.getFirstVisiblePosition();
            View v = liste.getChildAt(Message_list.size() - 1);
            final int top = (v == null) ? 0 : v.getTop();
            final int start_count = liste.getCount();
            load_more_conversation(last_id, index, top, start_count);
        }catch (Exception e){
            Toast.makeText(context, "Error onRefresh", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * return rootView from appropriate layout (conversation or conversation.mms).
     *
     * @param inflater
     * @param container
     * @return : code -> inflater.inflate(R.layout.conversation, container, false);
     */
    protected abstract View initRootView(LayoutInflater inflater, ViewGroup container);

    /**
     * init list attribute with custom layout.
     * @param rootView
     */
    protected abstract JazzyListView initList(View rootView);

    /**
     * init adapter with appropriate adapter (ConversationSmsAdapter/ConversationMmsAdapter).
     */
    protected abstract void initConversationAdapter();

    /**
     * init onItemClick.
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    protected abstract void initListOnItemClick(AdapterView<?> adapterView, View view, int position, long id);

    /**
     * init onItemLongClick.
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    protected abstract void initListOnItemLongClick(AdapterView<?> adapterView, View view, int position, long id);

    /**
     * load conversation with appropriate task (SMS or MMS).
     */
    protected abstract void load_conversation();

    /**
     * load more conversation with appropriate task (SMS or MMS).
     * @param last_id
     * @param index
     * @param top
     * @param start_count
     */
    protected abstract void load_more_conversation(final long last_id, final int index, final int top, final int start_count);

}
