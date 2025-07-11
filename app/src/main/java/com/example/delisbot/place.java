package com.example.delisbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class place extends AppCompatActivity {
    private List<Order> orderList; // List to hold orders
    private MaterialButton goToCartButton;

    private FirebaseFirestore db;
    private CollectionReference ordersRef;
    private SharedPreferences sharedPreferences;

    private static final String CART_ITEMS = "order_prefs"; // SharedPreferences name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place);

//        // Initialize Firestore
//        db = FirebaseFirestore.getInstance();
//        ordersRef = db.collection("celestal");
//
//        // Initialize the RecyclerView and the order list
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Initialize the button for displaying total price
//        goToCartButton = findViewById(R.id.go_to_cart_button);
//
//        // Initialize order list
//        orderList = new ArrayList<>();
//
//        // Fetch cart items from SharedPreferences
//        sharedPreferences = getSharedPreferences(CART_ITEMS, MODE_PRIVATE);
//        ArrayList<Map<String, Integer>> cartItems = getLocalCart();
//
//        // Debug SharedPreferences
//        if (cartItems != null) {
//            Log.d("place", "Cart items from SharedPreferences: " + cartItems);
//        }
//
//        // Loop through cart items and fetch details from Firestore
//        for (Map<String, Integer> cartItem : cartItems) {
//            for (Map.Entry<String, Integer> entry : cartItem.entrySet()) {
//                String idno = entry.getKey();
//                int quantity = entry.getValue();
//                if (quantity > 0) {
//                    fetchProductDetails(idno, quantity);
//                }
//            }
//        }
//
//        // Set the adapter for RecyclerView
//        OrderAdapter adapter = new OrderAdapter(orderList, this::onOrderQuantityChanged);
//        recyclerView.setAdapter(adapter);
//
//        // Calculate total price
//        double initialTotalPrice = calculateTotalPrice(orderList);
//        updateTotalPriceButton(initialTotalPrice);
//
//        // Set up the 'Go to Cart' button
//        goToCartButton.setOnClickListener(v -> {
//            double finalTotalPrice = calculateTotalPrice(orderList); // Get the final total price
//            Intent intent = new Intent(place.this, payment.class);
//            intent.putExtra("TOTAL_PRICE", finalTotalPrice); // Pass total price to payment class
//            startActivity(intent);
//        });
//    }
//
//    // Fetch product details from Firestore
//    private void fetchProductDetails(String idno, int quantity) {
//        Log.d("place", "Fetching product details for idno: " + idno + " with quantity: " + quantity);
//        ordersRef.whereEqualTo("idno", idno).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                    for (QueryDocumentSnapshot document : querySnapshot) {
//                        String imageUrl = document.getString("imageurl");
//                        String name = document.getString("name");
//                        String price = document.getString("price");
//
//                        Log.d("place", "Fetched product: name = " + name + ", price = " + price + ", imageUrl = " + imageUrl);
//
//                        // Create an order object and add it to the list
//                        orderList.add(new Order(imageUrl, name, price, quantity));
//                    }
//                    // Notify the adapter that the data has been updated
//                    RecyclerView recyclerView = findViewById(R.id.recycler_view);
//                    OrderAdapter adapter = (OrderAdapter) recyclerView.getAdapter();
//                    if (adapter != null) {
//                        adapter.notifyDataSetChanged();
//                    }
//                } else {
//                    Log.d("place", "No products found in Firestore for idno: " + idno);
//                }
//            } else {
//                Log.e("place", "Error getting product details: " + task.getException());
//                Toast.makeText(place.this, "Error getting product details", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Calculate total price for the orders
//    private double calculateTotalPrice(List<Order> orders) {
//        double total = 0;
//        for (Order order : orders) {
//            double price = Double.parseDouble(order.getPrice()); // Assuming price is stored as String
//            total += price * order.getQuantity();
//        }
//        return total;
//    }
//
//    // Update the button with the total price
//    private void updateTotalPriceButton(double totalPrice) {
//        String buttonText = String.format("Pay: ₹%.2f", totalPrice);
//        goToCartButton.setText(buttonText);
//    }
//
//    // Get cart items from SharedPreferences
//    private ArrayList<Map<String, Integer>> getLocalCart() {
//        String json = sharedPreferences.getString(CART_ITEMS, null);
//        if (json != null) {
//            Gson gson = new Gson();
//            Type type = new TypeToken<ArrayList<Map<String, Integer>>>(){}.getType();
//            return gson.fromJson(json, type);
//        }
//        return new ArrayList<>();
//    }
//
//    // Method to handle order quantity change
//    private void onOrderQuantityChanged(Order order) {
//        // Handle the logic when the order quantity changes
//        Log.d("place", "Order quantity changed for " + order.getName() + ", new quantity: " + order.getQuantity());
//
//        // Recalculate and update the total price
//        double newTotalPrice = calculateTotalPrice(orderList);
//        updateTotalPriceButton(newTotalPrice);
    }
}
