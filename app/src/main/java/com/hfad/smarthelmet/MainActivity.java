package com.hfad.smarthelmet;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    TextView textView;
    Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Set up login button listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLoginPage = new Intent(MainActivity.this, login_page.class);
                startActivity(toLoginPage);
            }
        });
        // Set up SignUp button listeners
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignupPage = new Intent(MainActivity.this, signup_page.class);
                startActivity(toSignupPage);
            }
        });
    }
}
