package com.example.drcomputer;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordFragment extends Fragment {
    private View view;

    private EditText registeredEmail;
    private Button resetPasswordBtn;
    private TextView goBack;
    private FrameLayout parentFrameLayout;
    private FirebaseAuth firebaseAuth;
    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailIconText;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_reset_password, container, false);

        registeredEmail = view.findViewById(R.id.forgot_password_email);

        resetPasswordBtn = view.findViewById(R.id.reset_password_button);

        parentFrameLayout = getActivity().findViewById(R.id.login_frame_layout);

        goBack = view.findViewById(R.id.forgot_password_go_back);

        emailIconContainer = view.findViewById(R.id.forgot_password_email_icon_container);

        emailIcon = view.findViewById(R.id.forgot_password_email_icon);

        emailIconText = view.findViewById(R.id.forgot_password_email_icon_text);

        progressBar = view.findViewById(R.id.forgot_password_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registeredEmail.addTextChangedListener(new TextWatcher() {
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

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                resetPasswordBtn.setEnabled(false);
                resetPasswordBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);


                firebaseAuth.sendPasswordResetEmail(registeredEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    emailIcon.setImageResource(R.drawable.ic_baseline_email_24);
                                    emailIconText.setVisibility(View.VISIBLE);
                                    emailIconText.setText("Recovery Email send Successfully! Check it.");
                                    emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
                                }
                                else{
                                    String error = task.getException().getMessage();
                                    progressBar.setVisibility(View.GONE);
                                    emailIconText.setText(error);
                                    emailIconText.setTextColor(getResources().getColor(R.color.errorRed));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);
                                    resetPasswordBtn.setEnabled(true);
                                    resetPasswordBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    private void checkInputs(){
        if(TextUtils.isEmpty(registeredEmail.getText())){
            resetPasswordBtn.setEnabled(false);
            resetPasswordBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        }
        else{
            resetPasswordBtn.setEnabled(true);
            resetPasswordBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}