package com.example.project3_amoham51;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class userProfile extends AppCompatActivity {

    Button logout,add,online;

    FirebaseUser user;
    DatabaseReference ref;

    ArrayList<String> names;
//    ArrayList<String> answers;
//    ArrayList<String> correctAnswers;


    ListView lv;

    ArrayAdapter<String> adapter;

    String username,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        names=new ArrayList<>();
        //adapter=
        //answers=new ArrayList<>();
        //correctAnswers=new ArrayList<>();


        online=findViewById(R.id.online);
        lv=findViewById(R.id.list);
        user=FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference("Users");
        uid=user.getUid();

        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CreateQuiz.class));
            }
        });
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertView();
            }
        });
        online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open new activity
                Intent intent=new Intent(getApplicationContext(),onlineQuiz.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });





        //FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
        //                                                                        .getCurrentUser().getUid()).setValue(u)

        //Now to create a listview
        //First download all the names of the quizzes
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        username=snapshot.getValue().toString();
                        FirebaseDatabase.getInstance()
                                .getReference(username)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot d: snapshot.getChildren()){
                                            names.add(d.getKey().toString());
                                        }
                                        //                                        adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,names);
                                        //Test
                                        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.eachitem,R.id.list_content,names);
                                        lv.setAdapter(adapter);

                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                //Create a new activity allowing the user to take the quiz

                                                Intent intent=new Intent(getApplicationContext(),takeQuiz.class);
                                                intent.putExtra("username",username);
                                                intent.putExtra("quiz name",names.get(i));
                                                startActivity(intent);


                                            }
                                        });
                                        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                            @Override
                                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                //Alertview
                                                deleteQuiz(i);
                                                return true;
                                            }
                                        });
//                                        online.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                //open new activity
//                                                Intent intent=new Intent(getApplicationContext(),onlineQuiz.class);
//                                                intent.putExtra("username",username);
//                                            }
//                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void alertView() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(userProfile.this);
        dialog.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                    }})
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        //Apparently using finish to end the activity logs out every user;
//                        finishAffinity();
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                }).show();
    }
    public void onResume() {

        super.onResume();


        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        username=snapshot.getValue().toString();
                        FirebaseDatabase.getInstance()
                                .getReference(username)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot d: snapshot.getChildren()){
                                            if(!(names.contains(d.getKey().toString()))){
                                                names.add(d.getKey().toString());
                                            }
                                        }
                                        //                                        adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,names);
                                        //Test
                                        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.eachitem,R.id.list_content,names);
                                        lv.setAdapter(adapter);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        FirebaseDatabase.getInstance().getReference("test1").orderByChild("questions")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.exists()){
//                    ArrayList<ArrayList<String>> test2;
//                    for(DataSnapshot d:dataSnapshot.getChildren()){
//                        System.out.println("We are back in the user profile");
//                        //This is for the quizzes
//                        ArrayList<String> test = (ArrayList<String>) d.child("questions").getValue();
//                        test2 = (ArrayList<ArrayList<String>>) d.child("answers").getValue();
//                        d.child("questions").getValue().toString();
////                        questions.add(test);
//
//                        //Check how to get image from storage
//                        //Uri
//
//
//                        String a =test.get(0);
//
//                        System.out.println("The questions are: "+a);
//                        //questions.clear();
//                        for(int i=0;i<test2.size();i++){
//                            String b=test2.get(i).get(0);
//                            String c=test2.get(i).get(1);
//                            String f=test2.get(i).get(2);
//                            String e=test2.get(i).get(3);
//
//                            System.out.println("--------------------");
//                            System.out.println("1: "+b);
//                            System.out.println("2: "+c);
//                            System.out.println("3: "+f);
//                            System.out.println("4: "+e);
//                            System.out.println("--------------------");
//                        }
//                    }
//
//                }
//                else{
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
    private void deleteQuiz(int pos) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(userProfile.this);
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
                        FirebaseDatabase.getInstance().getReference(username)
                                .child(names.get(pos)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //Delete here
                                        snapshot.getRef().removeValue();
                                        String n=names.get(pos);
                                        ArrayList<String> pRef= (ArrayList<String>) snapshot.child("images").getValue();
                                        names.remove(pos);
//                                        adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,names);
                                        //Test
                                        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.eachitem,R.id.list_content,names);
                                        lv.setAdapter(adapter);
                                        //No point in deleting images, because someone may have them saved(the quiz) and the pictures won't appear
                                        //If it's deleted
//                                        for(int i=0;i<pRef.size();i++){
//                                            FirebaseStorage.getInstance().getReference(username).child(pRef.get(i)).delete();
//                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }).show();
    }
}