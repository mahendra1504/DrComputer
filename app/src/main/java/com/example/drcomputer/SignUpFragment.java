package com.example.drcomputer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends Fragment {
    private TextView signInLink;
    private FrameLayout parentFrameLayout;
    View view;
    private EditText email,fullName,password,confirmPassword;
    private ImageButton closeBtn;
    private Button signUpBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        signInLink = view.findViewById(R.id.sign_in_link);

        email = view.findViewById(R.id.sign_up_email);
        fullName = view.findViewById(R.id.sign_up_full_name);
        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_confirm_password);

        closeBtn = view.findViewById(R.id.sign_up_close);

        signUpBtn = view.findViewById(R.id.sign_up_button);

        progressBar = view.findViewById(R.id.sign_up_progress_bar);

        parentFrameLayout = getActivity().findViewById(R.id.login_frame_layout);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
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

        fullName.addTextChangedListener(new TextWatcher() {
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

        confirmPassword.addTextChangedListener(new TextWatcher() {
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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To do send Data to Firebase
                checkEmailAndPassword();
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs(){
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(fullName.getText())){
                if(!TextUtils.isEmpty(password.getText()) && password.length()>=8){
                    if(!TextUtils.isEmpty(confirmPassword.getText())){
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                    else{
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    }
                }
                else{
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                }
            }
            else{
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            }
        }
        else{
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        }
    }

    private void checkEmailAndPassword(){

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.ic_error);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());
        if(email.getText().toString().matches(emailPattern)){
            if(password.getText().toString().equals(confirmPassword.getText().toString())){

                progressBar.setVisibility(view.VISIBLE);

                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));

                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Map<Object,String> userData = new HashMap<>();
                                    userData.put("email",email.getText().toString());
                                    userData.put("password",password.getText().toString());
                                    userData.put("fullname",fullName.getText().toString());
                                    userData.put("mobno","Not Set");
                                    userData.put("location","Not Set");
                                    userData.put("city","Not Set");
                                    userData.put("state","Not Set");
                                    userData.put("pincode","Not Set");
                                    firebaseFirestore.collection("USERS")
                                            .add(userData)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if(task.isSuccessful()){
                                                        homeIntent();
                                                    }else{
                                                        progressBar.setVisibility(view.INVISIBLE);
                                                        signUpBtn.setEnabled(true);
                                                        signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(),error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }else{
                                    progressBar.setVisibility(view.INVISIBLE);
                                    signUpBtn.setEnabled(true);
                                    signUpBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(),error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else{
                confirmPassword.setError("Password doesn't matched!",customErrorIcon);
            }
        }else{
            email.setError("Invalid Email ID!",customErrorIcon);
        }
    }

    private void homeIntent(){
        Intent homeIntent = new Intent(getActivity(),HomeActivity.class);
        startActivity(homeIntent);
        getActivity().finish();
    }
}