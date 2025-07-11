package com.example.delisbot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private List<Customer> customerList;
    private final Context context;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.customerList = customerList;
        this.context = context;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.customerName.setText(customer.getName());
        holder.customerEmail.setText(customer.getEmail());
        holder.tableNo.setText(customer.getTableNo());

        // Set the initial state of the toggle button based on customer status
        if ("Serviced".equals(customer.getStatus())) {
            holder.statusToggle.setChecked(true);
            holder.statusToggle.setBackgroundResource(R.drawable.toggle_button_on); // Green background
        } else {
            holder.statusToggle.setChecked(false);
            holder.statusToggle.setBackgroundResource(R.drawable.toggle_button_off); // Orange background
        }

        // Toggle button listener to update status
        holder.statusToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newStatus = isChecked ? "Serviced" : "Waiting";
            updateOrderStatus(newStatus, customer.getId(), holder.statusToggle); // Update order status in Firestore
            updateRobotableStatus(newStatus, customer.getTableNo()); // Update table status in Realtime Database
        });

        // Set up RecyclerView for order items
        OrdersAdapter orderAdapter = new OrdersAdapter(customer.getOrders());
        holder.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.ordersRecyclerView.setAdapter(orderAdapter);

        // Show/hide orders when the expand button is clicked
        holder.expandButton.setOnClickListener(v -> {
            if (holder.ordersRecyclerView.getVisibility() == View.VISIBLE) {
                holder.ordersRecyclerView.setVisibility(View.GONE);
            } else {
                holder.ordersRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, customerEmail, tableNo;
        ToggleButton statusToggle;
        MaterialButton expandButton;
        RecyclerView ordersRecyclerView;

        public CustomerViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customer_name_value);
            customerEmail = itemView.findViewById(R.id.customer_email_value);
            tableNo = itemView.findViewById(R.id.table_no_value);
            statusToggle = itemView.findViewById(R.id.status_toggle_button);
            expandButton = itemView.findViewById(R.id.expand_button);
            ordersRecyclerView = itemView.findViewById(R.id.orders_recycler_view);
        }
    }

    /**
     * Update the order status in Firestore.
     */
    private void updateOrderStatus(String status, String customerId, ToggleButton statusToggleButton) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders").document(customerId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated status
                    Toast.makeText(context, "Status updated to: " + status, Toast.LENGTH_SHORT).show();

                    // Change the button background based on the new status
                    if ("Serviced".equals(status)) {
                        statusToggleButton.setBackgroundResource(R.drawable.toggle_button_on);
                    } else {
                        statusToggleButton.setBackgroundResource(R.drawable.toggle_button_off);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Update the table number in the Realtime Database if the status is "Serviced".
     */
    private void updateRobotableStatus(String status, String tableNo) {
        if ("Serviced".equals(status)) {
            // Specify the regional URL for the Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://delis-bot-default-rtdb.asia-southeast1.firebasedatabase.app");
            DatabaseReference tableRef = database.getReference("number/table");

            try {
                // Parse tableNo to integer
                int tableNumber = Integer.parseInt(tableNo);

                // Update the 'table' field with the integer value of table number
                tableRef.setValue(tableNumber)
                        .addOnSuccessListener(aVoid -> {
                            // Successfully updated the table field
                            Toast.makeText(context, "Table " + tableNo + " updated as Serviced", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Handle the error
                            Toast.makeText(context, "Failed to update table number", Toast.LENGTH_SHORT).show();
                        });
            } catch (NumberFormatException e) {
                // Handle the error if tableNo is not a valid integer
                Toast.makeText(context, "Invalid table number format", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
