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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void onButtonLoginClick(View view){
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }

    public void onButtonRegisterClick(View view){
        TextView tvName = findViewById(R.id.editTextNameLogin2);
        TextView tvPassword = findViewById(R.id.editTextPasswordLogin2);
        TextView tvConfirmPassword = findViewById(R.id.editTextPasswordConfirm);

        String name = tvName.getText().toString();
        String password = tvPassword.getText().toString();
        String confirmPassword = tvConfirmPassword.getText().toString();

        if (password.isEmpty() || name.isEmpty()){
            String message = "Your name or password must not be blank!";

            // display toast message with makeText method
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        else if (password.equals(confirmPassword)){
            /* this is for successful registration that would lead to next page
             and also save the information through sharedPreferences*/

            // format message to show that register is a success
            String message = "You have successfully registered!";

            // display toast message with makeText method
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Put my sharedPreferences here later !!!
            saveDataToSharedPreferences(name, password, confirmPassword);

            // Also put intent here to transfer to next page, Log In button is to go back
            // to log in page only not to bring you there after you click on register
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        }
        else {
            String message = "Passwords do not match!";

            // display toast message with makeText method
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public void saveDataToSharedPreferences(String nameData, String passwordData, String confirmPasswordData){
        SharedPreferences sharedPreferences = getSharedPreferences("saveFile", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("Name", nameData);
        editor.putString("Password", passwordData);
        editor.putString("Confirm Password", confirmPasswordData);

        editor.apply();

    }
}