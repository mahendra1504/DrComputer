package com.example.drcomputer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Viewholder> {

    private List<MyOrderItemModel> myOrderItemModelList;
    FirebaseFirestore firebaseFirestore;

    public MyOrderAdapter(List<MyOrderItemModel> myOrderItemModelList) {
        this.myOrderItemModelList = myOrderItemModelList;
    }

    @NonNull
    @Override
    public MyOrderAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.Viewholder holder, int position) {
        String res = myOrderItemModelList.get(position).getProductImage();
        String title = myOrderItemModelList.get(position).getProductTitle();
        float rating = myOrderItemModelList.get(position).getRating();
        String id = myOrderItemModelList.get(position).getProductID();
        holder.setData(res,title,rating,id);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private TextView productTitle;
        private RatingBar ratingBar;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.my_order_product_image);
            productTitle = itemView.findViewById(R.id.my_order_title);
            ratingBar = itemView.findViewById(R.id.my_order_rating);
            firebaseFirestore = FirebaseFirestore.getInstance();
        }

        private void setData(String res,String title,float rating,String id){
            Glide.with(itemView.getContext()).load(res).apply(new RequestOptions().placeholder(R.drawable.business_laptop)).into(productImage);
            productTitle.setText(title);
            ratingBar.setRating(rating);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    firebaseFirestore.collection("ORDERS").document(id).update("rating",v).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(itemView.getContext(), "Rating Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }
}
