package com.example.drcomputer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MyAddressesFragment extends Fragment {

    private RecyclerView addressItemRecyclerView;
    private Button addNewAddressBtn;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_addresses, container, false);

        addNewAddressBtn = view.findViewById(R.id.my_address_add_new_address_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        addressItemRecyclerView = view.findViewById(R.id.my_address_recycler_view);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        addressItemRecyclerView.setLayoutManager(linearLayout);


        List<AddressItemModel> addressItemModelList = new ArrayList<>();
        AddressItemAdapter addressItemAdapter = new AddressItemAdapter(addressItemModelList);
        addressItemRecyclerView.setAdapter(addressItemAdapter);

        if (firebaseUser!=null){
            firebaseFirestore.collection("ADDRESSES").whereEqualTo("userID",firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    addressItemModelList.add(new AddressItemModel(documentSnapshot.get("location").toString(),documentSnapshot.get("city").toString(),documentSnapshot.get("state").toString(),documentSnapshot.get("pincode").toString(), documentSnapshot.getId()));
                                }
                                addressItemAdapter.notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAdrressIntent = new Intent(getActivity(),AddNewAddressActivity.class);
                startActivity(addAdrressIntent);
            }
        });


        return view;
    }

    public boolean newMethod(){
        return true;
    }
}