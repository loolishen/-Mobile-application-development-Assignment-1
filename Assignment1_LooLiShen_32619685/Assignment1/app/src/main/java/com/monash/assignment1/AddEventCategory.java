package com.monash.assignment1;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class AddEventCategory extends AppCompatActivity {

    Switch isActive;

    EditText etEventName;
    EditText etEventCount;
    Switch etEventActive;
    EditText eventID;


    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEventName =findViewById(R.id.editTextEventName);
        etEventCount =findViewById(R.id.editTextEventCount);
        etEventActive =findViewById(R.id.switchIsActive1);

        // this is for the SMS
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
        }, 0);

        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
        /*
         * Register the broadcast handler with the intent filter that is declared in
         * class SMSReceiver @line 11
         * */
        registerReceiver(myBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER), RECEIVER_EXPORTED);
    }

    public void onSaveCategoryClick(View view) {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder("C");

        Random random = new Random();

        for (int i = 0; i < 2; i++) {
            int randomIndex = random.nextInt(characters.length());
            result.append(characters.charAt(randomIndex));
        }

        result.append("-");

        for (int j = 0; j < 4; j++) {
            int randomDigit = random.nextInt(10);
            result.append(randomDigit);
        }

        // and it inputs in to the plain text after the button is clicked
        eventID = findViewById(R.id.editTextCategoryID);
        eventID.setText(result.toString());
        String eID = eventID.getText().toString();

        TextView eventName = findViewById(R.id.editTextEventName);
        String eName = eventName.getText().toString();

        TextView eventCount = findViewById(R.id.editTextEventCount);
        String eventCountString = eventCount.getText().toString();
        int eCount = Integer.parseInt(eventCountString);

        isActive = findViewById(R.id.switchIsActive1);
        boolean isActiveBool = isActive.isChecked();

        saveDataToSharedPreferences(eID, eName, eCount, isActiveBool);

        String message = "Category saved successfully:" + result;

        // display toast message with makeText method
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

    }
    public void saveDataToSharedPreferences(String eventID, String eventName, int eventCount, boolean eventIsActive) {
        SharedPreferences sharedPreferences = getSharedPreferences("saveFile", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Event ID", eventID);
        editor.putString("Event Name", eventName);
        editor.putInt("Event Count", eventCount);
        editor.putBoolean("Event Active", eventIsActive);

        editor.apply();

    }

    class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);

            if (msg == null || msg.isEmpty()) {
                // Handle empty or null message
                Toast.makeText(context, "Invalid message, not sure how to interpret that!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if ";" is used as the delimiter
            int delimiterCount = msg.split(";").length - 1;
            if (delimiterCount != 2) {
                // Handle incorrect delimiter
                Toast.makeText(context, "Invalid message: Incorrect delimiter used", Toast.LENGTH_SHORT).show();
                return;
            }


            // Check if the message starts with "category:" to ensure it's the correct format
            if (msg.startsWith("category:")) {
                // Remove "category:" prefix
                msg = msg.substring("category:".length());

                // Split the message using ";" delimiter
                String[] parts = msg.split(";");

                // Ensure we have at least 3 parts (name, count, isActive)
                if (parts.length == 3) {
                    String categoryName = parts[0];
                    String categoryCount = parts[1];
                    String categoryIsActive = parts[2];

                    // Parse count to int
                    int categoryCountInt;
                    //check for integer input
                    try {
                        categoryCountInt = Integer.parseInt(categoryCount);
                        if (categoryCountInt <= 0) {
                            Toast.makeText(context, "Invalid message: Count must be a positive integer", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Invalid message: Incorrect price format", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // check for correct boolean input
                    if (!categoryIsActive.equalsIgnoreCase("TRUE") && !categoryIsActive.equalsIgnoreCase("FALSE")) {
                        Toast.makeText(context, "Invalid SMS format: isActive should be either 'TRUE' or 'FALSE'", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    // Update UI elements
                    etEventName.setText(categoryName);
                    etEventCount.setText(String.valueOf(categoryCountInt));
                    etEventActive.setChecked(categoryIsActive.equalsIgnoreCase("TRUE"));

                } else {
                    // Handle incorrect format
                    Toast.makeText(context, "Invalid SMS format", Toast.LENGTH_SHORT).show();
                }

            } else {
                // Handle incorrect format
                Toast.makeText(context, "Invalid SMS format", Toast.LENGTH_SHORT).show();
            }
        }
    }

}