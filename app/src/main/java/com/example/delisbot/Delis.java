package com.example.delisbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class Delis extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delis);

        LottieAnimationView rob1 = findViewById(R.id.rob1);
        rob1.setScaleY(1f);
        rob1.setScaleX(1f);
        rob1.setSpeed(2);

        // Show splash screen for 1.5 seconds, then check user status
        new Handler().postDelayed(this::checkUserStatus, 1500);
    }

    private void checkUserStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String userType = sharedPreferences.getString("userType", "");

        if (isLoggedIn) {
            // Navigate based on user type
            if ("customer".equals(userType)) {
                startActivity(new Intent(this, homepage.class));
            } else if ("owner".equals(userType)) {
                startActivity(new Intent(this, customer_first.class));
            }
        } else {
            // Navigate to selection page if not logged in
            if ("customer".equals(userType)) {
                startActivity(new Intent(this, owner_first.class));
            } else if ("owner".equals(userType)) {
                startActivity(new Intent(this, owner_signup.class));
            }
            else
                startActivity(new Intent(this, openingpage.class));
        }
        finish(); // Close the splash screen activity
    }
}
