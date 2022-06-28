package com.example.zajecia6;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.zajecia6.model.OrderModel;
import com.example.zajecia6.model.OrderStatus;
import com.example.zajecia6.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.cancel.OnCancel;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.error.ErrorInfo;
import com.paypal.checkout.error.OnError;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;
import com.paypal.checkout.shipping.OnShippingChange;
import com.paypal.checkout.shipping.ShippingChangeActions;
import com.paypal.checkout.shipping.ShippingChangeData;

import org.jetbrains.annotations.NotNull;

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
    private PayPalButton payPalButton;
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
        payPalButton = view.findViewById(R.id.payPalButton);

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
                        totalPrice.setText(String.format("%.2f", newTotal));
                    }

                    @Override
                    public void onItemRemoved(MenuItem item) {
                        items.stream()
                                .filter(i -> i.getId().equals(item.getId()))
                                .findFirst().ifPresent(foundItem -> items.remove(foundItem));
                        double newSum = Double.parseDouble(sum.getText().toString()) - item.getPrice();
                        sum.setText(Double.toString(newSum));
                        double newTotal = newSum + Double.parseDouble(deliveryPrice.getText().toString());
                        totalPrice.setText(String.format("%.2f", newTotal));
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

        payPalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.PLN)
                                                        .value(totalPrice.getText().toString())
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order.Builder()
                                .appContext(new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build())
                                .intent(OrderIntent.CAPTURE)
                                .purchaseUnitList(purchaseUnits)
                                .build();
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                placeNewOrder();
                            }
                        });
                    }
                },
                new OnShippingChange() {
                    @Override
                    public void onShippingChanged(@NonNull ShippingChangeData shippingChangeData, @NonNull ShippingChangeActions shippingChangeActions) {

                    }
                },
                new OnCancel() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Płatność przerwana", Toast.LENGTH_SHORT).show();
                    }
                },
                new OnError() {
                    @Override
                    public void onError(@NonNull ErrorInfo errorInfo) {
                        placeNewOrder();
                        Toast.makeText(getContext(), "Błąd płatności", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return view;
    }


    private void placeNewOrder(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirestoreDAO dao = new FirestoreDAO(NewOrderFragment.this.getContext());
        OrderModel newOrderModel = new OrderModel();
        List<String> itemList = new ArrayList<>();
        for(MenuItem i : items){
            itemList.add(i.getId());
        }
        if(currentUser != null){
            newOrderModel.setUserId(currentUser.getUid());
        }
        newOrderModel.setAddress(address.getText().toString());
        newOrderModel.setAdditionalInfo(additionalInfo.getText().toString());
        newOrderModel.setPhoneNumber(phoneNumber.getText().toString());
        newOrderModel.setOrderStatus(OrderStatus.PENDING);
        newOrderModel.setItems(itemList);
        newOrderModel.setTotalPrice(Double.parseDouble(totalPrice.getText().toString()));

        dao.addOrder(newOrderModel, new OrderPlacedCallback() {
            @Override
            public void onOrderPlaced() {
                Toast.makeText(NewOrderFragment.this.getContext(), "Pomyślnie złożono zamówienie", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewOrderFragment.this.getContext(), MainActivity.class));
            }
        });


    }

}