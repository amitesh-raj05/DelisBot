package com.example.delisbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartAdapter extends BaseAdapter {
    private static final String TAG = "CartAdapter";
    private static final String CART_PREFS = "cart_prefs";
    private static final String CART_ITEMS = "cart_items";
    private Context context;
    private ArrayList<Map<String, Integer>> cartItems;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    // Cache to store item prices to avoid repeated Firestore calls
    private Map<String, Integer> priceCache = new HashMap<>();

    public CartAdapter(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        this.gson = new Gson();

        // Initialize and load cart items from SharedPreferences
        loadCartItems();
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.cart_item_layout, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        TextView priceTextView = convertView.findViewById(R.id.price_text_view);
        TextView quantityTextView = convertView.findViewById(R.id.quantity);
        ImageView burgerImageView = convertView.findViewById(R.id.burger_image_view);
        ImageView closeIcon = convertView.findViewById(R.id.close_icon);

        // Get item `idno` and `quantity` from cart data
        Map<String, Integer> item = cartItems.get(position);
        String idn = item.keySet().iterator().next(); // Get idno as the key
        Integer idno = Integer.valueOf(idn);
        Integer quantity = item.get(idn);

        closeIcon.setOnClickListener(view -> removeItem(position));

        // Set quantity directly
        quantityTextView.setText(String.valueOf(quantity));

        // Check price cache first; if not found, query Firestore
        if (priceCache.containsKey(idn)) {
            int price = priceCache.get(idn);
            priceTextView.setText("Rs. " + price);
        } else {
            // Query Firestore to find the document with matching `idno`
            db.collection("celestal")
                    .whereEqualTo("idno", idno)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String name = document.getString("name");
                                String priceString = document.getString("price");
                                String imageUrl = document.getString("imgUrl");

                                int price = priceString != null ? Integer.parseInt(priceString) : 0;
                                priceCache.put(idn, price); // Cache the price

                                titleTextView.setText(name != null ? name : "Unknown Item");
                                priceTextView.setText("Rs. " + price);

                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Picasso.get().load(imageUrl).into(burgerImageView);
                                } else {
                                    burgerImageView.setImageResource(R.drawable.birgir); // Placeholder if no URL
                                }
                                notifyDataSetChanged(); // Update UI with new price
                            }
                        } else {
                            titleTextView.setText("Item not found");
                            priceTextView.setText("N/A");
                            burgerImageView.setImageResource(R.drawable.birgir);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading item with idno " + idno, e);
                        titleTextView.setText("Error loading item");
                        priceTextView.setText("N/A");
                        burgerImageView.setImageResource(R.drawable.birgir);
                    });
        }

        return convertView;
    }

    // Remove an item from the cart and update SharedPreferences
    private void removeItem(int position) {
        cartItems.remove(position);
        saveCartItems();  // Save updated cart back to SharedPreferences
        notifyDataSetChanged();

        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    // Calculate the total price of the cart
    public int calculateTotalPrice() {
        int totalPrice = 0;
        for (Map<String, Integer> item : cartItems) {
            String idn = item.keySet().iterator().next(); // Get idno
            int quantity = item.get(idn);

            Integer price = priceCache.get(idn); // Get price from cache
            if (price != null) {
                totalPrice += price * quantity;
            }
        }
        return totalPrice;
    }

    // Load cart items from SharedPreferences
    private void loadCartItems() {
        String json = sharedPreferences.getString(CART_ITEMS, null);
        Type type = new TypeToken<ArrayList<Map<String, Integer>>>() {}.getType();
        cartItems = gson.fromJson(json, type);

        if (cartItems == null) {
            cartItems = new ArrayList<>(); // Initialize empty list if null
        }
    }

    // Save cart items to SharedPreferences
    private void saveCartItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(cartItems);
        editor.putString(CART_ITEMS, json);
        editor.apply();
    }

    // Method to add an item to the cart and save to SharedPreferences
    public void addItem(Map<String, Integer> item) {
        cartItems.add(item);
        saveCartItems();
        notifyDataSetChanged();
    }
}
