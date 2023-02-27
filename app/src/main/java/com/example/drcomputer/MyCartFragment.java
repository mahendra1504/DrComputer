package com.example.drcomputer;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MyCartFragment extends Fragment {

   private RecyclerView cartItemsRecyclerView;
   private FirebaseFirestore firebaseFirestore;
   private FirebaseUser firebaseUser;
   private Button continueBtn;
   private TextView totalAmountTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);
        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerview);
        continueBtn = view.findViewById(R.id.cart_continue_btn);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(layoutManager);
        totalAmountTV = view.findViewById(R.id.total_cart_amount);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser==null){
            Intent intent = new Intent(getContext(),Login_Activity.class);
            startActivity(intent);
        }

        List<CartItemModel> cartItemModelList = new ArrayList<>();
        CartAdapater cartAdapater = new CartAdapater(cartItemModelList);
        cartItemsRecyclerView.setAdapter(cartAdapater);
        if(firebaseUser!=null) {
            firebaseFirestore.collection("CART").whereEqualTo("userID", firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                float totalAmount=0f,price;
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    cartItemModelList.add(new CartItemModel(Integer.parseInt(documentSnapshot.get("type").toString()), documentSnapshot.get("productImage").toString(), documentSnapshot.get("productTitle").toString(), documentSnapshot.get("price").toString(), Integer.parseInt(documentSnapshot.get("productQty").toString()), documentSnapshot.getId()));
                                    price = Float.parseFloat(documentSnapshot.get("price").toString());
                                    totalAmount = totalAmount+price;
                                }
                                cartAdapater.notifyDataSetChanged();
                                totalAmountTV.setText("Rs. "+String.valueOf(totalAmount)+ "/-");
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent placeOrderIntent = new Intent(getActivity(),PlaceOrderActivity.class);
                startActivity(placeOrderIntent);
            }
        });

        return view;
    }
}