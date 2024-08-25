package com.monash.assignment1;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class AddEvent extends AppCompatActivity {

    EditText etCategoryID1;
    EditText etEventName;
    EditText etTicketsAvailable;
    Switch etIsActive;

    Switch isActive;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEventName =findViewById(R.id.editTextEventName1);
        etCategoryID1 =findViewById(R.id.editTextCategoryID1);
        etTicketsAvailable =findViewById(R.id.editTextTicketAvailable);
        etIsActive =findViewById(R.id.switchIsActive);

        // this is for the SMS
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS
        }, 0);

        AddEvent.MyBroadCastReceiver myBroadCastReceiver = new AddEvent.MyBroadCastReceiver();
        /*
         * Register the broadcast handler with the intent filter that is declared in
         * class SMSReceiver @line 11
         * */
        registerReceiver(myBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER), RECEIVER_EXPORTED);

    }

    public void onSaveEventClick(View view){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder("E");

        Random random = new Random();

        for (int i = 0; i < 2; i++) {
            int randomIndex = random.nextInt(characters.length());
            result.append(characters.charAt(randomIndex));
        }

        result.append("-");

        for (int j = 0; j < 5; j++) {
            int randomDigit = random.nextInt(10);
            result.append(randomDigit);
        }

        // and it inputs in to the plain text after the button is clicked
        TextView eventID = findViewById(R.id.editTextEventID);
        eventID.setText(result.toString());
        String eID = eventID.getText().toString();

        TextView eventName = findViewById(R.id.editTextEventName1);
        String eName = eventName.getText().toString();

        TextView categoryID = findViewById(R.id.editTextCategoryID1);
        String cName = categoryID.getText().toString();

        TextView eventCount = findViewById(R.id.editTextTicketAvailable);
        String eventCountString = eventCount.getText().toString();
        int eAvailable = Integer.parseInt(eventCountString);

        isActive = findViewById(R.id.switchIsActive);
        boolean isActiveBool = isActive.isChecked();

        saveDataToSharedPreferences(eID, eName, cName, eAvailable, isActiveBool);

        String message = "Event saved successfully: " + result + " to " + cName;

        // display toast message with makeText method
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void saveDataToSharedPreferences(String eventID, String eventName, String categoryID1, int eventAvailable, boolean eventIsActive) {
        SharedPreferences sharedPreferences = getSharedPreferences("saveFile2", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Event ID", eventID);
        editor.putString("Event Name", eventName);
        editor.putString("Category ID", categoryID1);
        editor.putInt("Ticket Available", eventAvailable);
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

            // Check if "event:" prefix is present
            if (!msg.startsWith("event:")) {
                // Handle incorrect prefix
                Toast.makeText(context, "Invalid message: Missing 'event:' prefix", Toast.LENGTH_SHORT).show();
                return;
            }

            // Remove the "event:" prefix
            msg = msg.substring("event:".length());
            // Check if ";" is used as the delimiter
            int delimiterCount = msg.split(";").length - 1;
            if (delimiterCount != 3) {
                // Handle incorrect delimiter
                Toast.makeText(context, "Invalid message: Incorrect delimiter/inputs used!", Toast.LENGTH_SHORT).show();
                return;
            }


            // Split the message using ";" delimiter
            String[] parts = msg.split(";");

            // Ensure we have at least 3 parts (name, count, isActive)
            if (parts.length == 4) {
                String eventName = parts[0];
                String categoryID = parts[1];
                String ticketAvailable = parts[2];
                String eventIsActive = parts[3];

                // Parse count to int
                int ticketAvailableInt;
                //check for integer input
                try {
                    ticketAvailableInt = Integer.parseInt(ticketAvailable);
                    if (ticketAvailableInt <= 0) {
                        Toast.makeText(context, "Invalid message: Count must be a positive integer", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid message: Incorrect price format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check for correct boolean input
                if (!eventIsActive.equalsIgnoreCase("TRUE") && !eventIsActive.equalsIgnoreCase("FALSE")) {
                    Toast.makeText(context, "Invalid SMS format: isActive should be either 'TRUE' or 'FALSE'", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Update UI elements
                etEventName.setText(eventName);
                etCategoryID1.setText(categoryID);
                etTicketsAvailable.setText(String.valueOf(ticketAvailableInt));
                etIsActive.setChecked(eventIsActive.equalsIgnoreCase("TRUE"));

            } else {
                // Handle incorrect format
                Toast.makeText(context, "Invalid SMS format", Toast.LENGTH_SHORT).show();
            }

        }
    }


}