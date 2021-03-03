package com.example.scutapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText, phoneNumberEditText;
    private TextView infectedState;
    private Button registerBTn, btnInfected, btnNone, btnHealthy;
    private TextView loginTextBtn;
    private ProgressBar progressBar;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID, state;
    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnInfected = findViewById(R.id.btnInfected);
        btnNone = findViewById(R.id.btnNone);
        btnHealthy = findViewById(R.id.btnHealthy);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        registerBTn = findViewById(R.id.registerBtn);
        loginTextBtn = findViewById(R.id.loginTextBtn);
        progressBar = findViewById(R.id.progressBar);
        infectedState = findViewById(R.id.infectionState);

        fStore = fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        buttonLogic();


        registerBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = fullNameEditText.getText().toString();
                final String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                final String phone = phoneNumberEditText.getText().toString();

                if (fullName.isEmpty()) {
                    fullNameEditText.setError("Full Name is required!");
                } else if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email is required!");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required!");
                } else if (phone.isEmpty()) {
                    phoneNumberEditText.setError("Telephone number is required!");
                } else if (password.length() < 6) {
                    passwordEditText.setError("Password must be >= 6 characters!");
                } else if (state == null) {
                    infectedState.setError("Set the health situation!");
                } else {
                    // register user into database
                    registerUser(email, password, fullName, phone, state);
                }
                progressBar.setVisibility(View.VISIBLE);

            }
        });

        loginTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void registerUser(final String email, String password, final String fullName, final String phone, final String state) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //send verification link
                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Register.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent" + e.getMessage());
                        }
                    });

                    Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                    // insert user in a document

                    userID = fuser.getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", userID);
                    user.put("fullName", fullName);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("state", state);
                    //insert user in the database
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user Profile is created for" + userID);
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }
                    });

                } else {
                    Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void buttonLogic() {
        btnHealthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnHealthy.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnInfected.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                state = "Healthy";
            }
        });

        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                btnHealthy.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnInfected.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                state = "None";
            }
        });

        btnInfected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnInfected.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                btnHealthy.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                state = "Infected";
            }
        });
    }


}
