package fr.lessaging.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import fr.lessaging.MainActivity;
import fr.lessaging.R;
import fr.lessaging.adapters.ViewPagerAdapter;
import fr.lessaging.conversation.Conversation;
import fr.lessaging.conversation.ConversationsList;
import fr.lessaging.message.Message;
import fr.lessaging.message.MessageManager;
import fr.lessaging.preferences.LessagingPreference;
import fr.lessaging.utils.BuildFragment;
import fr.lessaging.utils.UserPref;

/**
 * Created by lheido on 05/12/14.
 */
public class CurrentConversationFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener, View.OnLongClickListener {
    private static final String ARG_CONVERSATION_INDEX = "CurrentConversationFramgent:number";
    private static final int PICK_CONTACT = 100;
    private static final int PAGE_SMS = 0;
    private static final int PAGE_MMS = 1;
    private ViewPager mViewPager;
    private EditText mEditText;
    private ImageButton mButton;
    private ArrayList<Fragment> mPages;
    private ViewPagerAdapter mViewPagerAdapter;
    private String mmsImgPath;
    private Conversation conversation;

    public static CurrentConversationFragment newInstance(int index){
        CurrentConversationFragment fragment = new CurrentConversationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CONVERSATION_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversation = ConversationsList.get(getArguments().getInt(ARG_CONVERSATION_INDEX));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_conversation, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mEditText = (EditText) rootView.findViewById(R.id.send_body);
        mButton = (ImageButton) rootView.findViewById(R.id.send_button);
        mPages = new ArrayList<Fragment>();

        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mPages);
        mViewPager.setAdapter(mViewPagerAdapter);
        mPages.add(BuildFragment.SMS(conversation, getArguments().getInt(ARG_CONVERSATION_INDEX)));
        mPages.add(BuildFragment.MMS(conversation, getArguments().getInt(ARG_CONVERSATION_INDEX)));
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setOnPageChangeListener(this);

        mButton.setOnClickListener(this);
        mButton.setOnLongClickListener(this);

        mmsImgPath = null;

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        NavigationDrawerFragment fragment = (NavigationDrawerFragment)getFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (fragment != null && !fragment.isDrawerOpen()) {
            inflater.inflate(R.menu.conversation, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_call){
            Intent call = new Intent(Intent.ACTION_CALL);
            call.setData(Uri.parse("tel:" + conversation.getPhone()));
            startActivity(call);
            return true;
        } else if(id == R.id.action_voir_contact) {
            if (!PhoneNumberUtils.isGlobalPhoneNumber(conversation.getContactName())) {
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, conversation.getContactId());
                Intent look = new Intent(Intent.ACTION_VIEW, contactUri);
                look.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(look);
            } else {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, conversation.getPhone());
                startActivityForResult(intent, PICK_CONTACT);
            }
            return true;
        } else if(id == R.id.action_remove_conversation){
//            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity().getApplicationContext());
//
//            alert.setTitle(R.string.alert_dialog_remove_conversation_title);
//            alert.setMessage(R.string.alert_dialog_remove_conversation_msg);
//
//            alert.setPositiveButton(R.string.alert_dialog_remove_conversation_yes, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    try {
//                        Intent i = new Intent(getActivity().getApplicationContext(), RemoveConversationService.class);
//                        i.putExtra("conversationId", Global.conversationsList.get(currentConversation).getConversationId());
//                        startService(i);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            alert.setNegativeButton(R.string.alert_dialog_remove_conversation_no, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    // Canceled.
//                }
//            });
//
//            alert.show();

        } else if (id == R.id.action_settings) {
            Intent intent;
            intent = new Intent(getActivity().getApplicationContext(), LessagingPreference.class);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }
//        else if(id == R.id.action_delete_old){
//            if(userPref.old_message) {
//                Intent i = new Intent(getApplicationContext(), DeleteOldSMSService.class);
//                startService(i);
//            }else{
//                Toast.makeText(this, R.string.old_message_false, Toast.LENGTH_SHORT).show();
//            }
//        }
        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(conversation.getContactName());
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        ((MainActivity) activity).onSectionAttached(conversation.getContactName());
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int position) {
        if(mButton != null){
            if (position == 0) //page sms
                mButton.setImageResource(R.drawable.send_sms);
            else {
                if(mmsImgPath == null)
                    mButton.setImageResource(R.drawable.send_mms);
                else
                    mButton.setImageResource(R.drawable.send_sms);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    /**
     * Method called when user want to send message
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (UserPref.hideKeyboardIsEnabled(getActivity().getApplicationContext())) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        switch (mViewPager.getCurrentItem()){
            case PAGE_SMS:
                String body;
                if (mEditText.getText() != null) {
                    body = mEditText.getText().toString();
                } else
                    body = "";
                if (body.length() > 0) {
                    Message newSms = new Message();
                    newSms.setBody(body);
                    newSms.setRead(false);
                    Time now = new Time();
                    now.setToNow();
                    newSms.setDate(now);
                    long threadId = Conversation.getOrCreateThreadId(getActivity().getApplicationContext(), conversation.getPhone());
                    MessageManager.store(getActivity().getApplicationContext(), conversation.getPhone(), newSms, threadId);
                    MessageManager.send(getActivity().getApplicationContext(), newSms, conversation.getPhone());
                    ConversationsList.moveConversationToTop(conversation);
                    mEditText.setText(R.string.empty_sms);
                    ((SmsFragment) mPages.get(PAGE_SMS)).userAddSms(newSms.getId(), body, newSms.getSender(), 32, now, 0);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.empty_message, Toast.LENGTH_LONG).show();
                }
                break;

            case PAGE_MMS:

                break;

            default: break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        String calculatedLength = "";
        if(mEditText != null) {
            int[] len = SmsMessage.calculateLength(mEditText.getText(), false);
            calculatedLength = "\nnumber of sms required : " + len[0] + "\n";
            calculatedLength += "number of code units used : "+ len[1] + "\n";
            calculatedLength += "number of code units remaining until the next message : "+ len[2];
        }
        Toast.makeText(getActivity().getApplicationContext(),
                conversation.getPhone() + "\n" + conversation.getContactName() + calculatedLength,
                Toast.LENGTH_LONG)
             .show();
        return true;
    }
}
