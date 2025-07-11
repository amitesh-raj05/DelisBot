package com.example.delisbot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class setup extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText name, password, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        MaterialButton butt = findViewById(R.id.buttt);
        name = findViewById(R.id.name);
        password = findViewById(R.id.shos);
        email = findViewById(R.id.phone); // Assuming 'phone' EditText is used for email input now

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString().trim();
                String userName = name.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                // Validate email format
                if (!isValidEmail(emailInput)) {
                    Toast.makeText(setup.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check password length
                if (passwordInput.length() < 6) {
                    Toast.makeText(setup.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with registration if inputs are valid
                registerUserWithEmail(emailInput, passwordInput, userName);
            }
        });
    }

    private boolean isValidEmail(String email) {
        // Regular expression to validate an email
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        return emailPattern.matcher(email).matches();
    }

    private void registerUserWithEmail(String email, String password, String userName) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        // Set display name
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .build();
                            user.updateProfile(profileUpdates);

                            // Send email verification link
                            sendEmailVerification(user);
                        }
                    } else {
                        Log.e("EmailAuth", "Registration failed: " + task.getException());
                        Toast.makeText(setup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(setup.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        // Navigate to another activity if needed
                        Intent intent = new Intent(setup.this, Verification.class);
                        intent.putExtra("EXTRA_NAME", user.getDisplayName());
                        intent.putExtra("EXTRA_EMAIL", user.getEmail());
                        intent.putExtra("EXTRA_Password", user.getEmail());
                        intent.putExtra("EXTRA_PHONE", "0000000000"); // Ensure 'phoneNumber' is obtained from your input field.


                        startActivity(intent);
                    } else {
                        Log.e("EmailAuth", "Failed to send verification email.");
                        Toast.makeText(setup.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
