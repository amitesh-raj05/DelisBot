package com.example.delisbot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class forgetpassword extends AppCompatActivity {

    private EditText emailEditText;
    private MaterialButton butt;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgetpassword);

        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText); // Ensure ID matches your EditText
        butt = findViewById(R.id.butt);

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                // Validate email input
                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(forgetpassword.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Email exists and reset email is sent
                Toast.makeText(forgetpassword.this, "Verification code sent to your email", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(forgetpassword.this, owner_first.class);
                intent.putExtra("EXTRA_EMAIL", email);
                startActivity(intent);
            } else {
                // Email does not exist or there was another error
                Exception exception = task.getException();
                if (exception != null && exception.getMessage() != null && exception.getMessage().contains("no user record")) {
                    Toast.makeText(forgetpassword.this, "Account does not exist. Please create an account.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(forgetpassword.this, "Error sending reset email. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
