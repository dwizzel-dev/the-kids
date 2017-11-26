package com.dwizzel.thekids;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dwizzel.utils.Tracer;

public class SendInvitationForWatchingFragment1 extends Fragment {

    private static final String TAG = "SendInvitationForWatchingFragment1";
    private View fragmentView;
    private static Bundle args;


    public SendInvitationForWatchingFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bundle
        args = getArguments();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_invitation_for_watching_1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //le view du fragment
        fragmentView = view;
        //check si on avait des erreur ou un phone et name a ajouter
        if(args != null && !args.isEmpty()){
            try {
                String name = "";
                String phone = "";
                if(args.containsKey("msg")){
                    displayErrMsg(args.getInt("msg"));
                }
                if(args.containsKey("name")){
                    name = args.getString("name");
                    if(!name.isEmpty()){
                        ((EditText)fragmentView.findViewById(R.id.userName)).setText(name);
                    }
                }
                if(args.containsKey("phone")){
                    phone = args.getString("phone");
                    if(!phone.isEmpty()){
                        ((EditText)fragmentView.findViewById(R.id.userPhone)).setText(phone);
                    }
                }
            }catch(Exception e){
                Tracer.log(TAG, "onViewCreated.ARGS.exception: ", e);
            }

        }
    }

    private void displayErrMsg(int msgId){
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
