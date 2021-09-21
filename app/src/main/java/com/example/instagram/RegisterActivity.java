package com.example.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, name, email, pwd;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.usernameRedg);
        name = findViewById(R.id.nameRedg);
        email = findViewById(R.id.emailRedg);
        pwd = findViewById(R.id.passwordRedg);
        Button register = findViewById(R.id.registerBtn);
        TextView loginUserText = findViewById(R.id.loginTxt);

        pd = new ProgressDialog(this,ProgressDialog.THEME_HOLO_DARK);
        mAuth = FirebaseAuth.getInstance();

        loginUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameTxt = username.getText().toString();
                String nameTxt = name.getText().toString();
                String emailTxt = email.getText().toString();
                String pwdTxt = pwd.getText().toString();

                if (TextUtils.isEmpty(usernameTxt) || TextUtils.isEmpty(nameTxt) || TextUtils.isEmpty(emailTxt) || TextUtils.isEmpty(pwdTxt))
                    Toast.makeText(RegisterActivity.this, "Credentials Empty", Toast.LENGTH_SHORT).show();
                else if (pwdTxt.length() < 6)
                    Toast.makeText(RegisterActivity.this, "password must be of 6 characters", Toast.LENGTH_SHORT).show();
                else {
                    pd.setMessage("Registering you... Hold on a second...");
                    pd.show();
                    registerUser(usernameTxt, nameTxt, emailTxt, pwdTxt);
                }

            }
        });

    }
    //register funstion
    private void registerUser(String username, String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Username", username);
                    map.put("Name", name);
                    map.put("Email", email);
                    map.put("User ID", mAuth.getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid())
                            .setValue(map).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}