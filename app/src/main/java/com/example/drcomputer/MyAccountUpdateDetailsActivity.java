package com.example.drcomputer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MyAccountUpdateDetailsActivity extends AppCompatActivity {

    private EditText fullNameUpdate,mobileNoUpdate,emailUpdate;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userID;
    private Button editDetailsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_update_details);
        fullNameUpdate = findViewById(R.id.my_account_edit_fullname);
        mobileNoUpdate = findViewById(R.id.my_account_edit_mobileno);
        emailUpdate = findViewById(R.id.my_account_edit_email);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        editDetailsBtn = findViewById(R.id.my_account_edit_details_btn);

        if (firebaseUser!=null){
            firebaseFirestore.collection("USERS").whereEqualTo("email",firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                userID = documentSnapshot.getId();
                                fullNameUpdate.setText(documentSnapshot.get("fullname").toString());
                                mobileNoUpdate.setText(documentSnapshot.get("mobno").toString());
                                emailUpdate.setText(documentSnapshot.get("email").toString());
                            }
                        }
                    });

            editDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseFirestore.collection("USERS").document(userID).update("fullname",fullNameUpdate.getText().toString(),"mobno",mobileNoUpdate.getText().toString(),"email",emailUpdate.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MyAccountUpdateDetailsActivity.this, "Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(MyAccountUpdateDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    firebaseUser.updateEmail(emailUpdate.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MyAccountUpdateDetailsActivity.this, "Email Updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MyAccountUpdateDetailsActivity.this,HomeActivity.class);
                                startActivity(intent);

                            }
                        }
                    });
                }
            });

        }

    }
}