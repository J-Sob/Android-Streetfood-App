package com.example.zajecia6.callback;

import com.example.zajecia6.model.OrderModel;

import java.util.List;

public interface OrdersRetrievedCallback {
    void onOrdersRetrieved(List<OrderModel> orderList);
}
