package com.example.drcomputer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity {
    private TextView userFullname,userLocation,userCity,userState,userPincode,userMobno,priceAndItem,totalAmount,lastPrice;
    private Button placeOrderBtn;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        userFullname = findViewById(R.id.order_summary_fullname);
        userLocation = findViewById(R.id.order_summary_location);
        userCity = findViewById(R.id.order_summary_city);
        userPincode = findViewById(R.id.order_summary_pincode);
        userState = findViewById(R.id.order_summary_state);
        userMobno = findViewById(R.id.order_summary_mobno);
        priceAndItem = findViewById(R.id.order_summary_price_items);
        totalAmount = findViewById(R.id.order_summary_total_amount);
        lastPrice = findViewById(R.id.order_summary_last_price);
        placeOrderBtn = findViewById(R.id.order_summary_place_order_btn);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Dr. Computer","Dr. Computer",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (firebaseUser!=null){
            firebaseFirestore.collection("USERS").whereEqualTo("email",firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    userFullname.setText(documentSnapshot.get("fullname").toString());
                                    userLocation.setText(documentSnapshot.get("location").toString());
                                    userCity.setText(documentSnapshot.get("city").toString());
                                    userState.setText(documentSnapshot.get("state").toString());
                                    userPincode.setText(documentSnapshot.get("pincode").toString());
                                    userMobno.setText(documentSnapshot.get("mobno").toString());
                                }
                            }
                            else {
                                Toast.makeText(PlaceOrderActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            firebaseFirestore.collection("CART").whereEqualTo("userID",firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                float price,totalPrice=0f;
                                int item,totalItem=0;
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                    price = Float.parseFloat(queryDocumentSnapshot.get("price").toString());
                                    totalPrice = totalPrice+price;
                                    item = Integer.parseInt(queryDocumentSnapshot.get("productQty").toString());
                                    totalItem = totalItem + item;
                                }
                                totalAmount.setText("Total Amount                                 Rs. "+String.valueOf(totalPrice));
                                priceAndItem.setText("Price ("+String.valueOf(totalItem)+" item)                                           Rs. "+String.valueOf(totalPrice));
                                lastPrice.setText("Rs. "+String.valueOf(totalPrice)+"/-");
                            }
                        }
                    });

            placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(PlaceOrderActivity.this, "Your Order is Placed", Toast.LENGTH_SHORT).show();
                    firebaseFirestore.collection("CART").whereEqualTo("userID",firebaseUser.getEmail()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            Map<String,String> orders = new HashMap<>();
                                            orders.put("rating",documentSnapshot.get("rating").toString());
                                            orders.put("productImage",documentSnapshot.get("productImage").toString());
                                            orders.put("productTitle",documentSnapshot.get("productTitle").toString());
                                            orders.put("userID",firebaseUser.getEmail());
                                            orders.put("location",userLocation.getText().toString());
                                            orders.put("city",userCity.getText().toString());
                                            orders.put("state",userState.getText().toString());
                                            orders.put("pincode",userPincode.getText().toString());
                                            orders.put("fullname",userFullname.getText().toString());
                                            orders.put("mobno",userMobno.getText().toString());

                                            firebaseFirestore.collection("ORDERS").add(orders)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.M)
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(PlaceOrderActivity.this, "Your Order is Placed", Toast.LENGTH_SHORT).show();
                                                                firebaseFirestore.collection("CART").document(documentSnapshot.getId()).delete();
                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(PlaceOrderActivity.this,"Dr. Computer");
                                                                builder.setContentTitle("Dr. Computer");
                                                                builder.setContentText("Your Order is Placed!!Thank you for shopping.");
                                                                builder.setSmallIcon(R.drawable.logo);
                                                                builder.setAutoCancel(true);

                                                                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                                                    @Override
                                                                    public void onInit(int i) {
                                                                        if (i==TextToSpeech.SUCCESS){
                                                                            textToSpeech.setLanguage(Locale.ENGLISH);

                                                                        }
                                                                    }
                                                                });
                                                                textToSpeech.speak("Thank You for Ordering",TextToSpeech.QUEUE_FLUSH,null);

                                                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(PlaceOrderActivity.this);
                                                                managerCompat.notify(1,builder.build());
                                                                try {
                                                                    SmsManager smsManager=SmsManager.getDefault();
                                                                    smsManager.sendTextMessage(userMobno.getText().toString(),null,"Your Order is Placed Successfully. You will get your delivery within 2 days. Thank you for shopping.",null,null);

                                                                }catch (Exception e){
                                                                    Toast.makeText(PlaceOrderActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                                Intent intent = new Intent(PlaceOrderActivity.this,HomeActivity.class);
                                                                startActivity(intent);

                                                           }
                                                            else {
                                                                Toast.makeText(PlaceOrderActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                    else{
                                        Toast.makeText(PlaceOrderActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }
}