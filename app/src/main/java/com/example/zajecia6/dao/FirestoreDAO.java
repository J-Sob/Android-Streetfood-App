package com.example.zajecia6.dao;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.zajecia6.callback.AllMenuItemsRetrievedCallback;
import com.example.zajecia6.callback.UserRetrievedCallback;
import com.example.zajecia6.model.MenuItem;
import com.example.zajecia6.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreDAO {
    private FirebaseFirestore firebaseFirestore;

    public FirestoreDAO(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void addUser(String id, User user){
        firebaseFirestore.collection("users")
                .document(id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {}
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            throw e;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
    }

    public void getUserById(String id, UserRetrievedCallback callback){
        DocumentReference dbRef = firebaseFirestore.collection("users").document(id);
        dbRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                callback.onUserRetrieved(documentSnapshot.toObject(User.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    throw e;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    public void getAllMenuItems(AllMenuItemsRetrievedCallback callback){
        firebaseFirestore.collection("menuItems")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<MenuItem> menuItems = new ArrayList<>();
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            menuItems.add(documentSnapshot.toObject(MenuItem.class));
                        }
                        callback.onMenuItemsRetrieved(menuItems);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            throw e;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
    }

    public void addMenuItem(MenuItem item){
        firebaseFirestore.collection("menuItems")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {}
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            throw e;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
    }
}
