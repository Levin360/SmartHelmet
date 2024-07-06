package com.hfad.smarthelmet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login_page extends AppCompatActivity {

    EditText email_edit, password_edit;
    Button login_button;
    TextView clickable_text;
    DatabaseHelper userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initializing the Elements
        email_edit = (EditText) findViewById(R.id.editTextEmail);
        password_edit = (EditText) findViewById(R.id.editTextPassword);
        userDatabase = new DatabaseHelper(this);

        // Go to Signup Page
        clickable_text = (TextView) findViewById(R.id.signup_click);
        clickable_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignup = new Intent(login_page.this, signup_page.class);
                startActivity(goToSignup);
            }
        });

        // For Logging In
        login_button = findViewById(R.id.buttonLogin);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {

                    //Get values from EditText fields
                    String email = email_edit.getText().toString();
                    String password = password_edit.getText().toString();

                    //Authenticate User
                    User currentUser = userDatabase.authenticate(new User(null, null, 0, null, null, null, null, null, email, password));

                    //Check Authentication is successful or not
                    if (currentUser != null) {
                        Intent goToDash = new Intent(login_page.this, dashboard_page.class);
                        goToDash.putExtra("Email", currentUser.email);      // Display the Email of the User
                        goToDash.putExtra("Name", currentUser.name);        // Display the Name of the Current User
                        startActivity(goToDash);
                        Toast.makeText(login_page.this, " Login Successful!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(login_page.this, " Failed to Logged in!", Toast.LENGTH_LONG).show();
                    }


                }
            }
        });
    }
        //This method is used to validate input given by user
        public boolean validate() {
            boolean valid = false;

            //Get values from EditText fields
            String email = email_edit.getText().toString();
            String pass = password_edit.getText().toString();

            //Handling validation
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                valid = false;
                email_edit.setError("Please enter valid Email");
            } else {
                valid = true;
                email_edit.setError(null);

            }

            //Handling Password
            if (pass.isEmpty()) {
                valid = false;
                password_edit.setError("Please enter Valid Password");
            }
            return valid;
        }
    }
