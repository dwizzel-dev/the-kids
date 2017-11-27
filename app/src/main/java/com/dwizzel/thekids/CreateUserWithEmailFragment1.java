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


public class CreateUserWithEmailFragment1 extends Fragment {

    private View fragmentView;
    private static Bundle args;

    public CreateUserWithEmailFragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_user_with_email_1, container, false);
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
        String psw = ((CreateUserWithEmailActivity)getActivity()).getPsw();
        if(!psw.isEmpty()) {
            ((EditText)fragmentView.findViewById(R.id.userPsw0)).setText(String.format("%s", psw));
            ((EditText)fragmentView.findViewById(R.id.userPsw1)).setText(String.format("%s", psw));
        }
        //
        final Button buttRegister = fragmentView.findViewById(R.id.buttRegister);
        //butt create
        buttRegister.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        showSpinner(true);
                        //get the email entered
                        String[] psw = new String[2];
                        psw[0] = String.format("%s", ((EditText)fragmentView.findViewById(R.id.userPsw0)).getText());
                        psw[1] = String.format("%s", ((EditText)fragmentView.findViewById(R.id.userPsw1)).getText());
                        //set errors or not
                        displayErrMsg(((CreateUserWithEmailActivity)getActivity())
                                    .setPswFromFragment(psw));
                        //go to the next fragment for psw



                    }
                });
    }

    private void displayErrMsg(int msgId){
        TextView txtView = fragmentView.findViewById(R.id.errMsg);
        if(msgId != 0) {
            showSpinner(false);
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

    private void showSpinner(boolean show){
        //le bouton et le spinner
        ProgressBar progressBar = fragmentView.findViewById(R.id.loading_spinner);
        Button buttRegister = fragmentView.findViewById(R.id.buttRegister);
        if(show){
            progressBar.setVisibility(View.VISIBLE);
            buttRegister.setVisibility(View.INVISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            buttRegister.setVisibility(View.VISIBLE);
        }
    }



}
