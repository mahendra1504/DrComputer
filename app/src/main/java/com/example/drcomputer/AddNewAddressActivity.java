package com.example.drcomputer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddNewAddressActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private EditText location,city,state,pincode;
    private Button addAddressBtn;
    private HashMap<String,String> addressHashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        location = findViewById(R.id.add_address_location);
        city = findViewById(R.id.add_address_city);
        state = findViewById(R.id.add_address_state);
        pincode = findViewById(R.id.add_address_pincode);
        addAddressBtn = findViewById(R.id.add_address_btn);
        addressHashMap = new HashMap<>();

        location.addTextChangedListener(new TextWatcher() {
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

        city.addTextChangedListener(new TextWatcher() {
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

        state.addTextChangedListener(new TextWatcher() {
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

        pincode.addTextChangedListener(new TextWatcher() {
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

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressHashMap.put("userID",firebaseUser.getEmail());
                addressHashMap.put("location",location.getText().toString());
                addressHashMap.put("city",city.getText().toString());
                addressHashMap.put("state",state.getText().toString());
                addressHashMap.put("pincode",pincode.getText().toString());

                firebaseFirestore.collection("ADDRESSES").add(addressHashMap)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AddNewAddressActivity.this, "Address Added Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(AddNewAddressActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    private void checkInputs(){
        if(!TextUtils.isEmpty(location.getText())){
            if(!TextUtils.isEmpty(city.getText())){
                if(!TextUtils.isEmpty(state.getText())){
                    if(TextUtils.isDigitsOnly(pincode.getText())){
                        addAddressBtn.setEnabled(true);
                        addAddressBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                    else{
                        addAddressBtn.setEnabled(false);
                        addAddressBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                    }
                }
                else{
                    addAddressBtn.setEnabled(false);
                    addAddressBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
                }
            }
            else{
                addAddressBtn.setEnabled(false);
                addAddressBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            }
        }
        else{
            addAddressBtn.setEnabled(false);
            addAddressBtn.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        }
    }

}