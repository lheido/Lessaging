package fr.lessaging.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fr.lessaging.R;
import fr.lessaging.conversation.Conversation;
import fr.lessaging.conversation.ConversationsList;

/**
 * Created by lheido on 06/12/14.
 */
public class ConversationsListAdapter extends BaseAdapter{
    private final Context mContext;

    public ConversationsListAdapter(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public int getCount() {
        return ConversationsList.size();
    }

    @Override
    public Object getItem(int i) {
        return ConversationsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int r, View convertView, ViewGroup parent) {
        Conversation conversation = (Conversation) this.getItem(r);

        ListeConversationViewHolder holder;
        if (convertView == null) {
            holder = new ListeConversationViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.conversations_list, parent, false);
            holder.mName = (TextView) convertView.findViewById(R.id.list_conversation_contact_name);
            holder.mCount = (TextView) convertView.findViewById(R.id.list_conversation_count);
            holder.mContactPicture = (ImageView) convertView.findViewById(R.id.contactPict);
            //holder.mLayout = (RelativeLayout) convertView.findViewById(R.id.message_relativeLayout);
            convertView.setTag(holder);
        } else {
            holder = (ListeConversationViewHolder) convertView.getTag();
        }

        holder.mName.setText(conversation.getContactName());
        holder.mCount.setText("" + conversation.getNbSms());
        if(conversation.hasNewMessage())
            holder.mName.setBackgroundResource(R.drawable.bg_conversation_name_new_message);
        else
            holder.mName.setBackgroundResource(R.drawable.bg_conversation_name);
        Picasso.with(mContext).load(conversation.getContactPict()).fit().centerCrop().into(holder.mContactPicture);

        return convertView;
    }

    static class ListeConversationViewHolder {
        //public RelativeLayout mLayout;
        public TextView mName;
        public TextView mCount;
        public ImageView mContactPicture;
    }
}
