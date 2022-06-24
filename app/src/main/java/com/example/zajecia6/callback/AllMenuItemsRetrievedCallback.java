package com.example.zajecia6.callback;

import com.example.zajecia6.model.MenuItem;

import java.util.List;

public interface AllMenuItemsRetrievedCallback {
    void onMenuItemsRetrieved(List<MenuItem> menuItems);
}
