package com.example.zajecia6.dao;

import com.example.zajecia6.model.User;

public interface FirestoreCallback {
    void onUserRetrieved(User user);
}
