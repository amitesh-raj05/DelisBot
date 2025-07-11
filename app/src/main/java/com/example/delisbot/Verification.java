package com.example.delisbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Verification extends AppCompatActivity {

    private TextView timerTextView, emailii;
    private LottieAnimationView lottieAnimationView;
    private static final long TIMER_DURATION = 300000; // 5 minutes
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private CountDownTimer countDownTimer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Retrieve email and name from intent
        String email = getIntent().getStringExtra("EXTRA_EMAIL");
        String name = getIntent().getStringExtra("EXTRA_NAME");
        String phone = getIntent().getStringExtra("EXTRA_PHONE"); // Placeholder for phone input
        user = auth.getCurrentUser();

        emailii = findViewById(R.id.email);
        emailii.setText(email);

        timerTextView = findViewById(R.id.timerTextView);
        lottieAnimationView = findViewById(R.id.rob1);

        // Start Lottie animation with infinite loop
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView.playAnimation();

        // Start the timer
        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update timer TextView every second
                int secondsRemaining = (int) millisUntilFinished / 1000;
                int minutes = secondsRemaining / 60;
                int seconds = secondsRemaining % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

                // Periodically check email verification status
                checkEmailVerificationStatus();
            }

            public void onFinish() {
                // Stop animation and display "Time's Up!" message
                lottieAnimationView.setRepeatCount(0);
                lottieAnimationView.setAnimation(R.raw.timeup);
                lottieAnimationView.playAnimation();
                timerTextView.setText("Time's Up!");

                // Show toast message and delete unverified user account
                Toast.makeText(Verification.this, "User not registered", Toast.LENGTH_SHORT).show();
                deleteUnverifiedUser();
            }
        }.start();
    }

    private void checkEmailVerificationStatus() {
        user.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && user.isEmailVerified()) {
                // If verified, cancel timer, stop animation, and save user details to Firestore
                countDownTimer.cancel();
                lottieAnimationView.cancelAnimation();

                String name = getIntent().getStringExtra("EXTRA_NAME");
                String email = getIntent().getStringExtra("EXTRA_EMAIL");
                String phone = getIntent().getStringExtra("EXTRA_PHONE"); // Placeholder for phone input

                saveUserToFirestore(name, email, phone); // Save user info to Firestore

                Toast.makeText(Verification.this, "Email verified!", Toast.LENGTH_SHORT).show();
                saveUserChoice();
                Intent intent = new Intent(Verification.this, homepage.class);
                startActivity(intent);
                finish(); // End this activity
            }
        });
    }

    private void saveUserToFirestore(String name, String email, String phone) {
        // Log the entry into the method
        Log.d("Firestore", "Entering saveUserToFirestore method.");

        // Check if the user is authenticated
        if (user != null) {
            String userId = user.getUid(); // Get user ID
            Log.d("Firestore", "Authenticated user ID: " + userId);

            // Create a user map to store in Firestore
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("phone", phone); // Placeholder for phone input

            // Log the user data to be saved
            Log.d("Firestore", "User data to save: " + userData.toString());

            // Save user data to Firestore under the user's UID
            db.collection("users").document(userId)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "User added successfully: " + userId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding user: " + e.getMessage());
                    });
        } else {
            Log.e("Firestore", "User is null, cannot save to Firestore.");
        }

        // Log the exit from the method
        Log.d("Firestore", "Exiting saveUserToFirestore method.");
    }

    private void saveUserChoice() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);  // Save login status as true
        editor.apply();
    }

    private void deleteUnverifiedUser() {
        if (user != null && !user.isEmailVerified()) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("UserDeletion", "Unverified user account deleted successfully.");
                            Toast.makeText(Verification.this, "Unverified user account deleted.", Toast.LENGTH_SHORT).show();

                            // Redirect to setup screen
                            Intent intent = new Intent(Verification.this, setup.class);
                            startActivity(intent);
                            finish(); // End current activity
                        } else {
                            Log.e("UserDeletion", "Failed to delete unverified user account: " + task.getException());
                            Toast.makeText(Verification.this, "Error deleting account. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
