package com.example.delisbot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class homepage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BurgerAdapter burgerAdapter;
    private List<BurgerItem> allBurgers; // All burgers data
    private int currentBurgerIndex = 0; // Current index
    private static final int BURGERS_PER_PAGE = 5;
    private boolean doubleBackToExitPressedOnce = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isLoading = false;
    String orderId = null;
    MaterialButton mate;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        auth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        ShapeableImageView profileIcon = findViewById(R.id.profile_icon);

        // Set OnClickListener to navigate to Profile Activity
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homepage.this, profile.class); // Replace 'profile.class' with your actual profile activity
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recycler_view); // Update with your RecyclerView ID
        allBurgers = fetchAllBurgers(); // Fetch all burgers
        burgerAdapter = new BurgerAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(burgerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button butt = findViewById(R.id.my_material_button);
        Intent inten = new Intent(homepage.this, place.class);

        // Trigger the intent to display the cart
        butt.setOnClickListener(v -> {
            Intent inte = new Intent(homepage.this, DisplayCartActivity.class);
            this.startActivity(inte);
        });

        // Load initial burgers
        loadMoreBurgers();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastVisibleItemPosition() >= burgerAdapter.getItemCount() - 1) {
                    // Load more burgers if scrolled to the end
                    loadMoreBurgers();
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Set the home item as selected

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(homepage.this, homepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_account) {
                Intent intent = new Intent(homepage.this, profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_feedback) {
                Intent intent = new Intent(homepage.this, feedback.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_Order) {
                Intent intent = new Intent(homepage.this, place.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void loadMoreBurgers() {
        if (isLoading) return; // Prevent duplicate loading
        isLoading = true;

        db.collection("celestal")
                .orderBy("idno") // Order by idno to load burgers in order
                .startAt(currentBurgerIndex)
                .limit(BURGERS_PER_PAGE)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<BurgerItem> newBurgers = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String imgUrl = document.getString("imgUrl");

                            // Handle 'price' as either a Number or a String
                            double price = 0.0; // Default value in case of parsing issues
                            Object priceObj = document.get("price"); // Retrieve price object

                            if (priceObj instanceof Number) {
                                price = ((Number) priceObj).doubleValue(); // Cast to double if it’s a Number
                            } else if (priceObj instanceof String) {
                                try {
                                    price = Double.parseDouble((String) priceObj); // Parse if stored as String
                                } catch (NumberFormatException ex) {
                                    Log.e("Firestore", "Error parsing price from String: " + priceObj, ex);
                                }
                            } else {
                                Log.e("Firestore", "Unexpected type for price: " + priceObj);
                            }

                            // Set up the burger item
                            BurgerItem burgerItem = new BurgerItem(name, description, price, imgUrl);
                            newBurgers.add(burgerItem);
                        }

                        // Add burgers to adapter and update index
                        burgerAdapter.addMoreBurgers(newBurgers);
                        currentBurgerIndex += newBurgers.size();
                    } else {
                        Log.e("Firestore", "Error fetching documents: ", task.getException());
                    }
                    isLoading = false;
                });
    }

    private List<BurgerItem> fetchAllBurgers() {
        List<BurgerItem> burgers = new ArrayList<>();
        return burgers;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the intent

        // Set 'Home' as selected when returning to this activity
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
            if (doubleBackToExitPressedOnce) {
                finish();
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void startNewOrder(String tableNumber) {
        String userId = auth.getCurrentUser().getUid(); // Get current user's ID

        // Fetch user data from Firestore asynchronously
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve data from Firestore
                        String userName = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");

                        // Create an initial map with user information
                        Map<String, Object> userInfoMap = new HashMap<>();
                        userInfoMap.put("tableNumber", tableNumber);
                        userInfoMap.put("userName", userName);
                        userInfoMap.put("email", email);
                        userInfoMap.put("items", new HashMap<>()); // Placeholder for items to be added later

                        // Add a new document to the "order" collection
                        db.collection("order")
                                .add(userInfoMap)
                                .addOnSuccessListener(documentReference -> {
                                    orderId = documentReference.getId(); // Save the document ID
                                    Log.d("Firestore", "Order started with ID: " + orderId);
                                    Toast.makeText(homepage.this, "Order started successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error starting order", e);
                                    Toast.makeText(homepage.this, "Failed to start order", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(homepage.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(homepage.this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
