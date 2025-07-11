package com.example.delisbot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class openingpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_openingpage);

        MaterialButton butt = findViewById(R.id.butt);
        MaterialButton but_customer=findViewById(R.id.but_customer);




        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserChoice("owner");
                Intent intent = new Intent(openingpage.this,owner_signup.class);
                startActivity(intent);

            }
        });

        but_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserChoice("customer");
                Intent custom = new Intent(openingpage.this,owner_first.class);
                startActivity(custom);
            }
        });

    }

    // After user logs in and selects "customer" or "owner"
    private void saveUserChoice(String userType) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", userType); // Save either "customer" or "owner"// Save login status as true
        editor.apply();
    }

}