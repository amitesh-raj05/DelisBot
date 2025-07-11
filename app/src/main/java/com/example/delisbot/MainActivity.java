package com.example.delisbot;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    private static final int SPEECH_REQUEST_CODE = 0;
    private SeekBar seekBar;
    private TextView progressText;

    Button btnCenter, btnUp, btnDown, btnLeft, btnRight;
    int flag = 0;
    int flagl = 0;
    int flagr = 0;
    int flagu = 0;
    int flagd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnCenter = findViewById(R.id.btnCenter);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);

        seekBar = findViewById(R.id.seekBar);
        progressText = findViewById(R.id.progressText);

        // Set initial progress text
        progressText.setText("Speed: " + seekBar.getProgress() + "%");

        // Listener for SeekBar change
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update TextView with progress value
                progressText.setText("Speed: " + progress + "%");
                int speed = (255*progress)/100;

                mDatabase.child("your-path").setValue(speed).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Speed Engaged!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Can't Speed Up! Traffic Ahead!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Do something when the user starts dragging the seek bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Do something when the user stops dragging the seek bar
            }
        });

        // Center Button
        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";
                if (flag == 0) {
                    resetAllButtons();
                    data = "stop";
                    flag = 1;
                    btnCenter.setText("STOP");
                    btnCenter.setBackgroundTintList(ColorStateList.valueOf(0xFFE75656));
                } else {
                    flag = 0;
                    data = "stop";
                    btnCenter.setText("STOP");
                    btnCenter.setBackgroundTintList(ColorStateList.valueOf(0x25C120));
                }
                 sendDataToFirebase(data);
            }
        });



        // Left Button
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";
                if (flagl == 0) {
                    resetAllButtons();
                    data = "left";
                    flagl = 1;
                    btnLeft.setBackgroundTintList(ColorStateList.valueOf(0xFFB6B4B4));
                } else {
                    flagl = 0;
                    data = "stop";
                    btnLeft.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // Reset to white
                }
                 sendDataToFirebase(data);
            }
        });

        // Right Button
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";
                if (flagr == 0) {
                    resetAllButtons();
                    data = "right";
                    flagr = 1;
                    btnRight.setBackgroundTintList(ColorStateList.valueOf(0xFFB6B4B4));
                } else {
                    flagr = 0;
                    data = "stop";
                    btnRight.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // Reset to white
                }
                 sendDataToFirebase(data);
            }
        });

        // Up Button
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";
                if (flagu == 0) {
                    resetAllButtons();
                    data = "up";
                    flagu = 1;
                    btnUp.setBackgroundTintList(ColorStateList.valueOf(0xFFB6B4B4));
                } else {
                    flagu = 0;
                    data = "stop";
                    btnUp.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // Reset to white
                }
                 sendDataToFirebase(data);
            }
        });

        // Down Button
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";
                if (flagd == 0) {
                    resetAllButtons();
                    data = "down";
                    flagd = 1;
                    btnDown.setBackgroundTintList(ColorStateList.valueOf(0xFFB6B4B4));
                } else {
                    flagd = 0;
                    data = "stop";
                    btnDown.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // Reset to white
                }
                 sendDataToFirebase(data);
            }
        });



    }

    // Resets all buttons and their corresponding flags to default state
    private void resetAllButtons() {
        flag = flagl = flagr = flagu = flagd = 0; // Reset all flags

        // Reset button states
        btnCenter.setText("STOP");
        btnCenter.setBackgroundTintList(ColorStateList.valueOf(0x25C120)); // Green for ON

        btnLeft.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // White
        btnRight.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // White
        btnUp.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // White
        btnDown.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF)); // White
    }


    private void sendDataToFirebase(String data) {
        // Writing data to the "/your-path" in Firebase
        mDatabase.child("your-name").setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Gear Engaged!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Gear Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}




