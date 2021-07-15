package com.example.taxiapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;

public class Loginpage extends AppCompatActivity {

    EditText edtuName,edtuPassword;
    TextView forgetpassword;
    Button btnlogin;
    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    String name,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser=mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        edtuName=(EditText) findViewById(R.id.edsEmail);
        edtuPassword=(EditText) findViewById(R.id.edsPassword);
        btnlogin=(Button)findViewById(R.id.btnlogin);
        forgetpassword=(TextView)findViewById(R.id.forgetpassword);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Loginpage.this,ForgetPassword.class));
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//                                            startActivity(new Intent(Loginpage.this,HomeScreen.class));
                                            loginFun();
                                        }
                                    }
        );

    }
    private void loginFun(){

        String email = edtuName.getText().toString().trim();
        String password=edtuPassword.getText().toString().trim();
        if((email.length()>0) && (password.length()>0)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Loginpage.this, "Registration Succesfull", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Loginpage.this, HomeScreen.class));
                            } else {
                                Toast.makeText(Loginpage.this, "Registration Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Loginpage.this, "Registration Fail " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(Loginpage.this,"Field Should not be empty",Toast.LENGTH_SHORT).show();
//                edtuName.setBackgroundColor(Integer.parseInt("#FFF30101"));
        }

    }


}