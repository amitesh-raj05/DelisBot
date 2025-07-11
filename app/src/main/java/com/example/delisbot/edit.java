package com.example.delisbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.io.IOException;
import java.util.HashMap;

public class edit extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ShapeableImageView profileIcon;
    private EditText nameEditText, emailEditText, phoneEditText;
    private MaterialButton updateButton;

    private FirebaseFirestore db; // Firestore instance
    private FirebaseAuth auth; // FirebaseAuth instance
    private String userId; // User ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        profileIcon = findViewById(R.id.profile_icon);
        nameEditText = findViewById(R.id.nameEditText); // Make sure to set the correct IDs
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        updateButton = findViewById(R.id.butt); // Button to update user info

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build());
        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        // Get the current user's ID dynamically
        userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId != null) {
            // Fetch user data from Firestore when the activity starts
            fetchUserData();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally, navigate back or handle the error
        }

        // Set click listener for profile picture

        // Update button click listener
        updateButton.setOnClickListener(v -> updateUserData());
    }

    private void fetchUserData() {
        db.collection("users").document(userId) // Replace with your collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String phone = document.getString("phone");

                            // Set data to EditText fields
                            nameEditText.setText(name);
                            emailEditText.setText(email);
                            phoneEditText.setText(phone);
                        } else {
                            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserData() {
        String updatedName = nameEditText.getText().toString().trim();
        String updatedEmail = emailEditText.getText().toString().trim();
        String updatedPhone = phoneEditText.getText().toString().trim();

        // Create a HashMap to update user data
        HashMap<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", updatedName);
        userUpdates.put("email", updatedEmail);
        userUpdates.put("phone", updatedPhone);

        db.collection("users").document(userId) // Replace with your collection name
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Update the image view with the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileIcon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
