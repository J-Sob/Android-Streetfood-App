package com.example.zajecia6;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.zajecia6.callback.AllMenuItemsRetrievedCallback;
import com.example.zajecia6.callback.OrderChangedCallback;
import com.example.zajecia6.callback.OrderPlacedCallback;
import com.example.zajecia6.callback.UserRetrievedCallback;
import com.example.zajecia6.dao.FirestoreDAO;
import com.example.zajecia6.model.MenuItem;
import com.example.zajecia6.model.Order;
import com.example.zajecia6.model.OrderStatus;
import com.example.zajecia6.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class NewOrderFragment extends Fragment {

    private ListView menuList;
    private TextView address;
    private TextView phoneNumber;
    private TextView additionalInfo;
    private TextView sum;
    private TextView deliveryPrice;
    private TextView totalPrice;
    private Button placeOrder;
    private FirestoreDAO dao;
    private FirebaseAuth mAuth;
    private List<MenuItem> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_order, container, false);

        menuList = view.findViewById(R.id.listViewOrderMenu);
        address = view.findViewById(R.id.editTextOrderAddress);
        phoneNumber = view.findViewById(R.id.editTextOrderPhone);
        additionalInfo = view.findViewById(R.id.editTextAdditionalInfo);
        sum = view.findViewById(R.id.textViewSum);
        deliveryPrice = view.findViewById(R.id.textViewDelivery);
        totalPrice = view.findViewById(R.id.textViewTotalPrice);
        placeOrder = view.findViewById(R.id.buttonPlaceOrder);


        items = new ArrayList<>();

        menuList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        dao = new FirestoreDAO(getContext());
        mAuth = FirebaseAuth.getInstance();
        dao.getAllMenuItems(new AllMenuItemsRetrievedCallback() {
            @Override
            public void onMenuItemsRetrieved(List<MenuItem> menuItems) {
                OrderListAdapter orderListAdapter = new OrderListAdapter(NewOrderFragment.this.getContext(),
                        menuItems,
                        new OrderChangedCallback() {
                    @Override
                    public void onItemAdded(MenuItem item) {
                        items.add(item);
                        double newSum = Double.parseDouble(sum.getText().toString()) + item.getPrice();
                        sum.setText(Double.toString(newSum));
                        double newTotal = newSum + Double.parseDouble(deliveryPrice.getText().toString());
                        totalPrice.setText(Double.toString(newTotal));
                    }

                    @Override
                    public void onItemRemoved(MenuItem item) {
                        items.stream()
                                .filter(i -> i.getId().equals(item.getId()))
                                .findFirst().ifPresent(foundItem -> items.remove(foundItem));
                        double newSum = Double.parseDouble(sum.getText().toString()) - item.getPrice();
                        sum.setText(Double.toString(newSum));
                        double newTotal = newSum + Double.parseDouble(deliveryPrice.getText().toString());
                        totalPrice.setText(Double.toString(newTotal));
                    }
                });
                menuList.setAdapter(orderListAdapter);
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            dao.getUserById(currentUser.getUid(), new UserRetrievedCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    address.setText(user.getAddress());
                    phoneNumber.setText(user.getPhoneNumber());
                }
            });
        }

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(address.getText().toString().equals("") ||
                    phoneNumber.getText().toString().equals("")){
                    Toast.makeText(NewOrderFragment.this.getContext(), "Pola adres i numer telefonu są wymagane", Toast.LENGTH_SHORT).show();
                }else if(items.size() == 0){
                    Toast.makeText(NewOrderFragment.this.getContext(), "Dodaj pozycję do zamówienia", Toast.LENGTH_SHORT).show();
                }else if(phoneNumber.getText().toString().length() != 9){
                    Toast.makeText(NewOrderFragment.this.getContext(), "Nieprawidłowy numer telefonu", Toast.LENGTH_SHORT).show();
                }else{
                    placeNewOrder();
                }
            }
        });

        return view;
    }

    private void placeNewOrder(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirestoreDAO dao = new FirestoreDAO(NewOrderFragment.this.getContext());
        Order newOrder = new Order();
        List<DocumentReference> docRefs = new ArrayList<>();
        for(MenuItem i : items){
            docRefs.add(dao.getFirebaseFirestore().collection("menuItems").document(i.getId()));
        }
        if(currentUser != null){
            newOrder.setUserId(currentUser.getUid());
        }
        newOrder.setAddress(address.getText().toString());
        newOrder.setAdditionalInfo(additionalInfo.getText().toString());
        newOrder.setPhoneNumber(phoneNumber.getText().toString());
        newOrder.setOrderStatus(OrderStatus.PENDING);
        newOrder.setItems(docRefs);
        newOrder.setTotalPrice(Double.parseDouble(totalPrice.getText().toString()));

        dao.addOrder(newOrder, new OrderPlacedCallback() {
            @Override
            public void onOrderPlaced() {
                Toast.makeText(NewOrderFragment.this.getContext(), "Pomyślnie złożono zamówienie", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewOrderFragment.this.getContext(), MainActivity.class));
            }
        });

    }

}