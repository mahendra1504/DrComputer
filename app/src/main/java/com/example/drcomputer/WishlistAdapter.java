package com.example.drcomputer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<WishlistModel> wishlistModelList;
    private FirebaseFirestore firebaseFirestore;

    public WishlistAdapter(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String res = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        String rat = wishlistModelList.get(position).getRating();
        String price = wishlistModelList.get(position).getProductPrice();
        String id = wishlistModelList.get(position).getProductID();
        holder.setData(res,title,price,rat);
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView rating;
        private TextView productPrice;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.wishlist_product_image);
            productTitle = itemView.findViewById(R.id.wishlist_product_title);
            productPrice = itemView.findViewById(R.id.wishlist_product_price);
            rating = itemView.findViewById(R.id.wishlist_product_rating);
            deleteBtn = itemView.findViewById(R.id.wishlist_delete_btn);
            firebaseFirestore = FirebaseFirestore.getInstance();
        }

        private void setData(String res,String title,String price,String rat){
            Glide.with(itemView.getContext()).load(res).apply(new RequestOptions().placeholder(R.drawable.business_laptop)).into(productImage);
            productTitle.setText(title);
            productPrice.setText("Rs. "+price+"/-");
            rating.setText(rat);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = wishlistModelList.get(getPosition()).getProductID();
                    firebaseFirestore.collection("WISHLIST").document(id).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        wishlistModelList.remove(getAdapterPosition());
                                        notifyDataSetChanged();
                                        Toast.makeText(itemView.getContext(), "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });
        }
    }
}
