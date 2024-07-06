package com.hfad.smarthelmet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class signup_page extends AppCompatActivity {
    private static final String TAG = "signup_page";

    EditText editTextName, editTextAge, editTextBloodType, editTextContactPerson, editTextContactNumber,
            editTextMedicalCondition, editTextUsername, editTextPassword;
    RadioGroup radioGroupSex;
    Button buttonSubmit;
    DatabaseHelper userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        userDatabase = new DatabaseHelper(this);

        // Initialize UI elements
        editTextName = findViewById(R.id.textName);
        editTextAge = findViewById(R.id.textAge);
        editTextBloodType = findViewById(R.id.textBloodType);
        editTextContactPerson = findViewById(R.id.textEmergencyContactPerson);
        editTextContactNumber = findViewById(R.id.textEmergencyContactNumber);
        editTextMedicalCondition = findViewById(R.id.textKnownMedicalCondtion);
        editTextUsername = findViewById(R.id.textUsername);
        editTextPassword = findViewById(R.id.textPassword);
        radioGroupSex = findViewById(R.id.radioGroupSex);
        buttonSubmit = findViewById(R.id.fingerprintButton);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    // Get input from EditText fields
                    String name = editTextName.getText().toString().trim();
                    String ageString = editTextAge.getText().toString().trim();
                    String bloodType = editTextBloodType.getText().toString().trim();
                    String contactPerson = editTextContactPerson.getText().toString().trim();
                    String contactNumber = editTextContactNumber.getText().toString().trim();
                    String medicalCondition = editTextMedicalCondition.getText().toString().trim();
                    String email = editTextUsername.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    // Log the email to verify it is being captured
                    Log.d(TAG, "Email entered: " + email);

                    // Retrieve the selected sex value
                    int selectedId = radioGroupSex.getCheckedRadioButtonId();
                    String sex = null;
                    if (selectedId == R.id.radioMale) {
                        sex = "male";
                    } else if (selectedId == R.id.radioFemale) {
                        sex = "female";
                    }

                    if (userDatabase.isEmailExists(email)) {
                        Toast.makeText(signup_page.this, "Username already exists, please choose another", Toast.LENGTH_SHORT).show();
                    } else if (!userDatabase.isEmailExists((email))) {
                        // Convert age to integer
                        int age = Integer.parseInt(ageString);
                        // Insert data into the database
                        boolean isInserted = userDatabase.addUser(new User(name, age, bloodType, sex, contactPerson, contactNumber, medicalCondition, email, password));
                        if (isInserted) {
                            Toast.makeText(signup_page.this, "User created successfully! Please Login ", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    Intent toBiometrics = new Intent(signup_page.this, login_page.class);
                                    startActivity(toBiometrics);
                                }
                            }, Toast.LENGTH_SHORT);
                        } else {
                            Toast.makeText(signup_page.this, "User addition failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    // This method is used to validate input given by the user
    public boolean validate() {
        boolean valid = true;

        // Get values from EditText fields
        String name = editTextName.getText().toString().trim();
        String ageString = editTextAge.getText().toString().trim();
        String bloodType = editTextBloodType.getText().toString().trim();
        String contactPerson = editTextContactPerson.getText().toString().trim();
        String contactNumber = editTextContactNumber.getText().toString().trim();
        String medicalCondition = editTextMedicalCondition.getText().toString().trim();
        String email = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate name
        if (name.isEmpty()) {
            valid = false;
            editTextName.setError("Please enter your name");
        } else {
            editTextName.setError(null);
        }

        // Validate age
        if (ageString.isEmpty()) {
            valid = false;
            editTextAge.setError("Please enter your age");
        } else if (!ageString.matches("\\d+")) {
            valid = false;
            editTextAge.setError("Please enter a valid age");
        } else {
            editTextAge.setError(null);
        }

        // Validate blood type
        if (bloodType.isEmpty()) {
            valid = false;
            editTextBloodType.setError("Please enter your blood type");
        } else {
            editTextBloodType.setError(null);
        }

        // Validate contact person
        if (contactPerson.isEmpty()) {
            valid = false;
            editTextContactPerson.setError("Please enter a contact person");
        } else {
            editTextContactPerson.setError(null);
        }

        // Validate contact number
        if (contactNumber.isEmpty()) {
            valid = false;
            editTextContactNumber.setError("Please enter a contact number");
        } else {
            editTextContactNumber.setError(null);
        }

        // Validate medical condition
        if (medicalCondition.isEmpty()) {
            valid = false;
            editTextMedicalCondition.setError("Please enter your medical condition");
        } else {
            editTextMedicalCondition.setError(null);
        }

        // Validate email
        if (email.isEmpty()) {
            valid = false;
            editTextUsername.setError("Please enter a email");
        }

        // Validate password
        if (password.isEmpty()) {
            valid = false;
            editTextPassword.setError("Please enter a password");
        } else if (password.length() <= 8) {
            valid = false;
            editTextPassword.setError("Password is too short");
        } else {
            editTextPassword.setError(null);
        }
        return valid;
    }
}
