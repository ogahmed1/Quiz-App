package com.example.project3_amoham51;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class takeQuiz extends AppCompatActivity {


    ArrayList<String> questions,correctAnswers;
    ArrayList<ArrayList<String>> answers;
    ArrayList<String> picRef;
    String name,username;

    ArrayList<Integer> btnshffl;

    ArrayList<Uri> pics;


    ImageView img;
    TextView qstn;
    Button ca,wa1,wa2,wa3,next;

    int correct,num;

    Quiz tempQ;

    String uploader;

    //Score for current question
    //This is necessary because a person
    int score;

    //Score for overall quiz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        num=0;
        correct=0;
        score=0;
        pics=new ArrayList<>();

        btnshffl=new ArrayList<>();

        //shuffleBttn();

        img=findViewById(R.id.IV);
        qstn=findViewById(R.id.question);
        ca=findViewById(R.id.correctAnswer);
        wa1=findViewById(R.id.wa1);
        wa2=findViewById(R.id.wa2);
        wa3=findViewById(R.id.wa3);
        next=findViewById(R.id.next);

        //First thing you want to do is retrieve everything
        name= getIntent().getExtras().getString("quiz name");
        username=getIntent().getExtras().getString("username");
        //.orderByChild("questions")
        //Because this same code will be used for when a global quiz is clicked
        //Use this area here to check if a var is 0 or 1
        //To denote where the user is coming from.
        //The difference from this is which database the quiz is being picked from
        //New idea: set username as "GLOBAL" to differentiate
        FirebaseDatabase.getInstance().getReference(username).child(name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            questions=(ArrayList<String>) dataSnapshot.child("questions").getValue();
                            answers=(ArrayList<ArrayList<String>>) dataSnapshot.child("answers").getValue();
                            correctAnswers=(ArrayList<String>) dataSnapshot.child("correctAnswers").getValue();
                            picRef=(ArrayList<String>) dataSnapshot.child("images").getValue();
                            uploader= (String) dataSnapshot.child("uploader").getValue();
                            shuffleQstn();
                            shuffleBttn();

                            ca.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Change color
                                    ca.setBackgroundColor(getResources().getColor(R.color.green));
                                    //#50ff00
                                    //MAke the next button visible
                                    next.setVisibility(View.VISIBLE);

                                    if(correct==0){
                                        score++;
                                        correct=1;
                                    }
                                }
                            });
                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    next.setVisibility(View.INVISIBLE);
                                    correct=0;
                                    shuffleBttn();
                                    ca.setBackgroundColor(getResources().getColor(R.color.peach));
                                    wa1.setBackgroundColor(getResources().getColor(R.color.peach));
                                    wa2.setBackgroundColor(getResources().getColor(R.color.peach));
                                    wa3.setBackgroundColor(getResources().getColor(R.color.peach));

                                    //First check if it's the end
                                    //If it is, have an alertview congratulating them on finishing

                                    num++;

                                    //Design flaw, if someone uses the same question twice, the quiz ends prematurely
//                                    if(qstn.getText().toString().equals(tempQ.questions.get((tempQ.questions.size()-1)))){
//                                        //It's the end, show the alert view
//                                        System.out.println("The end");
//                                        alertView();
//
//                                    }
                                    //How else can we check if it's the end
                                    if((num)==tempQ.questions.size()){
                                        //It's the end, show the alert view
                                        alertView();

                                    }
                                    else{
                                        qstn.setText(tempQ.questions.get(num));
                                        ca.setText(tempQ.correctAnswers.get(num));
                                        ArrayList<String> wAns=returnWAnswers(tempQ.answers.get(num),tempQ.correctAnswers.get(num));
                                        wa1.setText(wAns.get(0));
                                        wa2.setText(wAns.get(1));
                                        wa3.setText(wAns.get(2));
                                        if(!(tempQ.images.get(num).equals("n"))){
                                            img.setVisibility(View.VISIBLE);
                                            Glide.with(getApplicationContext())
                                                    .load(FirebaseStorage.getInstance()
                                                            .getReference(uploader)
                                                            .child(tempQ.images.get(num)))
                                                    .into(img);
                                        }else{
                                            img.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                }
                            });
                            wa1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onWrong(view);
                                }
                            });
                            wa2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onWrong(view);
                                }
                            });
                            wa3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    onWrong(view);
                                }
                            });


                            qstn.setText(tempQ.questions.get(num));
                            ca.setText(tempQ.correctAnswers.get(num));
                            ArrayList<String> wAns=returnWAnswers(tempQ.answers.get(num),tempQ.correctAnswers.get(num));
                            wa1.setText(wAns.get(0));
                            wa2.setText(wAns.get(1));
                            wa3.setText(wAns.get(2));
                            if(!(tempQ.images.get(num).equals("n"))){
                                Glide.with(getApplicationContext())
                                        .load(FirebaseStorage.getInstance()
                                                .getReference(uploader)
                                                .child(tempQ.images.get(num)))
                                        .into(img);
                                img.setVisibility(View.VISIBLE);
                            }else{
                                img.setVisibility(View.INVISIBLE);
                            }

