package com.example.zajecia6;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.zajecia6.callback.AllMenuItemsRetrievedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.MenuItem;
import com.example.zajecia6.model.MenuItemCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    final long ONE_MEGABYTE = 1024 * 1024;
    ImageSlider imageSliderBurgers;
    ImageSlider imageSliderPancakes;
    private FirestoreDAO dao;
    private StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        imageSliderBurgers = view.findViewById(R.id.imageSlider_burger);
        imageSliderPancakes = view.findViewById(R.id.imageSlider_pancakes);
        storageRef = FirebaseStorage.getInstance().getReference();
        dao = new FirestoreDAO(getContext());
        dao.getAllMenuItems(new AllMenuItemsRetrievedCallback() {
            @Override
            public void onMenuItemsRetrieved(List<MenuItem> menuItems) {
                ArrayList<SlideModel> burgerImages = new ArrayList<>();
                ArrayList<SlideModel> pancakesImages = new ArrayList<>();
                for(MenuItem item : menuItems){
                    if(item.getCategory() == MenuItemCategory.BURGER){
                        burgerImages.add(new SlideModel(item.getImageRef(),
                                item.getName() + "\n" + item.getPrice() + "zł",
                                null));
                    }else if(item.getCategory() == MenuItemCategory.PANCAKES){
                        pancakesImages.add(new SlideModel(item.getImageRef(),
                                item.getName() + "\n" + item.getPrice() + "zł",
                                null));
                    }
                }
                imageSliderBurgers.setImageList(burgerImages, ScaleTypes.CENTER_CROP);
                imageSliderPancakes.setImageList(pancakesImages,ScaleTypes.CENTER_CROP);
            }
        });
        return view;
    }
}