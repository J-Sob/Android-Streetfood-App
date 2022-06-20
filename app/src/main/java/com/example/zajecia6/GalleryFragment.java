package com.example.zajecia6;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GalleryFragment extends Fragment {
    ImageView imageView;
    TextView igLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_fragment, container, false);
        igLink = view.findViewById(R.id.tV_ig);

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        imageView=(ImageView) view.findViewById(R.id.imageView);
        for(int i=0;i<10;i++){
            ImageView localView = new ImageView(getActivity());
            localView.setLayoutParams(new ViewGroup.LayoutParams(200,200));
            localView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            int resID = getResources().getIdentifier("a"+i, "drawable",getActivity().getPackageName());
            localView.setImageResource(resID);
            localView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView localView = (ImageView) v;
                    imageView.setImageDrawable(localView.getDrawable());
                }
            });
            linearLayout.addView(localView);
        }

        return view;
    }
}