//                            ArrayList<Uri> temp=new ArrayList<>();
//                            String temp2=FirebaseStorage.getInstance()
//                                    .getReference(username)
//                                    .child(picRef.get(2)).getDownloadUrl().toString();
//                            for(int j=0;j<picRef.size();j++){
//
//                            }
                            //temp.add(FirebaseStorage.getInstance()
                                    //.getReference(username).);
//                            System.out.println("The ref is "+picRef.get(0));
//                            Glide.with(getApplicationContext())
//                                    .load(FirebaseStorage.getInstance()
//                                            .getReference(username)
//                                            .child(picRef.get(2)))
//                                    .into(img);


                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public ArrayList<String> returnWAnswers(ArrayList<String> wrong,String correct){

        ArrayList<String> wAns=new ArrayList<>();
        for(int i=0;i<wrong.size();i++){
            if(!(wrong.get(i).equals(correct))){
                wAns.add(wrong.get(i));
            }
        }

        return wAns;
    }
    public void onStart() {

        super.onStart();


        //Shuffle questions
        //Shuffle buttons
//        shuffleQstn();
//        shuffleBttn();

        //If it's having trouble working, it maybe because

        //And that's it.
    }

    public void onWrong(View view){
        view.setBackgroundColor(getResources().getColor(R.color.red));
        if(correct==0){
            correct=-1;
        }
    }

    public void shuffleQstn(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmss");;
        LocalDateTime time= LocalDateTime.now();
        String s=dtf.format(time);
        int seed = Integer.parseInt(s);
        Random rand=new Random(seed);
        //All arraylists have to shuffled along with the questions

        //questions
        //answers
        //correctAnswers
        //picRef

        int len=questions.size();
        int r=rand.nextInt(len);
        //using the length we know when to stop the loop

        ArrayList<Integer> shffl=new ArrayList<>();

        while(shffl.size()!=len){
            r=rand.nextInt(len);
            if(!shffl.contains(r)){
                shffl.add(r);
            }
        }

        //Now the shuffling begins
        //[0,1,2,3,4,5,6,7,8,9]

        //questions
        //answers
        //correctAnswers
        //picRef
        Quiz q;

        ArrayList<String> tempQstn=new ArrayList<>();
        ArrayList<String> tempPicRef=new ArrayList<>();
        ArrayList<String> tempCans=new ArrayList<>();
        ArrayList<ArrayList<String>> tempAns=new ArrayList<>();


        int a=0;
        int b=0;
        for(int i=0;i<questions.size();i++){
            a=shffl.get(i);

            tempQstn.add(questions.get(a));
            tempPicRef.add(picRef.get(a));
            tempCans.add(correctAnswers.get(a));
            tempAns.add(answers.get(a));


        }
        tempQ=new Quiz(username,tempQstn,tempCans,tempAns,tempPicRef,"N/A");
        //Or simply use shuffle array list as order of questions
    }

    public void shuffleBttn(){
        //Start by getting an array of random numbers 1-4
        Button arr[]={ca,wa1,wa2,wa3};
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmmss");;
        LocalDateTime time= LocalDateTime.now();
        String s=dtf.format(time);
        int seed = Integer.parseInt(s);
        Random rand=new Random(seed);
        int r=rand.nextInt(4);

        while(btnshffl.size()!=4){
            r=rand.nextInt(4);
            if(!btnshffl.contains(r)){
                btnshffl.add(r);
            }
        }

        //Now shuffle the buttons

        //Two vars for what should be shuffled

        int a=0;
        int b=0;

        for(int i =0;i<3;i++){
            a=btnshffl.get(i);
            b=btnshffl.get(i+1);
            float tempX1=arr[a].getX();
            float tempY1=arr[a].getY();
            float tempX2=arr[b].getX();
            float tempY2=arr[b].getY();

            arr[a].setX(tempX2);
            arr[a].setY(tempY2);
            arr[b].setX(tempX1);
            arr[b].setY(tempY1);
        }

    }
    private void alertView() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(takeQuiz.this);
        if(questions.size()/2>=score){
            //Less than half
            dialog.setTitle("Whoops!")
                    .setMessage("You scored a "+ score +"/"+questions.size()+". Keep practicing!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            finish();
//                        Intent intent=new Intent(getApplicationContext(),userProfile.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                            //Apparently using finish to end the activity logs out every user;
//                        finishAffinity();
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }).show();
        }
        else {
            dialog.setTitle("Congratulations!")
                    .setMessage("You scored a " + score + "/" + questions.size() + ". Good job!")
                    .setPositiveButton("Thanks!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            finish();
//                        Intent intent=new Intent(getApplicationContext(),userProfile.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                            //Apparently using finish to end the activity logs out every user;
//                        finishAffinity();
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }).show();
        }
    }

}