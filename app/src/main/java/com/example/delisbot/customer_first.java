package com.example.delisbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class customer_first extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private List<Customer> customerList;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_first);

        recyclerView = findViewById(R.id.recycler_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(this,customerList);
        recyclerView.setAdapter(customerAdapter);

        fetchCustomerOrders();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, customer_first.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, add_items.class));
                return true;
            }
            return false;
        });
    }

    private void fetchCustomerOrders() {
        db.collection("orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extract customer details

                            String customerId = document.getId();


                            String userName = document.getString("userName");
                            String userEmail = document.getString("userEmail");
                            String tableNo = String.valueOf(document.getLong("tableNo"));
                            String status = document.getString("status");

                            Customer customer = new Customer(customerId,userName, userEmail, tableNo, status);

                            // Fetch the items in the order
                            List<Map<String, Object>> itemsArray = (List<Map<String, Object>>) document.get("items");
                            List<OrderItem> orders = new ArrayList<>();

                            if (itemsArray != null) {
                                for (Map<String, Object> itemData : itemsArray) {
                                    String itemIdNoString = (String) itemData.get("idno");
                                    int itemIdNo = Integer.parseInt(itemIdNoString);
                                    int quantity = ((Long) itemData.get("quantity")).intValue();

                                    // Fetch the item details
                                    db.collection("celestal")
                                            .whereEqualTo("idno", itemIdNo)
                                            .get()
                                            .addOnCompleteListener(itemTask -> {
                                                if (itemTask.isSuccessful() && !itemTask.getResult().isEmpty()) {
                                                    QueryDocumentSnapshot itemDoc = (QueryDocumentSnapshot) itemTask.getResult().getDocuments().get(0);
                                                    String itemName = itemDoc.getString("name");

                                                    // Create OrderItem and add to list
                                                    OrderItem orderItem = new OrderItem(itemName, quantity);
                                                    orders.add(orderItem);

                                                    // Once all items are processed, update the customer and notify the adapter
                                                    if (orders.size() == itemsArray.size()) {
                                                        customer.setOrders(orders);
                                                        customerList.add(customer);

                                                        // Notify the adapter on the UI thread
                                                        runOnUiThread(() -> customerAdapter.notifyDataSetChanged());
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }





}

