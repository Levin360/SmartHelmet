package com.hfad.smarthelmet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class dashboard_page extends AppCompatActivity {

    private TextView email_display, username_display;
    private Button logOut;
    private DatabaseHelper userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_page);

        userDatabase = new DatabaseHelper(this);

        logOut =  findViewById(R.id.logout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set a flag to indicate that the user logged out
                Intent toLogout = new Intent(dashboard_page.this, MainActivity.class);
                toLogout.putExtra("LOGOUT_FLAG", true);
                startActivity(toLogout);
                //Finish current Activity
                finish();
                Toast.makeText(dashboard_page.this,"Logout Successful",  Toast.LENGTH_SHORT).show();
            }
        });

        email_display = (TextView) findViewById(R.id.display_email);
        username_display = (TextView) findViewById(R.id.user_name);

        Intent i = this.getIntent();
        String email = i.getStringExtra("Email");
        String name = i.getStringExtra("Name");

        username_display.setText(name);
        email_display.setText(email);

    }
}