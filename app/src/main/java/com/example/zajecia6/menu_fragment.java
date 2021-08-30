package com.example.zajecia6;

import android.os.Bundle;

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

import java.util.ArrayList;

public class menu_fragment extends Fragment {
    ImageSlider imageSliderBurgers;
    ImageSlider imageSliderPancakes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        imageSliderBurgers = view.findViewById(R.id.imageSlider_burger);
        imageSliderPancakes = view.findViewById(R.id.imageSlider_pancakes);

        ArrayList<SlideModel> burgerImages = new ArrayList<>();
        burgerImages.add(new SlideModel(R.drawable.b0,"Black Burger \n25zł", null ));
        burgerImages.add(new SlideModel(R.drawable.b1,"Standard Burger \n18zł",null));
        burgerImages.add(new SlideModel(R.drawable.b2,"BBQ Cheeseburger \n20zł",null));
        burgerImages.add(new SlideModel(R.drawable.b3,"Texmex Cheeseburger \n20zł", null));
        burgerImages.add(new SlideModel(R.drawable.b4,"Cheeseburger \n19zł",null));
        burgerImages.add(new SlideModel(R.drawable.b5,"Burger premium z karmelizowaną gruszką \n20zł",null));
        imageSliderBurgers.setImageList(burgerImages, ScaleTypes.CENTER_CROP);

        ArrayList<SlideModel> pancakesImages = new ArrayList<>();
        pancakesImages.add(new SlideModel(R.drawable.p0,"American Classic \n18.50zł", null ));
        pancakesImages.add(new SlideModel(R.drawable.p1,"Nutella Classic \n17zł",null));
        pancakesImages.add(new SlideModel(R.drawable.p2,"Pan B. \n22zł",null));
        pancakesImages.add(new SlideModel(R.drawable.p3,"Pink Panther \n23zł", null));
        imageSliderPancakes.setImageList(pancakesImages,ScaleTypes.CENTER_CROP);
        return view;
    }
}