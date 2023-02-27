package com.example.drcomputer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapater extends RecyclerView.Adapter {
    private List<CartItemModel> cartItemModelList;

    public CartAdapater(List<CartItemModel> cartItemModelList) {
        this.cartItemModelList = cartItemModelList;
    }

    @Override
    public int getItemViewType(int position) {
       switch (cartItemModelList.get(position).getType()) {
           case 0:
               return CartItemModel.CART_ITEM;
           case 1:
               return CartItemModel.TOTAL_AMOUNT;
           default:
               return -1;
       }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case CartItemModel.CART_ITEM :
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                return new CartItemViewHolder(cartItemView);

                case CartItemModel.TOTAL_AMOUNT:

                    View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout,parent,false);
                    return new CartItemViewHolder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (cartItemModelList.get(position).getType()){
                case CartItemModel.CART_ITEM :
                    String resourse = cartItemModelList.get(position).getProductImage();
                    String title = cartItemModelList.get(position).getProductTitle();
                    String price = cartItemModelList.get(position).getProductPrice();
                    int qty = cartItemModelList.get(position).getProductQty();

                    ((CartItemViewHolder)holder).setItemDetails(resourse,title,price,qty);
                    break;

                    case CartItemModel.TOTAL_AMOUNT:
                        int tqty = cartItemModelList.get(position).getTotalItems();
                        String tprice = cartItemModelList.get(position).getTotalAmount();
                        String pprice = cartItemModelList.get(position).getProductPrice();
                        ((CartTotalAmountViewHolder)holder).setItemDetails(tqty,tprice,pprice);
                        break;
            }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView productQty;

        private Button deleteBtn;
        private FirebaseFirestore firebaseFirestore;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_product_image);
            productTitle = itemView.findViewById(R.id.cart_product_title);
            productPrice = itemView.findViewById(R.id.cart_product_price);
            productQty = itemView.findViewById(R.id.cart_product_quantity);

            deleteBtn = itemView.findViewById(R.id.cart_remove_product);
            firebaseFirestore = FirebaseFirestore.getInstance();
        }


        private void setItemDetails(String res,String title,String price,int qty){
            Glide.with(itemView.getContext()).load(res).apply(new RequestOptions().placeholder(R.drawable.business_laptop)).into(productImage);
            productTitle.setText(title);
            productQty.setText(Integer.toString(qty));
            productPrice.setText("Rs. "+price+"/-");

            productQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = cartItemModelList.get(getPosition()).getCartItemID();
                    Dialog quantityDialog = new Dialog(itemView.getContext());
                    quantityDialog.setContentView(R.layout.quantity_dialog);
                    quantityDialog.setCancelable(false);
                    quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    EditText qtyNo = quantityDialog.findViewById(R.id.quantity_number);
                    Button cancelBtn = quantityDialog.findViewById(R.id.qty_cancel_btn);
                    Button okBtn = quantityDialog.findViewById(R.id.qty_ok_btn);

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            quantityDialog.dismiss();
                        }
                    });

                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            quantityDialog.dismiss();
                            firebaseFirestore.collection("CART").document(id).update("productQty",qtyNo.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                float priceUpdate = Float.parseFloat(cartItemModelList.get(getPosition()).getProductPrice());
                                                int qtyUpdate = Integer.parseInt(qtyNo.getText().toString());
                                                float itemAmount = priceUpdate*qtyUpdate;
                                                String itemAmoutString = String.valueOf(itemAmount);
                                                firebaseFirestore.collection("CART").document(id).update("price",itemAmoutString);
                                                setItemDetails(res,title,itemAmoutString,qtyUpdate);
                                            }
                                            else{
                                                String error = task.getException().getMessage();
                                                Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                    quantityDialog.show();
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String id = cartItemModelList.get(getPosition()).getCartItemID();
                    firebaseFirestore.collection("CART").document(id).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        cartItemModelList.remove(getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                }
            });
        }
    }

    public class CartTotalAmountViewHolder extends RecyclerView.ViewHolder{
        private TextView totalAmountQty;
        private TextView totalAmountProductPrice;
        private TextView totalAmountTotalPrice;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalAmountQty = itemView.findViewById(R.id.total_amount_total_items);
            totalAmountProductPrice = itemView.findViewById(R.id.cart_final_product_price);
            totalAmountTotalPrice = itemView.findViewById(R.id.cart_final_total_amount);
        }

        private void setItemDetails(int qty,String price,String total){
            totalAmountQty.setText(Integer.toString(qty));
            totalAmountProductPrice.setText(price);
            totalAmountTotalPrice.setText(total);
        }
    }
}
