package com.example.drcomputer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressItemAdapter extends RecyclerView.Adapter<AddressItemAdapter.ViewHolder>{
    private List<AddressItemModel> addressItemModelList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String userDocumentID;

    public AddressItemAdapter(List<AddressItemModel> addressItemModelList) {
        this.addressItemModelList = addressItemModelList;

    }

    @NonNull
    @Override
    public AddressItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_address_layout,parent,false);
        return new AddressItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressItemAdapter.ViewHolder holder, int position) {
        String loc = addressItemModelList.get(position).getLocation();
        String ct = addressItemModelList.get(position).getCity();
        String st = addressItemModelList.get(position).getState();
        String pc = addressItemModelList.get(position).getPincode();
        holder.setData(loc,ct,st,pc);
    }

    @Override
    public int getItemCount() {
        return addressItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView location;
        private TextView city;
        private TextView state;
        private TextView pincode;
        private ImageButton editAdrress;
        private ImageButton deleteAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.recycler_view_location);
            city = itemView.findViewById(R.id.recycler_view_city);
            state = itemView.findViewById(R.id.recycler_view_state);
            pincode = itemView.findViewById(R.id.recycler_view_pincode);
            editAdrress = itemView.findViewById(R.id.recycler_view_address_edit_btn);
            deleteAddress = itemView.findViewById(R.id.recycler_view_address_delete_btn);
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }


        private void setData(String loc,String ct,String st,String pc){
            location.setText(loc);
            city.setText(ct);
            state.setText(st);
            pincode.setText(pc);


            editAdrress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (firebaseUser!=null) {
                        firebaseFirestore.collection("USERS").whereEqualTo("email", firebaseUser.getEmail()).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                userDocumentID = documentSnapshot.getId().toString();
                                                //Toast.makeText(view.getContext(), firebaseUser.getEmail()+" "+userDocumentID, Toast.LENGTH_SHORT).show();
                                                firebaseFirestore.collection("USERS").document(userDocumentID).update("location", addressItemModelList.get(getPosition()).getLocation(), "city", addressItemModelList.get(getPosition()).getCity(), "state", addressItemModelList.get(getPosition()).getState(), "pincode", addressItemModelList.get(getPosition()).getPincode())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(view.getContext(), "Address Set as Primary Address", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(view.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                        else {
                                            Toast.makeText(view.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
            });

            deleteAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = addressItemModelList.get(getPosition()).getAddressID();

                    firebaseFirestore.collection("ADDRESSES").document(id).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        addressItemModelList.remove(getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                    else{
                                        Toast.makeText(itemView.getContext(), "Not Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }

    }


}
