package fr.lessaging.utils;

/**
 * Created by lheido on 31/10/14.
 */


import fr.lessaging.conversation.Conversation;
import fr.lessaging.fragments.MmsFragment;
import fr.lessaging.fragments.SmsBaseFragment;
import fr.lessaging.fragments.SmsFragment;

/**
 * SMS/MMS fragment builder.
 */
public class BuildFragment {
    public static SmsFragment SMS(Conversation conversation, int position){
        SmsFragment fragment = new SmsFragment();
        SmsBaseFragment.setArgs(fragment, conversation, position);
        return fragment;
    }
    public static MmsFragment MMS(Conversation conversation, int position){
        MmsFragment fragment = new MmsFragment();
        SmsBaseFragment.setArgs(fragment, conversation, position);
        return fragment;
    }
}
