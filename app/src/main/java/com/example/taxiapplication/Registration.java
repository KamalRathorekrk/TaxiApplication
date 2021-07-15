package com.example.taxiapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taxiapplication.model.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    private EditText edtName,edtEmail,edtPassword,edtPhone;
    private Button btnRegi;

    private FirebaseDatabase database;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edtName=(EditText) findViewById(R.id.edtName);
        edtEmail=(EditText) findViewById(R.id.edtEmail);
        edtPassword=(EditText) findViewById(R.id.edtPassword);
        edtPhone=(EditText) findViewById(R.id.edtPhone);
        btnRegi=(Button) findViewById(R.id.btnRegistration);
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("User");

        btnRegi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrernewUser();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser=mAuth.getCurrentUser();
    }
    private void registrernewUser(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");

        String name=edtName.getText().toString();
        String phone=edtPhone.getText().toString();
        String email=edtEmail.getText().toString();
        String password=edtPassword.getText().toString();

        UserHelper uhelper=new UserHelper(name,email,phone,password);
//        myRef.child(mAuth.getUid()).setValue(uhelper);
        if(name.length()>0&&password.length()>0&&email.length()>0&&password.length()>0){


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        myRef.child(mAuth.getUid()).setValue(uhelper);
                                        Toast.makeText(Registration.this, "Registration Succesfull", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Registration.this,HomeScreen.class));
                                    }else{
                                        Toast.makeText(Registration.this, "Registration Fail", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    ).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Registration.this, "Authentication FAil "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(Registration.this,"Field Should not be empty",Toast.LENGTH_SHORT).show();
        }

    }
}