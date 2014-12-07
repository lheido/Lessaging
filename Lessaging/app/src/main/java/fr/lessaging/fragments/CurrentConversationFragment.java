package fr.lessaging.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import fr.lessaging.MainActivity;
import fr.lessaging.R;
import fr.lessaging.adapters.ViewPagerAdapter;
import fr.lessaging.conversation.Conversation;
import fr.lessaging.conversation.ConversationsList;
import fr.lessaging.utils.BuildFragment;

/**
 * Created by lheido on 05/12/14.
 */
public class CurrentConversationFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener, View.OnLongClickListener {
    private static final String ARG_CONVERSATION_INDEX = "CurrentConversationFramgent:number";
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        conversation = ConversationsList.get(getArguments().getInt(ARG_CONVERSATION_INDEX));
        ((MainActivity) activity).onSectionAttached(conversation.getContactName());
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

    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(getActivity().getApplicationContext(),
                conversation.getPhone() + "\n" +
                conversation.getContactName(),
                Toast.LENGTH_LONG).show();
        return true;
    }
}
