package com.example.zajecia6.callback;

import com.example.zajecia6.model.MenuItem;

public interface OrderChangedCallback {
    void onItemAdded(MenuItem item);
    void onItemRemoved(MenuItem item);
}
