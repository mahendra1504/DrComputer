package com.example.drcomputer;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;


public class SignInFragment extends Fragment {
    private TextView signUpLink;
    private FrameLayout parentFrameLayout;

    private EditText email,password;
    private ImageButton signInClose;
    private Button signInBtn;
    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private ProgressBar progressBar;
    private TextView forgotPassword;
    private TextToSpeech textToSpeech;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_sign_in, container, false);
         signUpLink = view.findViewById(R.id.sign_up_link);

         email = view.findViewById(R.id.sign_in_email);

         password = view.findViewById(R.id.sign_in_password);

         signInClose = view.findViewById(R.id.sign_in_close);

         signInBtn = view.findViewById(R.id.sign_in_button);

         parentFrameLayout = getActivity().findViewById(R.id.login_frame_layout);

         progressBar = view.findViewById(R.id.sign_in_progress_bar);

         forgotPassword = view.findViewById(R.id.sign_in_forgot_password);

         firebaseAuth = FirebaseAuth.getInstance();

         return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignUpFragment());
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login_Activity.onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });

        signInClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeIntent();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailAndPassword();
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slide_out_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs(){
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(password.getText())){
                signInBtn.setEnabled(true);
                signInBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            else{
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            }
        }
        else{
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        }
    }

    private void checkEmailAndPassword(){
        if(email.getText().toString().matches(emailPattern)){
            if(password.length() >= 8){

                progressBar.setVisibility(view.VISIBLE);
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    homeIntent();
                                }
                                else{
                                    progressBar.setVisibility(view.INVISIBLE);
                                    signInBtn.setEnabled(true);
                                    signInBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else
            {
                Toast.makeText(getActivity(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getActivity(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
        }
    }

    private void homeIntent(){
        Intent homeIntent = new Intent(getActivity(),HomeActivity.class);
        startActivity(homeIntent);
        getActivity().finish();
    }
}