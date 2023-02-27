package com.example.drcomputer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        SystemClock.sleep(3000);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            Intent loginIntent = new Intent(MainActivity.this,Login_Activity.class);
            startActivity(loginIntent);
            finish();
        }
        else{
            Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }
}