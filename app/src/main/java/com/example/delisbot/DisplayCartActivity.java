package com.example.delisbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DisplayCartActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS = "cart_items";
    private ListView listView;
    private CartAdapter cartAdapter;
    private MaterialButton goToCartButton;
    private Spinner tableSpinner;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_cart);

        sharedPreferences = getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        listView = findViewById(R.id.cart_list_view);
        goToCartButton = findViewById(R.id.go_to_cart_button);
        tableSpinner = findViewById(R.id.table_spinner);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Set up table number options in the Spinner
        ArrayAdapter<Integer> tableAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getTableNumbers());
        tableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(tableAdapter);

        // Retrieve cart items from SharedPreferences
        ArrayList<Map<String, Integer>> cartItems = getLocalCart();

        if (cartItems != null && !cartItems.isEmpty()) {
            cartAdapter = new CartAdapter(this);
            listView.setAdapter(cartAdapter);
            updateTotalPrice();

            // Listen for changes in cart data and update total price
            cartAdapter.registerDataSetObserver(new android.database.DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    updateTotalPrice();
                }
            });
        } else {
            Toast.makeText(this, "No items in the cart", Toast.LENGTH_SHORT).show();
        }

        // Handle the "Pay Total" button click
        goToCartButton.setOnClickListener(v -> {
            int selectedTableNo = (int) tableSpinner.getSelectedItem();
            if (selectedTableNo == 0) {
                Toast.makeText(this, "Please select a table number", Toast.LENGTH_SHORT).show();
            } else {
                placeOrder(selectedTableNo);
            }
        });
    }

    // Retrieve cart items from SharedPreferences
    private ArrayList<Map<String, Integer>> getLocalCart() {
        String json = sharedPreferences.getString(CART_ITEMS, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Map<String, Integer>>>() {}.getType();
        ArrayList<Map<String, Integer>> cartItems = gson.fromJson(json, type);

        return (cartItems != null) ? cartItems : new ArrayList<>();
    }

    // Update the total price displayed on the "Pay Total" button
    private void updateTotalPrice() {
        if (cartAdapter != null) {
            int totalPrice = cartAdapter.calculateTotalPrice();
            price = totalPrice;
            goToCartButton.setText("Pay Total: Rs. " + totalPrice);
        }
    }

    // Get table numbers for the spinner (1 to 5)
    private ArrayList<Integer> getTableNumbers() {
        ArrayList<Integer> tableNumbers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            tableNumbers.add(i);
        }
        return tableNumbers;
    }

    // Fetch user details from Firestore and place order
    private void placeOrder(int tableNo) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch user details from Firestore
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        // Prepare the order data
                        ArrayList<Map<String, Integer>> cartItems = getLocalCart();
                        Vector<Map<String, Object>> orderDetails = new Vector<>();

                        // Add each cart item to the order details
                        for (Map<String, Integer> item : cartItems) {
                            for (Map.Entry<String, Integer> entry : item.entrySet()) {
                                Map<String, Object> orderItem = new HashMap<>();
                                orderItem.put("idno", entry.getKey());
                                orderItem.put("quantity", entry.getValue());
                                orderDetails.add(orderItem);
                            }
                        }

                        // Create order data
                        Map<String, Object> orderData = new HashMap<>();
                        orderData.put("userName", name);
                        orderData.put("userEmail", email);
                        orderData.put("tableNo", tableNo);
                        orderData.put("status", "waiting");
                        orderData.put("items", orderDetails);

                        // Save order to Firestore
                        db.collection("orders").add(orderData)
                                .addOnSuccessListener(documentReference -> {
                                    // Successfully placed order, now clear SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove(CART_ITEMS);
                                    editor.apply();
                                    Toast.makeText(this, "Order placed successfully", Toast.LENGTH_SHORT).show();



                                    Intent intent = new Intent(DisplayCartActivity.this, payment.class);
                                    intent.putExtra("TOTAL_PRICE",price);
                                    startActivity(intent);

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error placing order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
