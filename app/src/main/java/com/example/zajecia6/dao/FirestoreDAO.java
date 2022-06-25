package com.example.zajecia6.dao;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.zajecia6.callback.AllMenuItemsRetrievedCallback;
import com.example.zajecia6.callback.MenuItemDeletedCallback;
import com.example.zajecia6.callback.MenuItemRetrievedCallback;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FirestoreDAO {
    private FirebaseFirestore firebaseFirestore;
    private Context context;

    public FirestoreDAO(Context context){
        this.context = context;
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
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getMenuItem(String id, MenuItemRetrievedCallback menuItemRetrievedCallback){
        firebaseFirestore.collection("menuItems")
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        menuItemRetrievedCallback.onItemRetrieved(documentSnapshot.toObject(MenuItem.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void editMenuItem(MenuItem item){
        firebaseFirestore.collection("menuItems")
                .document(item.getId())
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Pozycja zaktualizowana", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void addMenuItem(MenuItem item){
        DocumentReference ref = firebaseFirestore.collection("menuItems").document();
        String newId = ref.getId();
        item.setId(newId);
        ref.set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Pozycja dodana", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void deleteMenuItem(String itemId, String imageRef, MenuItemDeletedCallback menuItemDeletedCallback){
        firebaseFirestore.collection("menuItems")
                .document(itemId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageRef)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        menuItemDeletedCallback.onItemDeleted();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
