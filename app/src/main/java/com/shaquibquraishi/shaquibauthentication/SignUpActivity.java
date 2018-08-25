package com.shaquibquraishi.shaquibauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailEditText;
    EditText passwordEditText;
    TextView logInTextView;
    Button signUpButton;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    //#FF7043#9e9e9e

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==signUpButton.getId()){
            //Toast.makeText(this,"SignUp Successful",Toast.LENGTH_SHORT).show();
            String email=emailEditText.getText().toString().trim();
            String password= passwordEditText.getText().toString().trim();
            if(email.isEmpty()){
                emailEditText.setError("Email is required");
                emailEditText.requestFocus();
                return;
                }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailEditText.setError("Please enter a valid email");
                emailEditText.requestFocus();
                return;

            }
            if(password.isEmpty()){
                passwordEditText.setError("Password is required");
                passwordEditText.requestFocus();
                return;
            }

            if(password.length()<6){
                passwordEditText.setError("Minimum length of password id 6");
                passwordEditText.requestFocus();
                return;

            }
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                //Toast.makeText(SignUpActivity.this,"User Registered",Toast.LENGTH_SHORT).show();
                                FirebaseUser user=mAuth.getCurrentUser();
                                if(user!=null){
                                    finish();
                                    Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("info", "createUserWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {

                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user.
                                Log.i("info" ,"createUserWithEmail:failure"+ task.getException());
                                if(task.getException()instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(SignUpActivity.this, "You are already registered",Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                               // Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                       // Toast.LENGTH_SHORT).show();
                               // updateUI(null);
                            }

                            // ...
                        }
                    });



        }else if(id==logInTextView.getId()){
            //Toast.makeText(this,"LogInText Successful",Toast.LENGTH_SHORT).show();
            finish();
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);


        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailEditText=findViewById(R.id.editTextEmail);
        passwordEditText =findViewById(R.id.editTextPassword);
        logInTextView=findViewById(R.id.textViewLogIn);
        signUpButton=findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(this);
        logInTextView.setOnClickListener(this);
        progressBar=findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }
}
