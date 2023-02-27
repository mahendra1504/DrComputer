package com.example.drcomputer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;
    private FloatingActionButton addToWishlistBtn;
    private static boolean ADDED_TO_WISHLIST=false;
    private List<String> productImages;
    private FirebaseFirestore firebaseFirestore;
    private TextView productTitle;
    private TextView productRating;
    private TextView productPrice;
    private TextView productSpecs;
    private Bundle bundle;
    private Map<String,String> cartProductHashMap;
    private Map<String,String> wishlistProductHashMap;
    private Button productAddToCart;
    private String userID;
    private FirebaseUser firebaseUser;
    private List<WishlistModel> wishlistModelList;
    private String productIdForWishlist;
    private String PT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = getIntent().getExtras();
        String Pname = bundle.getString("Pid");

        productImagesViewPager = findViewById(R.id.product_images_view_pager);
        viewPagerIndicator = findViewById(R.id.view_pager_indicator);
        addToWishlistBtn = findViewById(R.id.add_to_wishlist);
        productTitle = findViewById(R.id.product_title);
        productRating = findViewById(R.id.wishlist_product_rating);
        productPrice = findViewById(R.id.product_price);
        productSpecs = findViewById(R.id.product_specification);
        productAddToCart = findViewById(R.id.product_add_to_cart_btn);
        wishlistModelList = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        productImages = new ArrayList<String>();
        cartProductHashMap = new HashMap<>();
        wishlistProductHashMap = new HashMap<>();


            firebaseFirestore.collection("PRODUCTS").document(Pname).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                for (long x = 1; x <= (long) documentSnapshot.get("no_productImages"); x++) {
                                    productImages.add(documentSnapshot.get("productImage" + x).toString());

                                }
                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                productImagesViewPager.setAdapter(productImagesAdapter);
                                productTitle.setText(documentSnapshot.get("productTitle").toString());
                                PT = documentSnapshot.get("productTitle").toString();
                                productRating.setText(documentSnapshot.get("rating").toString());
                                productPrice.setText("Rs. " + documentSnapshot.get("price").toString() + "/-");
                                /*String specs = documentSnapshot.get("specification").toString();
                                String[] s = specs.split("\\|");
                                String newSpecs = "";
                                for (int i=0;i<=s.length;i++){
                                    newSpecs.concat(s[i]);
                                }*/
                                productSpecs.setText(documentSnapshot.get("specification").toString());

                                if (firebaseUser!=null) {
                                    wishlistProductHashMap.put("userID",firebaseUser.getEmail());
                                    wishlistProductHashMap.put("price",documentSnapshot.get("price").toString());
                                    wishlistProductHashMap.put("productTitle", documentSnapshot.get("productTitle").toString());
                                    wishlistProductHashMap.put("productImage", documentSnapshot.get("productImage1").toString());
                                    wishlistProductHashMap.put("rating",documentSnapshot.get("rating").toString());
                                    cartProductHashMap.put("userID", firebaseUser.getEmail());
                                    cartProductHashMap.put("price", documentSnapshot.get("price").toString());
                                    cartProductHashMap.put("productTitle", documentSnapshot.get("productTitle").toString());
                                    cartProductHashMap.put("productQty", "1");
                                    cartProductHashMap.put("type", "0");
                                    cartProductHashMap.put("productImage", documentSnapshot.get("productImage1").toString());
                                    cartProductHashMap.put("rating",documentSnapshot.get("rating").toString());
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



        addToWishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ADDED_TO_WISHLIST==false){
                    ADDED_TO_WISHLIST=true;
                    addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.errorRed));
                    firebaseFirestore.collection("WISHLIST").add(wishlistProductHashMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ProductDetailsActivity.this, "Product Added Successfully in your Wishlist", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    ADDED_TO_WISHLIST=false;
                    addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                    Toast.makeText(ProductDetailsActivity.this, "Product Already Added!!!", Toast.LENGTH_SHORT).show();
                    firebaseFirestore.collection("WISHLIST").whereEqualTo("productTitle",PT).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            firebaseFirestore.collection("WISHLIST").document(documentSnapshot.getId()).delete();
                                        }
                                    }
                                }
                            });
                }
            }
        });

            productAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseFirestore.collection("CART").add(cartProductHashMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProductDetailsActivity.this, "Product Added Successfully to your Cart", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_cart_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            finish();
            return true;
        }
        else if(id==R.id.main_cart_icon){
            //To-DO Cart
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}