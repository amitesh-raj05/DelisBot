package com.example.delisbot;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class owner_signup extends AppCompatActivity {

    private static final String CORRECT_EMAIL = "princekumar23142@gmial.com";
    private static final String CORRECT_PASSWORD = "123456789";
    private EditText emailEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owner_signup);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.butt);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.equals(CORRECT_EMAIL) && password.equals(CORRECT_PASSWORD)) {
                    // Email and password match, proceed with login
                    Toast.makeText(owner_signup.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    saveUserChoice();
                     Intent intent = new Intent(owner_signup.this, customer_first.class);
                     startActivity(intent);
                     finish();

                } else {
                    // Email or password is incorrect, show an error message
                    Toast.makeText(owner_signup.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void saveUserChoice() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);  // Save login status as true
        editor.apply();
    }
}