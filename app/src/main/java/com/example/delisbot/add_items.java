package com.example.delisbot;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;import com.google.firebase.firestore.DocumentSnapshot; // Import this
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class add_items extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText nameEditText, descriptionEditText, priceEditText, imgUrlEditText;
    private MaterialButton addButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_items);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.emailEditText);
        priceEditText = findViewById(R.id.phoneEditText);
        imgUrlEditText = findViewById(R.id.imgurl);
        addButton = findViewById(R.id.butt);

        // Set click listener for the add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoodItem();
            }
        });

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_add); // Set the home item as selected

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_homei) {
                Intent intent = new Intent(add_items.this, customer_first.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                return true;
            }else if (itemId == R.id.nav_add) {
                Intent intent = new Intent(add_items.this, add_items.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);                startActivity(intent);
                return true;
            }
            return false;
        });

    }

    private void addFoodItem() {
        final String name = nameEditText.getText().toString().trim();
        final String description = descriptionEditText.getText().toString().trim();
        final String price = priceEditText.getText().toString().trim();
        final String imgUrl = imgUrlEditText.getText().toString().trim();

        // Check for empty fields
        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || imgUrl.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current ID number from the database
        DocumentReference countRef = db.collection("id").document("count");
        countRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    long idno = task.getResult().getLong("idno");

                    // Create a food item object to save
                    Map<String, Object> foodItem = new HashMap<>();
                    foodItem.put("name", name);
                    foodItem.put("description", description);
                    foodItem.put("price", price);
                    foodItem.put("imgUrl", imgUrl);
                    foodItem.put("idno", idno);

                    // Save the food item in the "celestal" collection
                    CollectionReference celestialRef = db.collection("celestal");
                    celestialRef.add(foodItem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                // Increment the ID number
                                countRef.update("idno", idno + 1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(add_items.this, "Food item added successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(add_items.this, "Failed to increment ID number", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(add_items.this, "Failed to add food item", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(add_items.this, "Failed to get ID number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}