package com.example.drcomputer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {

   private View view;

   private RecyclerView categoryRecyclerView;

   private CategoryAdapter categoryAdapter;
   private List<CategoryModel> categoryModelsList;
   private FirebaseFirestore firebaseFirestore;

   /////////Banner Slider
    private ViewPager bannerSliderViewPager;
    private List<SliderModel> sliderModelList;
    private int currentPage = 2;
    private Timer timer;
    final private long DELAY_TIME = 3000;
    final private long PERIOD_TIME = 3000;
    ////////Banner Slider

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home2, container, false);

        categoryRecyclerView = view.findViewById(R.id.category_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);

        categoryModelsList = new ArrayList<CategoryModel>();

        categoryModelsList.add(new CategoryModel(R.drawable.ic_dr_computer,"Home"));
        categoryModelsList.add(new CategoryModel(R.drawable.business_laptop,"Business Laptops"));
        categoryModelsList.add(new CategoryModel(R.drawable.gaming_laptop,"Gaming Laptops"));
        categoryModelsList.add(new CategoryModel(R.drawable.personal_laptop,"Personal Laptops"));
        categoryModelsList.add(new CategoryModel(R.drawable.professional_laptop,"Professional Laptops"));
        categoryModelsList.add(new CategoryModel(R.drawable.all_in_one_desktop,"All in One"));
        categoryModelsList.add(new CategoryModel(R.drawable.personal_desktop,"Personal Desktops"));
        categoryModelsList.add(new CategoryModel(R.drawable.ic_black_search,"Search Product"));

        categoryAdapter = new CategoryAdapter(categoryModelsList);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

        ////////banner slider
        bannerSliderViewPager = view.findViewById(R.id.banner_slider_view_pager);

        sliderModelList = new ArrayList<SliderModel>();
        sliderModelList.add(new SliderModel(R.drawable.banner_three));
        sliderModelList.add(new SliderModel(R.drawable.banner_four));

        sliderModelList.add(new SliderModel(R.drawable.banner_one));
        sliderModelList.add(new SliderModel(R.drawable.banner_two));
        sliderModelList.add(new SliderModel(R.drawable.banner_three));
        sliderModelList.add(new SliderModel(R.drawable.banner_four));

        sliderModelList.add(new SliderModel(R.drawable.banner_one));
        sliderModelList.add(new SliderModel(R.drawable.banner_two));

        SliderAdapter sliderAdapter = new SliderAdapter(sliderModelList);
        bannerSliderViewPager.setAdapter(sliderAdapter);
        bannerSliderViewPager.setClipToPadding(false);
        bannerSliderViewPager.setPageMargin(20);

        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE){
                    pageLooper();
                }
            }
        };
        bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);
        startBannerSlideShow();
        bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                pageLooper();
                stopBannerSlideShow();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    startBannerSlideShow();
                }
                return false;
            }
        });

        ///Grid Product Layout
        TextView gridLayoutTitle = view.findViewById(R.id.grid_product_layout_title);
        //Button gridLayoutViewAllBtn = view.findViewById(R.id.grid_product_layout_view_all_btn);
        GridView gridView = view.findViewById(R.id.grid_product_layout_grid_view);


        horizontalProductScrollModelList = new ArrayList<HorizontalProductScrollModel>();
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.personal_laptop,"Personal Laptops","Intel Core i3 Processors","From Rs.29999/-"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.gaming_laptop,"Gaming Laptops","Intel Core i5,i7 Processors","From Rs.59999/-"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.professional_laptop,"Professional Laptops","Intel Core i5 Processors","From Rs.49999/-"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.business_laptop,"Business Laptops","Intel Core i3 Processors","From Rs.24999/-"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.all_in_one_desktop,"All in One","Intel Core i5 Processors","From Rs.34999/-"));
        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.personal_desktop,"Personal Desktops","Intel Core i3,i5,i7 Processors","From Rs.24999/-"));
        gridView.setAdapter(new GridProductLayoutAdapter(horizontalProductScrollModelList));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent personal = new Intent(getContext(),ProductViewListActivity.class);
                    personal.putExtra("CategoryName",horizontalProductScrollModelList.get(i).getProductTitle());
                    startActivity(personal);
            }
        });

        ///Grid Product Layout
        return view;
    }

    //Banner Slider
    private void pageLooper(){
        if(currentPage==sliderModelList.size()-2){
            currentPage = 2;
            bannerSliderViewPager.setCurrentItem(currentPage,false);
        }
        if(currentPage==1){
            currentPage = sliderModelList.size()-3;
            bannerSliderViewPager.setCurrentItem(currentPage,false);
        }
    }

    private void startBannerSlideShow(){
        Handler handler = new Handler();
        Runnable update = new Runnable() {
            @Override
            public void run() {
                if(currentPage>=sliderModelList.size()){
                    currentPage = 1;
                }
                bannerSliderViewPager.setCurrentItem(currentPage++,true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        },DELAY_TIME,PERIOD_TIME);
    }

    private void stopBannerSlideShow(){
        timer.cancel();
    }
    //Banner Slider
}