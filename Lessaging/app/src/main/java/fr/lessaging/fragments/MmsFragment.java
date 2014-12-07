package fr.lessaging.fragments;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyListView;

import fr.lessaging.R;
import fr.lessaging.adapters.MmsAdapter;

/**
 * Created by lheido on 31/10/14.
 */
public class MmsFragment extends SmsBaseFragment {
    private ImageButton zoom;

    @Override
    protected View initRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.conversation_mms, container, false);
    }

    @Override
    protected com.twotoasters.jazzylistview.JazzyListView initList(View rootView) {
        // add extra view only with MMS
        zoom = (ImageButton)rootView.findViewById(R.id.expanded_image);
        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoom.setVisibility(View.INVISIBLE);
            }
        });
        return (JazzyListView)rootView.findViewById(R.id.list_conversation_mms);
    }

    @Override
    protected void initBroadcastReceiver() {
//        mBroadCast = new MmsFragmentReceiver() {
//            @Override
//            protected void customNotifyDelivered(long id) {
//
//            }
//
//            @Override
//            protected void customNotifyReceive(long id, String sender, String body, long date, boolean isRead) {
//
//            }
//        };
//        filter = ((MmsFragmentReceiver) mBroadCast).getIntentFilter(3000);
//        mBroadCast = new SmsReceiver(){
//            @Override
//            public void customReceivedSMS() {}
//
//            @Override
//            public void customReceivedMMS() {
//                if(PhoneNumberUtils.compare(phoneContact, phone) && !mOnPause){
////                    updateFragment();
//                    //on est dans la bonne conversation !
////                    Time t = new Time();
////                    t.set(date);
////                    add_(-1L, body, phoneContact, 0, t, 0);
////                    conversation_nb_sms += 1;
////                    liste.smoothScrollToPosition(liste.getBottom());
////                    LheidoUtils.Send.newMessageRead(context, list_conversationId, phoneContact);
//                }
//            }
//
//            @Override
//            public void customNewMessageRead(int position, String phone) {}
//
//            @Override
//            public void customDelivered(long _id){
//                int k = 0;
//                boolean find = false;
//                while(!find && k < Message_list.size()){
//                    if(_id == Message_list.get(k).getId()){
//                        find = true;
//                        Message_list.get(k).setRead(true);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                    k++;
//                }
//            }
//        };
//        filter.addAction(LheidoUtils.ACTION_RECEIVE_MMS);
    }

    @Override
    protected void initConversationAdapter() {
        mAdapter = new MmsAdapter(context, phoneContact, Message_list);
    }

    @Override
    protected void initListOnItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Uri uri = Message_list.get(Message_list.size()-1-position).getUriPicture();
        try{
            Picasso.with(context).load(uri).resize(zoom.getMeasuredWidth()-zoom.getPaddingLeft()*2, zoom.getMeasuredHeight()-zoom.getPaddingTop()*2).centerInside().into(zoom);
        }catch (Exception e){
            e.printStackTrace();
        }
        zoom.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initListOnItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

    }

    @Override
    protected void load_conversation() {
//        LheidoUtils.MmsTask loadTask = new LheidoUtils.MmsTask(getActivity(), conversationId) {
//            @Override
//            protected void onProgressUpdate(Message... prog) {
//                if (this.act.get() != null) {
//                    add__(prog[0], 1);
//                }
//            }
//        };
//        loadTask.execTask();
    }

    @Override
    protected void load_more_conversation(final long last_id, final int index, final int top, final int start_count) {
//        LheidoUtils.MmsTask more = new LheidoUtils.MmsTask(getActivity(), conversationId, last_id) {
//            @Override
//            protected void onProgressUpdate(Message... prog) {
//                if (this.act.get() != null) {
//                    Message_list.add(prog[0]);
//                }
//            }
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(Boolean result) {
//                if (act.get() != null) {
//                    if (!result)
//                        Toast.makeText(context, "Problème génération conversation", Toast.LENGTH_LONG).show();
//                    swipeLayout.setRefreshing(false);
//                    mAdapter.notifyDataSetChanged();
//                    int finalposition = index + liste.getCount() - start_count;
//                    liste.setSelectionFromTop(finalposition, top);
//                }
//            }
//        };
//        more.execTask();
    }
}
