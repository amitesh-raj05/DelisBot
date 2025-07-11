//package com.example.delisbot;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.util.List;
//import java.util.function.Consumer;
//
//public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
//
//    private List<Order> orderList;
//    private Consumer<Order> onOrderQuantityChanged;
//
//    // Constructor to accept order list and a callback
//    public OrderAdapter(List<Order> orderList, Consumer<Order> onOrderQuantityChanged) {
//        this.orderList = orderList;
//        this.onOrderQuantityChanged = onOrderQuantityChanged;
//    }
//
//    @NonNull
//    @Override
//    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
//        return new OrderViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
//        Order order = orderList.get(position);
//
//        holder.titleTextView.setText(order.getTitle());
//        holder.priceTextView.setText(order.getPrice());
//        holder.quantityTextView.setText(String.valueOf(order.getQuantity()));
//
//        // Load image using Glide
//        Glide.with(holder.imageView.getContext())
//                .load(order.getImageUrl())
//                .into(holder.imageView);
//
//        // Handle quantity increment and decrement
//        holder.incrementButton.setOnClickListener(v -> {
//            order.incrementQuantity();
//            onOrderQuantityChanged.accept(order);
//            notifyDataSetChanged();
//        });
//
//        holder.decrementButton.setOnClickListener(v -> {
//            order.decrementQuantity();
//            onOrderQuantityChanged.accept(order);
//            notifyDataSetChanged();
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return orderList.size();
//    }
//
//    // ViewHolder class to bind order data to views
//    public static class OrderViewHolder extends RecyclerView.ViewHolder {
//
//        private ImageView imageView;
//        private TextView titleTextView;
//        private TextView priceTextView;
//        private TextView quantityTextView;
//        private ImageView incrementButton;
//        private ImageView decrementButton;
//
//        public OrderViewHolder(View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.burger_image_view);
//            titleTextView = itemView.findViewById(R.id.title_text_view);
//            priceTextView = itemView.findViewById(R.id.price_text_view);
//            quantityTextView = itemView.findViewById(R.id.quantity);
//            incrementButton = itemView.findViewById(R.id.increment_button);
//            decrementButton = itemView.findViewById(R.id.decrement_button);
//        }
//    }
//}
