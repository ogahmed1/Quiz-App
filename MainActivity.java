package com.example.project3_amoham51;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    Button login;
    EditText user,pass;
    TextView reg;

    String email="";

    FirebaseDatabase firebaseDatabase;

    DatabaseReference databaseReference;
    String user1,pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login=findViewById(R.id.login);
        reg=findViewById(R.id.register);
        user=findViewById(R.id.userET);
        pass=findViewById(R.id.passET);

        mAuth=FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.setBackgroundColor(R.color.red);
                //view.setBackgroundColor(getResources().getColor(R.color.red));
                user1="test1";
                pass1="123456";

                if(pass.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(user.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            //The user name is there
                            for(DataSnapshot d:dataSnapshot.getChildren()){
                                //Loop through comparing each username, when it's a hit, get the email as well

                                String iter=d.child("username").getValue().toString();


                                if(iter.equals(user.getText().toString().trim())){
                                    email=d.child("email").getValue().toString();
                                }
                            }

                        }
                        else{
                        }
                        if(!email.equals("")){
                            //Success!
                            //login in now

                            mAuth.signInWithEmailAndPassword(email,pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //New activity showing the logged in screen
                                        startActivity(new Intent(getApplicationContext(),userProfile.class));
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Wrong username and/or password", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Not logging in", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { // ToDo: do something for errors

                    }
                  });
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),newUser.class));
            }
        });

    }
}