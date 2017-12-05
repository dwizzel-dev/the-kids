package com.dwizzel.thekids;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Trace;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dwizzel.utils.Tracer;

import static android.app.Activity.RESULT_OK;

public class SendInvitationForWatchingFragment0 extends Fragment implements ISendInvitationForWatchingFragment {

    private static final String TAG = "SendInvitationForWatchingFragment0";
    private View fragmentView;
    private static Bundle args;

    private static final int RESULT_PICK_CONTACT = 32000;

    public SendInvitationForWatchingFragment0() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bundle
        args = getArguments();
        // Inflate the layout for this fragment
        return inflater.
                inflate(R.layout.fragment_send_invitation_for_watching_0, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //le view du fragment
        fragmentView = view;
        //check si on avait des erreur dun fragment precedent ou autres dans les args
        if(args != null && !args.isEmpty()){
            displayErrMsg(args.getInt("msg"));
        }
        //
        Button buttSelectFromContact = fragmentView.findViewById(R.id.buttSelectFromContact);
        Button buttEnterNewPhoneNumber = fragmentView.findViewById(R.id.buttEnterNewPhoneNumber);
        //butt create
        buttSelectFromContact.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        pickContact();
                    }
                });
        buttEnterNewPhoneNumber.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //on call l'activity principale qui va changer de fragment
                        ((SendInvitationForWatchingActivity)getActivity())
                                .gotoFragment(1, null);
                    }
                });

    }

    public void pickContact() {
        Tracer.log(TAG, "pickContact");
        try {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
        }catch(Exception e){
            Tracer.log(TAG, "pickContact.exception: ", e);
        }
    }

    private void onContactPicked(Intent data){
        Tracer.log(TAG, "onContactPicked");
        Cursor cursor = null;
        try {
            String phone = "";
            String name = "";
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            if(uri != null) {
                //Query the content uri
                Context context = getContext();
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if(cursor != null) {
                    cursor.moveToFirst();
                    // column index of the phone number
                    int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    // column index of the contact name
                    int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    phone = cursor.getString(phoneIndex);
                    name = cursor.getString(nameIndex);
                    cursor.close();
                    gotoSendMessageFragment(name, phone);
                }
            }
        } catch (Exception e) {
            Tracer.log(TAG, "onContactPicked.exception: ", e);
        }
    }

    private void gotoSendMessageFragment(String name, String phone){
        Tracer.log(TAG, String.format("gotoSendMessageFragment: %s | %s", name, phone));
        //on set le bundle avec les args
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("phone", phone);
        //on call l'activity principale qui va changer de fragment avec les arguments a ecrire
        ((SendInvitationForWatchingActivity)getActivity())
                .gotoFragment(1, bundle);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tracer.log(TAG, "onActivityResult: " + requestCode);
        // check whether the result is ok
        try {
            if(resultCode == RESULT_OK) {
                // Check for the request code,
                // we might be usign multiple startActivityForReslut
                switch (requestCode) {
                    case RESULT_PICK_CONTACT:
                        onContactPicked(data);
                        break;
                }
            }
        }catch(Exception e){
            Tracer.log(TAG, "onActivityResult.exception: ", e);
        }
    }


    public void displayErrMsg(int msgId){
        Tracer.log(TAG, "displayErrMsg: " + msgId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
