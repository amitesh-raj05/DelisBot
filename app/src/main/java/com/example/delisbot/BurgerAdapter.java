package com.example.delisbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BurgerAdapter extends RecyclerView.Adapter<BurgerAdapter.BurgerViewHolder> {
    private List<BurgerItem> burgerList;
    private Context context;
    private FirebaseFirestore db;
    private String orderId;
    private SharedPreferences sharedPreferences;
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS = "cart_items";

    public BurgerAdapter(List<BurgerItem> burgerList, Context context) {
        this.burgerList = burgerList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);

        // Get the current user's UID from Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            this.orderId = auth.getCurrentUser().getUid(); // Use the UID as the orderId
        } else {
            this.orderId = "default_order_id"; // Default if not authenticated
        }
    }

    @NonNull
    @Override
    public BurgerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new BurgerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BurgerViewHolder holder, int position) {
        BurgerItem currentItem = burgerList.get(position);

        holder.titleTextView.setText(currentItem.getTitle());
        holder.descriptionTextView.setText(currentItem.getDescription());
        holder.priceTextView.setText("Rs. " + currentItem.getPrice());
        holder.counterTextView.setText(String.valueOf(currentItem.getCounter()));

        // Load the image with Picasso
        String imageUrl = currentItem.getImageUrl();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.animm)
                .into(holder.imageView);

        // Increment counter
        holder.buttonIncrement.setOnClickListener(v -> {
            int count = currentItem.getCounter();
            currentItem.setCounter(++count);
            holder.counterTextView.setText(String.valueOf(count));
        });

        // Decrement counter
        holder.buttonDecrement.setOnClickListener(v -> {
            int count = currentItem.getCounter();
            if (count > 0) {
                currentItem.setCounter(--count);
                holder.counterTextView.setText(String.valueOf(count));
            }
        });

        // Add button to save selected quantity locally
        holder.buttonAdd.setOnClickListener(v -> {
            if (currentItem.getCounter() > 0) {
                // Fetch idno from Firestore using the item's name
                fetchIdNoByName(currentItem.getTitle(), idno -> {
                    if (idno != null) {
                        saveItemToLocalCart(idno, currentItem.getCounter());
                        Toast.makeText(context, "Item added to cart locally", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "Please select a quantity greater than 0", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return burgerList.size();
    }

    public void addMoreBurgers(List<BurgerItem> newBurgers) {
        int initialSize = burgerList.size();
        burgerList.addAll(newBurgers);
        notifyItemRangeInserted(initialSize, newBurgers.size());
    }

    // Method to fetch idno by item name and convert the idno to a string
    private void fetchIdNoByName(String name, FirestoreCallback callback) {
        db.collection("celestal") // Replace with your actual collection name
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Fetch the idno as a number first
                            Object idnoObject = document.get("idno");

                            String idno = null;
                            if (idnoObject instanceof Number) {
                                idno = String.valueOf(idnoObject); // Convert to String
                            }

                            callback.onCallback(idno); // Return the idno as a string
                            return;
                        }
                    } else {
                        callback.onCallback(null); // If item is not found
                    }
                });
    }

    // Save item to SharedPreferences
    // Save item to SharedPreferences
    // Save item to SharedPreferences
    private void saveItemToLocalCart(String idno, int quantity) {
        Gson gson = new Gson();
        ArrayList<Map<String, Integer>> cartItems = getLocalCart();

        // Check if item already exists in the cart
        boolean itemExists = false;
        for (Map<String, Integer> item : cartItems) {
            if (item.containsKey(idno)) {
                // Item found, update the quantity

                item.put(idno,  quantity); // Add the new quantity to the existing one
                itemExists = true;
                break;
            }
        }

        // If item does not exist in the cart, add it as a new entry
        if (!itemExists) {
            Map<String, Integer> newItem = new HashMap<>();
            newItem.put(idno, quantity);
            cartItems.add(newItem);
        }

        // Save updated cart back to SharedPreferences
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_ITEMS, json).apply();

        // Log the idno and updated cart
        Log.d("BurgerAdapter", "Cart updated with idno: " + idno + ", quantity: mgmdmg" + quantity);
        Log.d("BurgerAdapter", "Updated cart: " + cartItems);
    }



    // Retrieve cart items from SharedPreferences
    private ArrayList<Map<String, Integer>> getLocalCart() {
        String json = sharedPreferences.getString(CART_ITEMS, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Map<String, Integer>>>() {}.getType();
        ArrayList<Map<String, Integer>> cartItems = gson.fromJson(json, type);

        return (cartItems != null) ? cartItems : new ArrayList<>();
    }

    // Callback interface to handle the result of fetching idno
    public interface FirestoreCallback {
        void onCallback(String idno);
    }

    static class BurgerViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, priceTextView, counterTextView;
        ImageView imageView;
        MaterialButton buttonDecrement, buttonIncrement, buttonAdd;

        public BurgerViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            counterTextView = itemView.findViewById(R.id.counter_text);
            imageView = itemView.findViewById(R.id.burger_image_view);
            buttonDecrement = itemView.findViewById(R.id.button_decrement);
            buttonIncrement = itemView.findViewById(R.id.button_increment);
            buttonAdd = itemView.findViewById(R.id.butt); // Assuming there's an "Add" button in your layout
        }
    }
}

