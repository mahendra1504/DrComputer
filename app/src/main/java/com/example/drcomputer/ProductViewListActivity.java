package com.example.drcomputer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductViewListActivity extends AppCompatActivity {
    private RecyclerView productViewListRecyclerview;
    private ProductViewListAdapter productViewListAdapter;
    private List<ProductViewListModel> productViewListModelList;
    private FirebaseFirestore firebaseFirestore;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view_list);

        Toolbar toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productViewListRecyclerview = findViewById(R.id.product_view_list_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductViewListActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        productViewListRecyclerview.setLayoutManager(linearLayoutManager);

        bundle = getIntent().getExtras();
        String catName = bundle.getString("CategoryName");
        productViewListModelList = new ArrayList<ProductViewListModel>();
        firebaseFirestore = FirebaseFirestore.getInstance();


        productViewListAdapter = new ProductViewListAdapter(productViewListModelList);
        productViewListRecyclerview.setAdapter(productViewListAdapter);

        firebaseFirestore.collection("PRODUCTS").whereEqualTo("category",catName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                productViewListModelList.add(new ProductViewListModel(queryDocumentSnapshot.get("productImage1").toString(),queryDocumentSnapshot.get("productTitle").toString(),queryDocumentSnapshot.get("price").toString(),queryDocumentSnapshot.get("rating").toString(),queryDocumentSnapshot.get("detail1").toString(),queryDocumentSnapshot.get("detail2").toString(),queryDocumentSnapshot.get("detail3").toString(), queryDocumentSnapshot.getId()));
                            }
                            productViewListAdapter.notifyDataSetChanged();
                        }
                        else{
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductViewListActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_cart_icon, menu);
        MenuItem item = menu.findItem(R.id.main_search_icon);

        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //processSearch(s);
                return false;
            }
        });
        return true;
    }

    private void processSearch(String s) {
        firebaseFirestore.collection("PRODUCTS").orderBy("productTitle").startAt(s).endAt(s+"\uf8ff").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                productViewListModelList.add(new ProductViewListModel(queryDocumentSnapshot.get("productImage1").toString(), queryDocumentSnapshot.get("productTitle").toString(), queryDocumentSnapshot.get("price").toString(), queryDocumentSnapshot.get("rating").toString(), queryDocumentSnapshot.get("detail1").toString(), queryDocumentSnapshot.get("detail2").toString(), queryDocumentSnapshot.get("detail3").toString(), queryDocumentSnapshot.getId()));
                            }
                            productViewListAdapter.notifyDataSetChanged();
                        }
                        else{
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductViewListActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.main_cart_icon){
            //To-DO Cart
            return true;
        }
        else if(id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}