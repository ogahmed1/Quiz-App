package com.example.project3_amoham51;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class onlineQuiz extends AppCompatActivity {

    Button returnOn,add;

    ListView lv;
    String username;
    ArrayList<String> names;
    ArrayAdapter<String> adapter;
    Quiz q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_quiz);
        names=new ArrayList<>();
        lv=findViewById(R.id.lv);
        returnOn=findViewById(R.id.returnOn);
        add=findViewById(R.id.add);
        returnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent=getIntent();
        username=intent.getExtras().getString("username");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //New activity to online quiz
                Intent intent=new Intent(getApplicationContext(),addToOnline.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance()
                .getReference("Global").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot d: snapshot.getChildren()){
                            names.add(d.getKey().toString());
                        }
                        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.eachitem,R.id.list_content,names);
                        lv.setAdapter(adapter);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent=new Intent(getApplicationContext(),takeQuiz.class);
                                intent.putExtra("username","Global");
                                intent.putExtra("quiz name",names.get(i));
                                startActivity(intent);
                                //someActivityResultLauncher.launch(intent);
                            }
                        });
                        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                //First check if the person long clicking is the uploader
                                //FirebaseDatabase.getInstance().getReference().child("uploader")
                                //First search the one that was clicked by name
                                //Then check if it's the uploader clicking it or just another user

                                FirebaseDatabase.getInstance().getReference("Global").child(names.get(i))
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.child("uploader").getValue().toString().equals(username)){
                                                            //Check if they have it first
                                                            deleteFromOnline(i);
                                                        }
                                                        else{
                                                            //Ask if they want it added to theirs
                                                            ArrayList<String> questions= (ArrayList<String>) snapshot.child("questions").getValue();
                                                            ArrayList<ArrayList<String>> answers=(ArrayList<ArrayList<String>>) snapshot.child("answers").getValue();
                                                            ArrayList<String> correctAnswers=(ArrayList<String>) snapshot.child("correctAnswers").getValue();
                                                            ArrayList<String> picRef=(ArrayList<String>) snapshot.child("images").getValue();
                                                            String uploader= (String) snapshot.child("uploader").getValue();
                                                            String name=names.get(i);

                                                            Quiz q=new Quiz(name,questions,correctAnswers,answers,picRef,uploader);

                                                            addToOwn(i,q);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                return true;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    ActivityResultLauncher<Intent>
            someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
// There are no request codes
                        Intent data = result.getData();
                        //This is how we get our values back
                        q=data.getParcelableExtra("Quiz");



                    }
                }
            });

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance()
                .getReference("Global").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot d: snapshot.getChildren()){
                            if(!(names.contains(d.getKey().toString()))){
                                names.add(d.getKey().toString());
                            }
                        }
                        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.eachitem,R.id.list_content,names);
                        lv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void addToOwn(int pos,Quiz q) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(onlineQuiz.this);
        //Less than half
        dialog.setTitle("Would you like to download this quiz?")
                .setMessage("It will be added to your list")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        //finish();
                        FirebaseDatabase.getInstance().getReference(username).orderByChild("name").
                                equalTo(names.get(pos)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            Toast.makeText(getApplicationContext(), "A quiz already has that name!", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            FirebaseDatabase.getInstance().getReference(username)
                                                    .child(names.get(pos)).setValue(q).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getApplicationContext(), "Quiz has been downloaded", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }).show();
    }
    private void deleteFromOnline(int pos) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(onlineQuiz.this);
        //Less than half
        dialog.setTitle("Would you like to delete this quiz?")
                .setMessage("Once deleted, you won't get it back.")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        //finish();
                        FirebaseDatabase.getInstance().getReference("Global")
                                .child(names.get(pos)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //Delete here
                                        snapshot.getRef().removeValue();
                                        names.remove(pos);
                                        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.eachitem,R.id.list_content,names);
                                        lv.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }).show();
    }
}