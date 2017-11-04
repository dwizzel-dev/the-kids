package com.dwizzel.thekids;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class CreateUserWithEmailFragment0 extends Fragment {

    private View fragmentView;
    private static Bundle args;

    public CreateUserWithEmailFragment0() {
        // Required empty public constructor
    }

    public static CreateUserWithEmailFragment0 newInstance() {
        CreateUserWithEmailFragment0 fragment = new CreateUserWithEmailFragment0();
        args = new Bundle();
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
        //bundle
        args = getArguments();
        // Inflate the layout for this fragment
        return inflater.
                inflate(R.layout.fragment_create_user_with_email_0, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //le view du fragment
        fragmentView = view;
        //check si on avait des erreur dun fragment precedent ou autres dans les args
        if(args != null && !args.isEmpty()){
            displayErrMsg(args.getInt("msg"));
        }
        //check si pas deja entrer un email pour le reafficher
        String email  = ((CreateUserWithEmailActivity)getActivity()).getEmail();
        if(!email.isEmpty()) {
            ((EditText)fragmentView.findViewById(R.id.userEmail)).setText(String.format("%s", email));
        }
        //
        final Button buttNextToPsw = fragmentView.findViewById(R.id.buttNextToPsw);
        //butt create
        buttNextToPsw.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //get the email entered
                        String email = String.format("%s",((EditText)fragmentView.findViewById(R.id.userEmail)).getText());
                        //problem set an error message
                        displayErrMsg(((CreateUserWithEmailActivity)getActivity())
                                    .setEmailFromFragment(email));
                        //go to the next fragment for psw
                    }
                });
    }


    private void displayErrMsg(int msgId){
        TextView txtView = fragmentView.findViewById(R.id.errMsg);
        if(msgId != 0) {
            txtView.setText(msgId);
        }else {
            txtView.setText("");
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
