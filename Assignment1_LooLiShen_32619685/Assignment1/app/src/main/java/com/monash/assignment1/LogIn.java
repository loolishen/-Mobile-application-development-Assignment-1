package com.monash.assignment1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //retrieve the last saved username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("saveFile", MODE_PRIVATE);
        String lastSavedUsername = sharedPreferences.getString("Name", "");

        //prefill the username field with the last saved username
        TextView tvName = findViewById(R.id.editTextNameLogin2);
        tvName.setText(lastSavedUsername);
    }


    public void onButtonLoginClick(View view){

        SharedPreferences sharedPreferences =getSharedPreferences("saveFile", MODE_PRIVATE);

        String nameRestored = sharedPreferences.getString("Name", "");
        String passwordRestored = sharedPreferences.getString("Password", "");

        //this is useless just keep it just in case
        String passwordConfirmRestored = sharedPreferences.getString("Confirm Password", "Blank");

        TextView tvName = findViewById(R.id.editTextNameLogin2);
        TextView tvPassword = findViewById(R.id.editTextPasswordLogin2);

        String loginName = tvName.getText().toString();
        String loginPassword = tvPassword.getText().toString();

        if ((loginName.equals(nameRestored) && loginPassword.equals(passwordRestored)) && (!loginName.isEmpty() && !loginPassword.isEmpty())){
            String message = "You have successfully log in!";

            // display toast message with makeText method
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            //add Intent here to go onto next activity
            Intent intent = new Intent(this, EventTransfer.class);
            startActivity(intent);
        }
        else{
            String message = "Incorrect Name or Password!";

            // display toast message with makeText method
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onButtonRegisterClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}