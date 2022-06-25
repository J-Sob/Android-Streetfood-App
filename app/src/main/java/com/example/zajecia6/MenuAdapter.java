package com.example.zajecia6;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.zajecia6.callback.MenuItemRetrievedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.MenuItem;

import java.util.List;

public class MenuAdapter extends ArrayAdapter<MenuItem> {
    private Context context;
    private List<MenuItem> menuList;
    private FirestoreDAO dao;

    MenuAdapter(Context context, List<MenuItem> menuList){
        super(context, R.layout.menu_list_item, menuList);
        this.context = context;
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.menu_list_item, null);
        }
        dao = new FirestoreDAO(context);

        TextView name = view.findViewById(R.id.textViewItemName);
        Button edit = view.findViewById(R.id.buttonItemEdit);
        Button delete = view.findViewById(R.id.buttonItemDelete);

        name.setText(menuList.get(position).getName());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditItemDialog(menuList.get(position).getId());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    void showEditItemDialog(String id){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.item_edit_dialog);

        EditText name = dialog.findViewById(R.id.editTextItemName);
        EditText price = dialog.findViewById(R.id.editTextItemPrice);
        Switch availability = dialog.findViewById(R.id.switchAvailability);
        Button accept = dialog.findViewById(R.id.buttonAcceptItemEdit);

        MenuItem item = new MenuItem();

        dao.getMenuItem(id, new MenuItemRetrievedCallback() {
            @Override
            public void onItemRetrieved(MenuItem menuItem) {
                item.setId(menuItem.getId());
                item.setName(menuItem.getName());
                item.setPrice(menuItem.getPrice());
                item.setAvailable(menuItem.isAvailable());
                item.setCategory(menuItem.getCategory());
                item.setImageRef(menuItem.getImageRef());

                name.setText(menuItem.getName());
                price.setText(Double.toString(menuItem.getPrice()));
                if(menuItem.isAvailable()){
                    availability.setChecked(true);
                }else{
                    availability.setChecked(false);
                }
                accept.setEnabled(true);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setName(name.getText().toString());
                item.setPrice(Double.parseDouble(price.getText().toString()));
                if(availability.isChecked()){
                    item.setAvailable(true);
                }else{
                    item.setAvailable(false);
                }
                dao.editMenuItem(item);
                dialog.dismiss();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.Fragment_container,new AdminFragment()).commit();
            }
        });


        dialog.show();
    }
}
