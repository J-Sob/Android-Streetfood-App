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

import com.example.zajecia6.callback.OrderChangedCallback;
import com.example.zajecia6.model.MenuItem;

import java.util.List;

public class OrderListAdapter extends ArrayAdapter<MenuItem> {
    private Context context;
    private List<MenuItem> items;
    private OrderChangedCallback callback;

    public OrderListAdapter(@NonNull Context context, List<MenuItem> items, OrderChangedCallback callback) {
        super(context, R.layout.order_list_item, items);
        this.context = context;
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.order_list_item, null);
        }

        TextView itemName = view.findViewById(R.id.textViewOrderName);
        TextView quantity = view.findViewById(R.id.textViewQuantity);
        Button plus = view.findViewById(R.id.buttonPlus);
        Button minus = view.findViewById(R.id.buttonMinus);


        if(!items.get(position).isAvailable()){
            plus.setVisibility(View.GONE);
            minus.setVisibility(View.GONE);
            quantity.setText("Niedostępne");
        }

        itemName.setText(items.get(position).getName() + " " + Double.toString(items.get(position).getPrice()) + " zł");

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                quantity.setText(Integer.toString(currentQuantity + 1));
                callback.onItemAdded(items.get(position));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQuantity = Integer.parseInt(quantity.getText().toString());
                if(currentQuantity > 0){
                    quantity.setText(Integer.toString(currentQuantity - 1));
                    callback.onItemRemoved(items.get(position));
                }
            }
        });

        return view;
    }
}
