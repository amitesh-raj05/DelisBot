package com.example.delisbot;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class payment extends AppCompatActivity {

    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        totalPrice = getIntent().getIntExtra("TOTAL_PRICE", 0);

        // You can now use totalPrice for further payment processing
        // For example, display it in a TextView
        TextView totalPriceTextView = findViewById(R.id.total_price_text_view);
        totalPriceTextView.setText(String.format("Total: ₹%.2f", totalPrice));
    }
}