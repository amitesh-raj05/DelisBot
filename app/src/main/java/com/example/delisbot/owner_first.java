package com.example.delisbot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class owner_first extends AppCompatActivity {

    // Declare variables but don't initialize them outside of onCreate
    Drawable newDraw;
    EditText pass,em;
    Button butt;
    private FirebaseAuth auth;


    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        // Set the content view first
        setContentView(R.layout.activity_owner_first);

        // Initialize your UI components here
        pass = findViewById(R.id.passwordEditText);
        newDraw = ContextCompat.getDrawable(this, R.drawable.showhide);
        butt = findViewById(R.id.butt);
        TextView forget = findViewById(R.id.forget);
        TextView sign = findViewById(R.id.signup);
        em = findViewById(R.id.emailEditText);



        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the desired activity
                Intent intent = new Intent(owner_first.this, forgetpassword.class);
                // Start the activity
                startActivity(intent);
            }
        });



        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the desired activity
                Intent intent = new Intent(owner_first.this, setup.class);
                // Start the activity
                startActivity(intent);
            }
        });

        Intent intent = new Intent(owner_first.this,homepage.class);


        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = em.getText().toString().trim();
                String password = pass.getText().toString().trim();

                // Validate input
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(owner_first.this, "Email and Password are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Attempt to sign in with email and password
                signInUser(email, password);
            }
        });






        // Set up the touch listener
        pass.setOnTouchListener((v, event) -> {
            int flag =0 ;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = pass.getCompoundDrawables()[2];
                if (drawableEnd != null) {
                    float drawableWidth = drawableEnd.getIntrinsicWidth();
                    boolean isDrawableClicked = event.getRawX() >= (pass.getRight() - drawableWidth);

                    if (isDrawableClicked) {
                        // Using resource IDs directly





                        if (pass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            // Show the password
                            pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hideshow, 0);
                            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else{
                            // Hide the password
                            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.showhide, 0);
                        }
                        pass.setSelection(pass.getText().length());

                        return true; // Consume the event
                    }
                }
            }
            return false; // Pass other events to the EditText
        });
    }

    private void signInUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success, navigate to Homepage
                        Log.d("Login", "signInWithEmail:success");
                        saveUserChoice();
                        Intent intent = new Intent(owner_first.this, homepage.class);
                        startActivity(intent);
                        finish(); // Close the login activity
                    } else {
                        // If sign-in fails, display a message to the user
                        Log.w("Login", "signInWithEmail:failure", task.getException());
                        Toast.makeText(owner_first.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // After user logs in and selects "customer" or "owner"
    private void saveUserChoice() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);  // Save login status as true
        editor.apply();
    }



}
