package com.example.zajecia6;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginRegisterFragment extends Fragment {
    EditText editTextLoginEmail;
    EditText editTextLoginPassword;
    EditText editTextRegisterEmail;
    EditText editTextRegisterPassword;
    EditText editTextConfirmPassword;
    EditText editTextFullname;
    EditText editTextAddress;
    EditText editTextPhone;
    Button buttonLogin;
    Button buttonRegister;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_register, container, false);

        editTextLoginEmail = view.findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = view.findViewById(R.id.editTextLoginPassword);
        editTextRegisterEmail = view.findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword = view.findViewById(R.id.editTextRegisterPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        editTextFullname = view.findViewById(R.id.editTextFullName);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editTextLoginEmail.getText().toString().equals("")
                    && !editTextLoginPassword.getText().toString().equals("")){
                    mAuth.signInWithEmailAndPassword(editTextLoginEmail.getText().toString(), editTextLoginPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(LoginRegisterFragment.this.getContext(), "Logowanie powiodło się", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginRegisterFragment.this.getContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginRegisterFragment.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }else{
                    Toast.makeText(LoginRegisterFragment.this.getContext(), "Uzupełnij wszystkie informacje", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editTextRegisterEmail.getText().toString().equals("") &&
                        !editTextFullname.getText().toString().equals("") &&
                        !editTextAddress.getText().toString().equals("") &&
                        !editTextRegisterPassword.getText().toString().equals("") &&
                        !editTextConfirmPassword.getText().toString().equals("") &&
                        !editTextPhone.getText().toString().equals("")){
                    if(editTextPhone.getText().toString().length() == 9){
                        if(editTextConfirmPassword.getText().toString().equals(editTextRegisterPassword.getText().toString())){
                            mAuth.createUserWithEmailAndPassword(editTextRegisterEmail.getText().toString(), editTextRegisterPassword.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            User model = new User(editTextRegisterEmail.getText().toString(),
                                                    editTextFullname.getText().toString(),
                                                    editTextAddress.getText().toString(),
                                                    editTextPhone.getText().toString(),
                                                    false);

                                            FirestoreDAO dao = new FirestoreDAO();
                                            try {
                                                dao.addUser(user.getUid(), model);
                                                Toast.makeText(LoginRegisterFragment.this.getContext(), "Konto utworzone.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginRegisterFragment.this.getContext(), MainActivity.class);
                                                startActivity(intent);
                                            }catch (Exception e){
                                                Toast.makeText(LoginRegisterFragment.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginRegisterFragment.this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }else{
                            Toast.makeText(LoginRegisterFragment.this.getContext(), "Hasła się nie zgadzają", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginRegisterFragment.this.getContext(), "Błędny numer telefonu.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginRegisterFragment.this.getContext(), "Uzupełnij wszystkie informacje", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }
}