package com.shaquibquraishi.shaquibauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailEditText;
    EditText passwordEditText;
    TextView signUpTextView;
    Button logInButton;
    ProgressBar progressBar;
    ConstraintLayout constraintLayoutLogin;
    ImageView imageViewLogin;
    LinearLayout linearLayoutLogin;
    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            finish();
            Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(id==R.id.constraintLayoutLogin||id==R.id.imageViewLogin ||id==R.id.linearLayoutLogin&& inputMethodManager.isActive()) {
            // InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        if(id==logInButton.getId()){
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
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){

                             progressBar.setVisibility(View.GONE);
                             FirebaseUser user=mAuth.getCurrentUser();
                             if(user!=null){
                                 finish();//if you dont want to come back to this activity after login
                                 Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 startActivity(intent);

                             }


                         }else{
                             progressBar.setVisibility(View.GONE);
                             Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                         }
                        }
                    });

        }else if(id==signUpTextView.getId()){
            finish();
            Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditText=findViewById(R.id.editTextEmail);
        passwordEditText =findViewById(R.id.editTextPassword);
        signUpTextView =findViewById(R.id.textViewSignUp);
        logInButton =findViewById(R.id.buttonLogIn);

        progressBar=findViewById(R.id.progressBar);
        constraintLayoutLogin=findViewById(R.id.constraintLayoutLogin);
        imageViewLogin=findViewById(R.id.imageViewLogin);
        linearLayoutLogin=findViewById(R.id.linearLayoutLogin);
        mAuth = FirebaseAuth.getInstance();
        logInButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
        constraintLayoutLogin.setOnClickListener(this);
        imageViewLogin.setOnClickListener(this);
        linearLayoutLogin.setOnClickListener(this);
    }
}
