package com.example.drcomputer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private FrameLayout frameLayout;

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int MY_ORDER_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int MY_ACCOUNT_FRAGMENT = 4;
    private TextView userName,email;
    private ImageView actionBarLogo;

    private static int currentFragment=-1;
    private NavigationView navigationView;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private StorageReference firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private TextView userEmailID;
    private TextToSpeech textToSpeech;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.my_cart_toolbar);
        actionBarLogo = findViewById(R.id.action_bar_logo);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,0,0);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_my_orders, R.id.nav_my_address, R.id.nav_my_cart,R.id.nav_my_wishlist,R.id.nav_my_account)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance().getReference("images");
        //Toast.makeText(this, firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();

        View hView = navigationView.getHeaderView(0);


        if(firebaseUser!=null) {
            TextView nav_user = hView.findViewById(R.id.main_full_name);
            TextView nav_userEmail = hView.findViewById(R.id.main_email);
            ImageView nav_profilePhoto = hView.findViewById(R.id.main_profile_image);
            firebaseFirestore.collection("USERS").whereEqualTo("email", firebaseUser.getEmail()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    nav_user.setText(queryDocumentSnapshot.get("fullname").toString());
                                    nav_userEmail.setText(queryDocumentSnapshot.get("email").toString());
                                    Glide.with(HomeActivity.this).load(queryDocumentSnapshot.get("profileImage")).apply(new RequestOptions().placeholder(R.drawable.my_account_circle_view)).into(nav_profilePhoto);
                                }
                            } else {
                                Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.nav_dr_computer){
                    drawer.close();
                    actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(),HOME_FRAGMENT);
                }
                else if(id == R.id.nav_my_orders){
                    drawer.close();
                    gotoFragment("My Orders",new MyOrdersFragment(),MY_ORDER_FRAGMENT);
                }
                else if(id == R.id.nav_my_address){
                    drawer.close();
                    gotoFragment("My Addresses",new MyAddressesFragment(),6);
                }
                else if(id == R.id.nav_my_cart){
                    drawer.close();
                    gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
                }
                else if(id == R.id.nav_my_wishlist) {
                    drawer.closeDrawer(Gravity.LEFT);
                    gotoFragment("My Wishlist",new MyWishlistFragment(),WISHLIST_FRAGMENT);

                }
                else if(id == R.id.nav_my_account){
                    drawer.closeDrawer(Gravity.LEFT);
                    gotoFragment("My Account",new MyAccountFragment(),MY_ACCOUNT_FRAGMENT);
                }
                else if(id == R.id.nav_sign_out){
                    drawer.close();
                    firebaseAuth.signOut();
                    Intent register = new Intent(HomeActivity.this,Login_Activity.class);
                    register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    register.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(register);
                }
                return false;
            }
        });

        navigationView.getMenu().getItem(0).setChecked(true);

        frameLayout = findViewById(R.id.main_frame_layout);

        setFragment(new HomeFragment(),HOME_FRAGMENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.home, menu);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.main_cart_icon){
            gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFragment(String title,Fragment fragment,int fragmentNo) {

        actionBarLogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment,fragmentNo);
        if (fragmentNo==CART_FRAGMENT) {
            navigationView.getMenu().getItem(3).setChecked(false);
        }
        else if (fragmentNo==WISHLIST_FRAGMENT){
            navigationView.getMenu().getItem(4).setChecked(false);
        }
        else if (fragmentNo==MY_ACCOUNT_FRAGMENT){
            navigationView.getMenu().getItem(5).setChecked(false);
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setFragment(Fragment fragment,int fragmentNo){
        if(fragmentNo != currentFragment) {
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}