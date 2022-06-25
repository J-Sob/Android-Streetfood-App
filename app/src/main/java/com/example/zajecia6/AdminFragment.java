package com.example.zajecia6;

import android.app.Dialog;
import android.content.Intent;
import android.media.Session2Command;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.zajecia6.callback.AllMenuItemsRetrievedCallback;
import com.example.zajecia6.callback.ImageUploadedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.MenuItem;
import com.example.zajecia6.model.MenuItemCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminFragment extends Fragment {

    private ListView menuList;
    private Button addItem;
    private ProgressBar spinner;
    private FirestoreDAO dao;
    private Uri imageUri;
    private ImageView preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        menuList = view.findViewById(R.id.listViewMenu);
        spinner = view.findViewById(R.id.progressBarAdmin);
        addItem = view.findViewById(R.id.buttonAddItem);
        dao = new FirestoreDAO(getContext());
        dao.getAllMenuItems(new AllMenuItemsRetrievedCallback() {
            @Override
            public void onMenuItemsRetrieved(List<MenuItem> menuItems) {
                MenuAdapter menuAdapter = new MenuAdapter(AdminFragment.this.getContext(), menuItems);
                menuList.setAdapter(menuAdapter);
                spinner.setVisibility(View.GONE);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });
        return view;
    }

    void showAddItemDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_item_dialog);

        AutoCompleteTextView category = dialog.findViewById(R.id.autoCompleteTextViewCategory);
        EditText name = dialog.findViewById(R.id.editTextNewItemName);
        EditText price = dialog.findViewById(R.id.editTextNewItemPrice);
        Switch availability = dialog.findViewById(R.id.switchNewItemAvailability);
        Button addImage = dialog.findViewById(R.id.buttonAddItemImage);
        preview = dialog.findViewById(R.id.imageViewItemPreview);
        Button accept = dialog.findViewById(R.id.buttonAcceptNewItem);

        List<String> categoryStrings = new ArrayList<>();
        for(MenuItemCategory cat : MenuItemCategory.values()){
            categoryStrings.add(cat.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), R.layout.category_dropdown_item, categoryStrings);
        category.setAdapter(adapter);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    if(!name.getText().toString().equals("")
                            && !price.getText().toString().equals("")
                            && !category.getText().toString().equals("Wybierz kategorię")){
                        MenuItem newItem = new MenuItem();
                        newItem.setName(name.getText().toString());
                        newItem.setPrice(Double.parseDouble(price.getText().toString()));
                        newItem.setCategory(MenuItemCategory.valueOf(category.getText().toString()));
                        if(availability.isChecked()){
                            newItem.setAvailable(true);
                        }else{
                            newItem.setAvailable(false);
                        }
                        uploadImage(new ImageUploadedCallback() {
                            @Override
                            public void onImageUploaded(Uri imageUri) {
                                newItem.setImageRef(imageUri.toString());
                                dao.addMenuItem(newItem);
                                dialog.dismiss();
                                ((FragmentActivity)AdminFragment.this.getContext())
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.Fragment_container,new AdminFragment())
                                        .commit();
                            }
                        });
                    }else{
                        Toast.makeText(AdminFragment.this.getContext(), "Uzupełnij wszystkie informacje", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AdminFragment.this.getContext(), "Nie wybrano pliku", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    void uploadImage(ImageUploadedCallback imageUploadedCallback){
        String fileName = UUID.randomUUID().toString();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("/menuItemImage/" + fileName);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUploadedCallback.onImageUploaded(uri);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AdminFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == -1){
                if(data != null && data.getData() != null){
                    imageUri = data.getData();
                    preview.setImageURI(imageUri);
                }
            }else{
                Toast.makeText(this.getContext(), "Wybranie zdjęcia nie powiodło się", Toast.LENGTH_SHORT).show();
            }
        }
    }
}