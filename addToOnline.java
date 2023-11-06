package com.example.project3_amoham51;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class addToOnline extends AppCompatActivity {

    String username;

    ArrayList<String> names;

    ArrayAdapter<String> adapter;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_online);

        names=new ArrayList<>();

        username=getIntent().getExtras().getString("username");
        lv=findViewById(R.id.lv);
        //Now go to that database and get the
        FirebaseDatabase.getInstance().getReference(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot d: snapshot.getChildren()){
                            names.add(d.getKey().toString());
                            System.out.println(d.getKey().toString() +" Should be Testing");
                        }
                        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.eachitem,R.id.list_content,names);
                        lv.setAdapter(adapter);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                //Create a new activity allowing the user to take the quiz

                                //But instead I will be returning a quiz

                                FirebaseDatabase.getInstance().getReference(username).child(names.get(i))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Intent intent= new Intent();
                                        setResult(RESULT_OK,intent);
                                        if(snapshot.exists()){
                                            ArrayList<String> questions= (ArrayList<String>) snapshot.child("questions").getValue();
                                            ArrayList<ArrayList<String>> answers=(ArrayList<ArrayList<String>>) snapshot.child("answers").getValue();
                                            ArrayList<String> correctAnswers=(ArrayList<String>) snapshot.child("correctAnswers").getValue();
                                            ArrayList<String> picRef=(ArrayList<String>) snapshot.child("images").getValue();
                                            String uploader= (String) snapshot.child("uploader").getValue();
                                            String name=names.get(i);
                                            Quiz q=new Quiz(name,questions,correctAnswers,answers,picRef,uploader);
                                            FirebaseDatabase.getInstance().getReference("Global").orderByChild("name").
                                                    equalTo(q.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.exists()) {
                                                                Toast.makeText(getApplicationContext(), "A quiz already has that name!", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
                                                            else{
                                                                FirebaseDatabase.getInstance().getReference("Global").child(q.name).setValue(q)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(getApplicationContext(), "Congrats on uploading!", Toast.LENGTH_SHORT).show();
                                                                                    //Send them back to main screen
                                                                                    finish();
                                                                                } else {
                                                                                    Toast.makeText(getApplicationContext(), "Uploading failed!", Toast.LENGTH_SHORT).show();
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
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}