package com.example.zajecia6.dao;

import androidx.annotation.NonNull;

import com.example.zajecia6.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void getUserById(String id, FirestoreCallback callback){
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
}
