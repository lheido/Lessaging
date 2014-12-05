package fr.lessaging.conversation;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

/**
 * Created by lheido on 05/12/14.
 */
public class Conversation {
    private String mContactName = null;
    private String mLastSMS = null;
    private long mNbSms = -1;
    private String mContactPhone = null;
    private String mConversationId = null;
    private Uri mContactPict;
    private long mContactId;
    private boolean mMarkNewMessage = false;
    private String mAccountType = null;

    public Conversation(){
        // Empty constructor
    }
    /**
     *
     * @param contactName            : contact name.
     * @param contactPhone           : contact phone number
     * @param nbSms          : sms count
     * @param conversationId : conversation id to fetch database
     */
    public Conversation(String contactName, String contactPhone, long nbSms, String conversationId){
        this.mContactName = contactName;
        this.mContactPhone = contactPhone;
        this.mNbSms = nbSms;
        this.mConversationId = conversationId;
    }

    public Conversation newInstance(){
        Conversation c = new Conversation();
        c.setContactName(this.mContactName);
        c.setContactPhone(this.mContactPhone);
        c.setContactId(this.mContactId);
        c.setConversationId(this.mConversationId);
        c.setContactPict();
        c.setNbSms("" + this.mNbSms);
        c.setAccountType(this.mAccountType);
        return c;
    }

    public String getContactName(){
        return mContactName;
    }
    public String getLastsms(){
        return mLastSMS;
    }
    public long getNbSms(){
        return mNbSms;
    }
    public String getPhone(){
        return mContactPhone;
    }
    public String getConversationId(){
        return mConversationId;
    }
    public void setNbSms(String count){
        this.mNbSms = Integer.parseInt(count);
    }
    public void setContactPhone(String address){
        this.mContactPhone = address;
    }
    public void setConversationId(String id){
        this.mConversationId = id;
    }

    public Uri getContactPict(){
        return this.mContactPict;
    }

    public void setContactPict(){
        this.mContactPict = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, this.mContactId);
    }

    public long getContactId() {
        return this.mContactId;
    }
    public void setContactId(long id_){
        this.mContactId = id_;
    }
    public void setContactName(String string) {
        this.mContactName = string;
    }

    public void nbSmsIncrese() {
        this.mNbSms += 1;
    }

    public void markNewMessage(Boolean val) {
        this.mMarkNewMessage = val;
    }

    public boolean hasNewMessage() {
        return this.mMarkNewMessage;
    }

    public void setAccountType(String accountType) {
        this.mAccountType = accountType;
    }

    public String getAccountType() {
        return this.mAccountType;
    }

    /**
     * static methods to retrieve contact and conversation info from database.
     */

    public static String retrieveContactName(Context context, String address){
        String res = "";
        Cursor cur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if(cur != null){
            String name = "";
            while(name.equals("") && cur.moveToNext()){
                String phone = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //Log.v("LHEIDO SMS LOG", phone + ", " + address);
                if(name.equals("") && PhoneNumberUtils.compare(phone, address)){
                    name = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    res += name;
                }
            }
            if(name.equals(""))
                res += address;
            cur.close();
        }
        return res;
    }

    public static void retrieveContact(Context context, Conversation conversation, String phone){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        Cursor cur = context.getContentResolver().query(uri, projection, null, null, null);
        if(cur != null){
            if(cur.moveToFirst()){
                try{
                    conversation.setContactId(cur.getLong(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID)));
                }catch(Exception ex){
                    Toast.makeText(context, "Error setId\n" + ex.toString(), Toast.LENGTH_LONG).show();
                }
                try{
                    conversation.setContactName(cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                }catch(Exception ex){
                    Toast.makeText(context, "Error setName\n"+ex.toString(), Toast.LENGTH_LONG).show();
                }
                try{
                    conversation.setContactPict();
                }catch(Exception ex){
                    Toast.makeText(context, "Error setPic\n"+ex.toString(), Toast.LENGTH_LONG).show();
                }
            }else
                conversation.setContactName(phone);
            cur.close();
        }
    }

    public static Conversation getConversationInfo(Context context, Cursor query){
        Conversation conversation = new Conversation();
        conversation.setConversationId(query.getString(query.getColumnIndex("_id")));
        conversation.setNbSms(query.getString(query.getColumnIndex("message_count")));
        String recipientId = query.getString(query.getColumnIndex("recipient_ids"));
        String[] recipientIds = recipientId.split(" ");
        for (String recipientId1 : recipientIds) {
            Uri ur = Uri.parse("content://mms-sms/canonical-addresses");
            if (!recipientId1.equals("")) {
                Cursor cr = context.getContentResolver().query(ur, new String[]{"*"}, "_id = " + recipientId1, null, null);
                if (cr != null) {
                    while (cr.moveToNext()) {
                        //String id = cr.getString(0);
                        String address = cr.getString(1);
                        conversation.setContactPhone(address);
                        retrieveContact(context, conversation, address);
                    }
                    cr.close();
                }
            }
        }
        return conversation;
    }


    public static String getMessageCount(Context context, String id){
        String res = null;
        try {
            final String[] projection = new String[]{"_id", "message_count"};
            Uri uri = Uri.parse("content://mms-sms/conversations?simple=true");
            Cursor query = context.getContentResolver().query(uri, projection, null, null, "date DESC");
            if(query != null){
                boolean find = false;
                while(query.moveToNext() && !find) {
                    if(query.getString(query.getColumnIndex("_id")).equals(id)) {
                        res = query.getString(query.getColumnIndex("message_count"));
                        find = true;
                    }
                }
                query.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}

