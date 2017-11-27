package com.dwizzel.thekids;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dwizzel.utils.Tracer;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendInvitationForWatchingFragment2 extends Fragment {

    private static final String TAG = "SendInvitationForWatchingFragment2";
    private View fragmentView;
    private static Bundle args;

    public SendInvitationForWatchingFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //bundle
        args = getArguments();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_invitation_for_watching_2, container, false);

        /*
        getResources().getString(R.string.toast_connected_as_and_last,
                UserObject.getInstance().getEmail(),
                UserObject.getInstance().getLastConnection(SignInUserWithEmailActivity.this))
        */

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //le view du fragment
        fragmentView = view;
        boolean hasProfile = false;
        //check si on avait des erreur ou un phone et name a ajouter
        if(args != null && !args.isEmpty()){
            try {
                String phone = "";
                String message = "";
                String inviteId = "";
                if(args.containsKey("phone")){
                    phone = args.getString("phone");
                }
                if(args.containsKey("message")){
                    message = args.getString("message");
                }
                if(args.containsKey("inviteId")){
                    inviteId = args.getString("inviteId");
                }
                //on fait le tittre
                String completeTitle = getResources().getString(R.string.sms_invitation_title,
                        phone);
                //on fait le long message
                String completeMessage = getResources().getString(R.string.sms_invitation_message,
                        message, inviteId);
                //on show le message
                ((TextView)fragmentView.findViewById(R.id.textTitle)).setText(completeTitle);
                ((TextView)fragmentView.findViewById(R.id.textDescription)).setText(completeMessage);


            }catch(Exception e){
                Tracer.log(TAG, "onViewCreated.ARGS.exception: ", e);
            }

        }

        //
        Button buttSend = fragmentView.findViewById(R.id.buttSend);
        //butt create
        buttSend.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //TODO: send Sms
                    }
                });

    }

    private void showSpinner(boolean show){
        //le bouton et le spinner
        ProgressBar progressBar = fragmentView.findViewById(R.id.loading_spinner);
        Button buttSend = fragmentView.findViewById(R.id.buttSend);
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            buttSend.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            buttSend.setVisibility(View.VISIBLE);
        }
    }

}
