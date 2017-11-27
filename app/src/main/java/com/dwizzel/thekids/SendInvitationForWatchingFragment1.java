package com.dwizzel.thekids;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dwizzel.Const;
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
        boolean hasProfile = false;
        //check si on avait des erreur ou un phone et name a ajouter
        if(args != null && !args.isEmpty()){
            try {
                if(args.containsKey("msg")){
                    displayErrMsg(args.getInt("msg"));
                }
                if(args.containsKey("name")){
                    String name = args.getString("name");
                    if(name!= null && !name.isEmpty()){
                        ((EditText)fragmentView.findViewById(R.id.userName)).setText(name);
                        hasProfile = true;
                    }
                }
                if(args.containsKey("phone")){
                    String phone = args.getString("phone");
                    if(phone != null && !phone.isEmpty()){
                        ((EditText)fragmentView.findViewById(R.id.userPhone)).setText(phone);
                        hasProfile = true;
                    }
                }
                //on set le focus sur le message
                if(hasProfile){
                    ((EditText)fragmentView.findViewById(R.id.userMessage)).requestFocus();
                }

            }catch(Exception e){
                Tracer.log(TAG, "onViewCreated.ARGS.exception: ", e);
            }

        }

        //
        Button buttNext = fragmentView.findViewById(R.id.buttNext);
        //butt create
        buttNext.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        checkMandatoryFieldsAndCreate();
                    }
                });

    }

    private void checkMandatoryFieldsAndCreate(){
        showSpinner(true);
        displayErrMsg(Const.error.NO_ERROR);
        //on va chercher les infos et on les sets
        String name = String.format("%s",((EditText)fragmentView.findViewById(R.id.userName)).getText());
        String phone = String.format("%s",((EditText)fragmentView.findViewById(R.id.userPhone)).getText());
        String message = String.format("%s",((EditText)fragmentView.findViewById(R.id.userMessage)).getText());
        //minor check
        if(name.isEmpty()){
            displayErrMsg(R.string.err_no_name);
            return;
        }
        if(phone.isEmpty()){
            displayErrMsg(R.string.err_no_phone);
            return;
        }
        if(message.isEmpty()){
            displayErrMsg(R.string.err_no_message);
            return;
        }

        //on a le tout allors on creer l'invitation
        ((SendInvitationForWatchingActivity) getActivity()).createInviteId(phone, name, message);

    }

    private void displayErrMsg(int msgId){
        showSpinner(false);
        TextView txtView = fragmentView.findViewById(R.id.errMsg);
        if(msgId != 0) {
            txtView.setText(msgId);
        }else {
            txtView.setText("");
        }
    }

    private void showSpinner(boolean show){
        //le bouton et le spinner
        ProgressBar progressBar = fragmentView.findViewById(R.id.loading_spinner);
        Button buttNext = fragmentView.findViewById(R.id.buttNext);
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            buttNext.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            buttNext.setVisibility(View.VISIBLE);
        }
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
