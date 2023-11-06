package com.example.project3_amoham51;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class newUser extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText email,pass,user;
    Button b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        mAuth = FirebaseAuth.getInstance();
        user= findViewById(R.id.userET);
        pass=findViewById(R.id.passET);
        email=findViewById(R.id.emailET);
        b=findViewById(R.id.create);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check all vals

                if (user.getText().toString().trim().isEmpty()) {
                    //Make a toast message saying they're missing an input
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())) {
                    Toast.makeText(getApplicationContext(), "Not a valid email!", Toast.LENGTH_SHORT).show();
                }
                if (pass.getText().toString().trim().length() < 6) {
                    Toast.makeText(getApplicationContext(), "Please enter at least 6 characters for your password", Toast.LENGTH_SHORT).show();
                }
                //Before actually creating the account, we need to see if a username or email is unique

                FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").
                        equalTo(user.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    Toast.makeText(getApplicationContext(), "Username already taken!", Toast.LENGTH_SHORT).show();
                                    //return;
                                }
                                else{
                                    mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if (task.isSuccessful()) {
                                                        User u = new User(user.getText().toString().trim(), email.getText().toString().trim());

                                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                                                        .getCurrentUser().getUid()).setValue(u)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(getApplicationContext(), "Congrats on registering!", Toast.LENGTH_SHORT).show();
                                                                            //Send them back to main screen
                                                                            finish();
                                                                        } else {
                                                                            Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Registration failed!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { // ToDo: do something for errors
                                //Could move forward

                            }
                        });

                }

        });
    }
}