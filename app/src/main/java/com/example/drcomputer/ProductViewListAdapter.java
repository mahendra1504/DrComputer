package com.example.drcomputer;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProductViewListAdapter extends RecyclerView.Adapter<ProductViewListAdapter.ViewHolder> {

    private List<ProductViewListModel> productViewListModelList;

    public ProductViewListAdapter(List<ProductViewListModel> productViewListModelList) {
        this.productViewListModelList = productViewListModelList;
    }

    @NonNull
    @Override
    public ProductViewListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewListAdapter.ViewHolder holder, int position) {
        String image = productViewListModelList.get(position).getProductImageLink();
        String id = productViewListModelList.get(position).getProductID();
        String name = productViewListModelList.get(position).getProductName();
        String rating = productViewListModelList.get(position).getProductRating();
        String price = productViewListModelList.get(position).getProductPrice();
        String detail1 = productViewListModelList.get(position).getProductDetail1();
        String detail2 = productViewListModelList.get(position).getProductDetail2();
        String detail3 = productViewListModelList.get(position).getProductDetail3();
        holder.setProduct(image,name,rating,price,detail1,detail2,detail3,position);
    }

    @Override
    public int getItemCount() {
        return productViewListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView productImage;
        private TextView productName;
        private TextView productRating;
        private TextView productPrice;
        private TextView productDetail1;
        private TextView productDetail2;
        private TextView productDetail3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            productImage = itemView.findViewById(R.id.product_view_product_image);
            productName = itemView.findViewById(R.id.product_view_product_name);
            productRating = itemView.findViewById(R.id.product_view_product_rating);
            productPrice = itemView.findViewById(R.id.product_view_product_price);
            productDetail1 = itemView.findViewById(R.id.product_view_product_detail1);
            productDetail2 = itemView.findViewById(R.id.product_view_product_detail2);
            productDetail3 = itemView.findViewById(R.id.product_view_product_detail3);
        }

        @Override
        public void onClick(View view) {
            String pID = productViewListModelList.get(getPosition()).getProductID();
            Intent productDetailsIntent = new Intent(itemView.getContext(),ProductDetailsActivity.class);
            productDetailsIntent.putExtra("Pid",pID);
            itemView.getContext().startActivity(productDetailsIntent);
        }

        private void setProduct(String image, String name, String rating, String price, String detail1, String detail2, String detail3,int position){
            Glide.with(itemView.getContext()).load(image).apply(new RequestOptions().placeholder(R.drawable.business_laptop)).into(productImage);
            productName.setText(name);
            productRating.setText(rating);
            productPrice.setText("Rs. "+price+"/-");
            productDetail1.setText(detail1);
            productDetail2.setText(detail2);
            productDetail3.setText(detail3);
        }
    }
}
