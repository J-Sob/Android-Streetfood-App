package com.example.zajecia6;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zajecia6.callback.OrdersRetrievedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.OrderModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Comparator;
import java.util.List;

public class UsersOrdersFragment extends Fragment {

    private ListView usersOrders;
    private FirestoreDAO dao;
    private FirebaseAuth mAuth;
    private TextView noOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_orders, container, false);

        usersOrders = view.findViewById(R.id.listViewUsersOrders);
        noOrders = view.findViewById(R.id.textViewNoOrders);
        mAuth = FirebaseAuth.getInstance();
        dao = new FirestoreDAO(getContext());

        if(mAuth.getCurrentUser() != null){
            dao.getUsersOrders(mAuth.getCurrentUser().getUid(), new OrdersRetrievedCallback() {
                @Override
                public void onOrdersRetrieved(List<OrderModel> orderList) {
                    if(orderList.isEmpty()){
                        noOrders.setVisibility(View.VISIBLE);
                    }else{
                        orderList.sort(new Comparator<OrderModel>() {
                            @Override
                            public int compare(OrderModel o1, OrderModel o2) {
                                return o1.getOrderStatus().compareTo(o2.getOrderStatus());
                            }
                        });
                        UsersOrdersAdapter adapter = new UsersOrdersAdapter(getContext(), orderList);
                        usersOrders.setAdapter(adapter);
                    }
                }
            });
        }


        return view;
    }
}