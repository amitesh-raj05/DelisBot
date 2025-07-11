package com.example.delisbot;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    ImageView burgerImageView;
    TextView titleTextView;
    TextView priceTextView;
    TextView quantityTextView;
    ImageView closeIcon;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        burgerImageView = itemView.findViewById(R.id.burger_image_view);
        titleTextView = itemView.findViewById(R.id.title_text_view);
        priceTextView = itemView.findViewById(R.id.price_text_view);
        quantityTextView = itemView.findViewById(R.id.quantity);
        closeIcon = itemView.findViewById(R.id.close_icon);
    }
}
