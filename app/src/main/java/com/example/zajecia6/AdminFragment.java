package com.example.zajecia6;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.denzcoskun.imageslider.models.SlideModel;
import com.example.zajecia6.callback.AllMenuItemsRetrievedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.MenuItem;
import com.example.zajecia6.model.MenuItemCategory;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

    private ListView menuList;
    private Button addItem;
    private ProgressBar spinner;
    private FirestoreDAO dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        menuList = view.findViewById(R.id.listViewMenu);
        spinner = view.findViewById(R.id.progressBarAdmin);
        addItem = view.findViewById(R.id.buttonAddItem);
        dao = new FirestoreDAO();
        dao.getAllMenuItems(new AllMenuItemsRetrievedCallback() {
            @Override
            public void onMenuItemsRetrieved(List<MenuItem> menuItems) {
                MenuAdapter menuAdapter = new MenuAdapter(AdminFragment.this.getContext(), menuItems);
                menuList.setAdapter(menuAdapter);
                spinner.setVisibility(View.GONE);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}