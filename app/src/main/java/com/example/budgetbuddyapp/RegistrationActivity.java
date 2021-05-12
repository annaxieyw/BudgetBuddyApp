package com.example.budgetbuddyapp;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends Activity {

    private EditText edtEmail, edtPassword;
    private Button btnRegister, btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // initialising all views through id defined above
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Set on Click Listener on Registration button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        // Set on Click Listener on Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void writeUser(String email) {
        String userId = mAuth.getUid();
        mDatabase.getReference("users/" + userId + "/email").setValue(email);
    }

    private void registerNewUser() {
        // Take the value of two edit texts in Strings
        String email, password;
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.error_email, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.error_password, Toast.LENGTH_LONG).show();
            return;
        }

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            writeUser(email);

                            Toast.makeText(getApplicationContext(), R.string.registration_success, Toast.LENGTH_LONG).show();

                            // if the user created intent to login activity
                            Intent intent = new Intent(RegistrationActivity.this, RegistrationPreferencesActivity.class);
                            startActivity(intent);
                        } else {
                            // Registration failed
                            Toast.makeText(getApplicationContext(), R.string.registration_fail, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

