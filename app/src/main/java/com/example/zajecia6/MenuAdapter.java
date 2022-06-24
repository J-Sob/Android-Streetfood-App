package com.example.zajecia6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zajecia6.model.MenuItem;

import java.util.List;

public class MenuAdapter extends ArrayAdapter<MenuItem> {
    private Context context;
    private List<MenuItem> menuList;

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

        TextView name = view.findViewById(R.id.textViewItemName);
        Button edit = view.findViewById(R.id.buttonItemEdit);
        Button delete = view.findViewById(R.id.buttonItemDelete);

        name.setText(menuList.get(position).getName());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
