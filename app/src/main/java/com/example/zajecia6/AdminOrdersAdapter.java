package com.example.zajecia6;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.zajecia6.callback.MenuItemRetrievedCallback;
import com.example.zajecia6.callback.OrderStatusChangedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.MenuItem;
import com.example.zajecia6.model.OrderModel;
import com.example.zajecia6.model.OrderStatus;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class AdminOrdersAdapter extends ArrayAdapter<OrderModel> {

    private Context context;
    private List<OrderModel> orderList;
    private TextView itemsNames;
    private TextView orderStatus;
    private Button acceptOrder;
    private Button declineOrder;
    private Button deleteOrder;
    private Button orderReady;
    private FirestoreDAO dao;

    public AdminOrdersAdapter(@NonNull Context context, List<OrderModel> orderList) {
        super(context, R.layout.admin_orders_list_item, orderList);
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.admin_orders_list_item, null);
        }

        dao = new FirestoreDAO(getContext());

        itemsNames = view.findViewById(R.id.textViewItemsNames);
        orderStatus = view.findViewById(R.id.textViewOrderStatus);
        acceptOrder = view.findViewById(R.id.buttonAcceptOrder);
        declineOrder = view.findViewById(R.id.buttonDeclineOrder);
        deleteOrder = view.findViewById(R.id.buttonDeleteOrder);
        orderReady = view.findViewById(R.id.buttonOrderReady);

        itemsNames.setText(orderList.get(position).getAddress() + "\n" + orderList.get(position).getTotalPrice() + "zł");

        switch (orderList.get(position).getOrderStatus()){
            case PENDING:
                orderStatus.setText("Oczekujące");
                acceptOrder.setVisibility(View.VISIBLE);
                declineOrder.setVisibility(View.VISIBLE);
                break;
            case PREPARING:
                orderStatus.setText("W trakcie przygotowania");
                orderReady.setVisibility(View.VISIBLE);
                break;
            case DECLINED:
                orderStatus.setText("Odrzucone");
                deleteOrder.setVisibility(View.VISIBLE);
                break;
            case FINISHED:
                orderStatus.setText("Gotowe");
                deleteOrder.setVisibility(View.VISIBLE);
                break;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(position);
            }
        });

        acceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus(position, OrderStatus.PREPARING);
            }
        });

        declineOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus(position, OrderStatus.DECLINED);
            }
        });

        orderReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus(position, OrderStatus.FINISHED);
            }
        });

        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrder(position);
            }
        });
        return view;

    }

    void showInfoDialog(int position){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.order_info_dialog);

        TextView orderId = dialog.findViewById(R.id.textViewOrderInfoId);
        TextView address = dialog.findViewById(R.id.textViewOrderInfoAddress);
        TextView phone = dialog.findViewById(R.id.textViewOrderInfoPhone);
        TextView total = dialog.findViewById(R.id.textViewOrderInfoTotal);
        TextView orderNames = dialog.findViewById(R.id.textViewOrderInfoNames);
        TextView additionalInfo = dialog.findViewById(R.id.textViewOrderInfoAdditional);
        Button close = dialog.findViewById(R.id.buttonCloseInfo);

        orderId.setText(orderList.get(position).getId());
        address.setText(orderList.get(position).getAddress());
        phone.setText(orderList.get(position).getPhoneNumber());
        total.setText(Double.toString(orderList.get(position).getTotalPrice()) + "zł");
        additionalInfo.setText(orderList.get(position).getAdditionalInfo());

        for(String itemId : orderList.get(position).getItems()){
            dao.getMenuItem(itemId, new MenuItemRetrievedCallback() {
                @Override
                public void onItemRetrieved(MenuItem menuItem) {
                    String itemName = orderNames.getText().toString() + menuItem.getName() + " - " + menuItem.getPrice() + "zł\n";
                    orderNames.setText(itemName);
                }
            });
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void changeOrderStatus(int position, OrderStatus status){
        OrderModel order = orderList.get(position);
        order.setOrderStatus(status);
        dao.editOrder(order, new OrderStatusChangedCallback() {
            @Override
            public void onStatusChanged() {
                Toast.makeText(context, "Zmieniono status zamówienia", Toast.LENGTH_SHORT).show();
                ((FragmentActivity)context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.Fragment_container,new AdminOrdersFragment())
                        .commit();
            }
        });
    }

    void deleteOrder(int position){
        dao.deleteOrder(orderList.get(position).getId(), new OrderStatusChangedCallback() {
            @Override
            public void onStatusChanged() {
                ((FragmentActivity)context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.Fragment_container,new AdminOrdersFragment())
                        .commit();
            }
        });
    }
}
