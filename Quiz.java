package com.example.project3_amoham51;

import java.util.ArrayList;

public class Quiz {
    //public String name,questions,correctAnswers,answers;

    public String name;
    public ArrayList<String> questions,correctAnswers;
    //To save space I can maybe just use
    //A regular arraylist and separate the answers with a comma
    public ArrayList<ArrayList<String>> answers;

    //Arraylist holding the image links that are provided

    public ArrayList<String> images;

    //This is used to check who uploaded the quiz to the global hub
    public String uploader;


    //Will also follow guide for uploading images

//    Quiz(String name,String questions,String correctAnswers,String answers){
//        this.name=name;
//        this.questions=questions;
//        this.correctAnswers=correctAnswers;
//        this.answers=answers;
//    }
    Quiz(String name,ArrayList<String> questions,ArrayList<String> correctAnswers,ArrayList<ArrayList<String>> answers,ArrayList<String> images,String uploader){
        this.name=name;
        this.questions=questions;
        this.correctAnswers=correctAnswers;
        this.answers=answers;
        this.uploader=uploader;
        this.images=images;
    }

}
