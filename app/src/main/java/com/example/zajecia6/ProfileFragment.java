package com.example.zajecia6;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zajecia6.dao.FirestoreCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextAddress;
    private EditText editTextPhone;
    private Button buttonEdit;
    private Button buttonChangePassword;
    private Button buttonLogout;
    private FirebaseAuth mAuth;
    private FirestoreDAO dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        editTextName = view.findViewById(R.id.editTextProfileName);
        editTextEmail = view.findViewById(R.id.editTextProfileEmail);
        editTextAddress = view.findViewById(R.id.editTextProfileAddress);
        editTextPhone = view.findViewById(R.id.editTextProfilePhone);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        buttonLogout = view.findViewById(R.id.buttonLogout);
        mAuth = FirebaseAuth.getInstance();
        dao = new FirestoreDAO();
        dao.getUserById(mAuth.getCurrentUser().getUid(), new FirestoreCallback() {
            @Override
            public void onUserRetrieved(User user) {
                editTextName.setText(user.getFullName());
                editTextEmail.setText(user.getEmail());
                editTextAddress.setText(user.getAddress());
                editTextPhone.setText(user.getPhoneNumber());
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileFragment.this.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("Edytuj".equals(buttonEdit.getText().toString())){
                    buttonEdit.setText("Zapisz");
                    editTextAddress.setEnabled(true);
                    editTextName.setEnabled(true);
                    editTextPhone.setEnabled(true);
                }else{
                    if("".equals(editTextName.getText().toString()) ||
                            "".equals(editTextAddress.getText().toString()) ||
                            "".equals(editTextPhone.getText().toString())){
                        Toast.makeText(ProfileFragment.this.getContext(), "Żadne pole nie może być puste", Toast.LENGTH_SHORT).show();
                    }else{
                        if(editTextPhone.getText().toString().length() != 9){
                            Toast.makeText(ProfileFragment.this.getContext(), "Błędny numer telefonu", Toast.LENGTH_SHORT).show();
                        }else{
                            buttonEdit.setText("Edytuj");
                            User u = new User(editTextEmail.getText().toString(),
                                    editTextName.getText().toString(),
                                    editTextAddress.getText().toString(),
                                    editTextPhone.getText().toString());
                            dao.addUser(mAuth.getCurrentUser().getUid(), u);
                            Toast.makeText(ProfileFragment.this.getContext(), "Informacje zaktualizowane", Toast.LENGTH_SHORT).show();
                            editTextAddress.setEnabled(false);
                            editTextName.setEnabled(false);
                            editTextPhone.setEnabled(false);
                        }
                    }
                }
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });
        return view;
    }

    void showChangePasswordDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.password_change_dialog);

        EditText currentPassword = dialog.findViewById(R.id.editTextCurrentPassword);
        EditText newPassword = dialog.findViewById(R.id.editTextNewPassword);
        Button acceptChange = dialog.findViewById(R.id.buttonAcceptChange);

        acceptChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential credential = EmailAuthProvider.getCredential(editTextEmail.getText().toString(), currentPassword.getText().toString());
                mAuth.getCurrentUser().reauthenticate(credential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                mAuth.getCurrentUser().updatePassword(newPassword.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ProfileFragment.this.getContext(), "Hasło zmienione pomyślnie.", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        dialog.show();
    }
}