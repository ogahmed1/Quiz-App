package com.example.project3_amoham51;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateQuiz extends AppCompatActivity {

    Button next;

    FloatingActionButton imagebtn;
    TextView question,qNameTV,qAmountTV,caTV,waTV, addphoto;

    EditText Qname,Qnum,questionET,correct,w1,w2,w3;

    //Checks if the name and q amount have been entered
    Boolean p=false;

    Quiz quiz;

    String username;
    int questionAmount,count;
    String name;

    //Array list of bitmaps
    ArrayList<String> picRef;
    ArrayList<Uri> images;

    ArrayList<String> questions;
    ArrayList<String> correctAnswers;
    ArrayList<ArrayList<String>> answers;

    Boolean paused=false;

    int j,k=0;

    Uri mImageUri;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        questions=new ArrayList<>();
        correctAnswers=new ArrayList<>();
        answers=new ArrayList<>();

        mImageUri=null;

        picRef=new ArrayList<>();
        images=new ArrayList<>();

        img=findViewById(R.id.img);

        count=0;


        final int[] buff = {0};

        addphoto=findViewById(R.id.photoTV);
        imagebtn=findViewById(R.id.floatingActionButton5);

        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                someActivityResultLauncher.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

            }
        });

        username="";
        next = findViewById(R.id.next);


        question=findViewById(R.id.Question);

        caTV=findViewById(R.id.correctAnswer);
        waTV=findViewById(R.id.wrongAnswer1);
        qNameTV=findViewById(R.id.name);
        qAmountTV=findViewById(R.id.amount);
        Qname=findViewById(R.id.nameET);
        Qnum=findViewById(R.id.amountET);
        questionET=findViewById(R.id.questionET);
        correct=findViewById(R.id.caET);
        w1=findViewById(R.id.wa1ET);
        w2=findViewById(R.id.wa2ET);
        w3=findViewById(R.id.wa3ET);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We need to differenciate if it's asking for answers instead of set up
                if(!p){

                    if(Qname.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Enter a name for the quiz!", Toast.LENGTH_SHORT).show();
                        //You'll want to make sure no other quiz has the same name later
                        return;
                    }
                    if(Qnum.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Enter the number of questions", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int amount= Integer.parseInt(Qnum.getText().toString());
                    if(amount>10||amount<1){
                        Toast.makeText(getApplicationContext(), "1 to 10 questions only!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    name=Qname.getText().toString().trim();
                    questionAmount=amount;

                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                username=snapshot.child("username").getValue().toString();
                                FirebaseDatabase.getInstance().getReference(username).orderByChild("name").
                                        equalTo(name.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {
                                                    Toast.makeText(getApplicationContext(), "A quiz already has that name!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                else{
                                                    addphoto.setVisibility(View.VISIBLE);
                                                    imagebtn.setVisibility(View.VISIBLE);
                                                    qNameTV.setVisibility(View.GONE);
                                                    qAmountTV.setVisibility(View.GONE);
                                                    Qnum.setVisibility(View.GONE);
                                                    Qname.setVisibility(View.GONE);

                                                    p=true;
                                                    waTV.setVisibility(View.VISIBLE);
                                                    caTV.setVisibility(View.VISIBLE);
                                                    question.setVisibility(View.VISIBLE);
                                                    correct.setVisibility(View.VISIBLE);
                                                    questionET.setVisibility(View.VISIBLE);
                                                    w1.setVisibility(View.VISIBLE);
                                                    w2.setVisibility(View.VISIBLE);
                                                    w3.setVisibility(View.VISIBLE);
                                                    question.setText("Enter a question for number "+(count+1));
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
                else{

                    //Check for empty ET
                    if(questionET.getText().toString().isEmpty()||correct.getText().toString().isEmpty()
                            ||w1.getText().toString().isEmpty()||w2.getText().toString().isEmpty()||w3.getText().toString().isEmpty()){
                        //One of them is empty, so put a toast message
                        Toast.makeText(getApplicationContext(), "One of the inputs is missing!", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        if(mImageUri!=null){
                            //It has been given an image
                            //Make it null
                            mImageUri=null;
                        }
                        else{
                            //It does not have an image
                            //Add null to array list

                            images.add(null);
                        }

                        img.setVisibility(View.INVISIBLE);
                        addphoto.setVisibility(View.VISIBLE);
                        imagebtn.setVisibility(View.VISIBLE);

                        questions.add(questionET.getText().toString().trim());
                        answers.add(new ArrayList<>());
                        answers.get(count).add(correct.getText().toString().trim());
                        answers.get(count).add(w1.getText().toString().trim());
                        answers.get(count).add(w2.getText().toString().trim());
                        answers.get(count).add(w3.getText().toString().trim());
                        correctAnswers.add(correct.getText().toString().trim());

                        questionET.setText("");
                        correct.setText("");
                        w1.setText("");
                        w2.setText("");
                        w3.setText("");

                        //Check if count is done
                        count++;
                        question.setText("Enter a question for number "+(count+1));

                        if(!(count<questionAmount)) {
                            //Done
                            Toast.makeText(getApplicationContext(), "Quiz created", Toast.LENGTH_SHORT).show();
                            finish();




                            for (int i = picRef.size(); i < questions.size(); i++) {
                                if(images.get(i)==null){
                                    picRef.add("n");
                                }
                                else{
                                    //P for placeholder
                                    //Or simply put the name of the imageRef
                                    String nameOfRef=System.currentTimeMillis() + "." + returnFE(images.get(i));
                                    StorageReference stoRef = FirebaseStorage.getInstance().getReference(username).
                                            child(nameOfRef);
                                    picRef.add(nameOfRef);
                                    stoRef.putFile(images.get(i));
                                }


                            }
                            Quiz q = new Quiz(name, questions, correctAnswers, answers, picRef, username);

                            //Instead of Users, put in the username of the currentuser, and order it by name


                            FirebaseDatabase.getInstance().getReference(username).child(name).setValue(q)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Congrats on uploading!", Toast.LENGTH_SHORT).show();
                                                //Send them back to main screen
                                                //finish();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Uploading failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }


                    }

                }

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
                        mImageUri=result.getData().getData();

                        //Test out making it null
                        img.setImageURI(mImageUri);

                        images.add(result.getData().getData());

                        img.setVisibility(View.VISIBLE);
                        addphoto.setVisibility(View.INVISIBLE);
                        imagebtn.setVisibility(View.INVISIBLE);

                    }
                }
            });
    public String returnFE(Uri uri){
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}