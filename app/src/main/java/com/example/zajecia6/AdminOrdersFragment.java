package com.example.zajecia6;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zajecia6.callback.MenuItemRetrievedCallback;
import com.example.zajecia6.callback.OrderRetrievedCallback;
import com.example.zajecia6.callback.OrdersRetrievedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.MenuItem;
import com.example.zajecia6.model.MenuItemCategory;
import com.example.zajecia6.model.OrderModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminOrdersFragment extends Fragment {

    private ListView adminOrders;
    private FirestoreDAO dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_orders, container, false);

        adminOrders = view.findViewById(R.id.listViewAdminOrders);

        dao = new FirestoreDAO(getContext());
        dao.getAllOrders(new OrdersRetrievedCallback() {
            @Override
            public void onOrdersRetrieved(List<OrderModel> orderList) {
                orderList.sort(new Comparator<OrderModel>() {
                    @Override
                    public int compare(OrderModel o1, OrderModel o2) {
                        return o1.getOrderStatus().compareTo(o2.getOrderStatus());
                    }
                });
                AdminOrdersAdapter adapter = new AdminOrdersAdapter(getContext(), orderList);
                adminOrders.setAdapter(adapter);
            }
        });

        return view;
    }

}