package com.shaquibquraishi.shaquibauthentication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    ImageView imageViewCamera;
    EditText editTextDisplayName;
    Button buttonSave;
    Uri uriProfileImage;
    ProgressBar progressBar;
    String profileImageUrl;
    TextView textViewVerified;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    private void saveUserInformation(){
        String displayName=editTextDisplayName.getText().toString().trim();
        if(displayName.isEmpty()){
            editTextDisplayName.setError("Name Required");
            editTextDisplayName.requestFocus();
            return;
        }
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null && profileImageUrl!=null){
            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

    }

    private void showImageChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK &&data!=null&&data.getData()!=null){
            uriProfileImage=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                imageViewCamera.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void uploadImageToFirebase() {
        final StorageReference profileImageRef=FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(uriProfileImage!=null){
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileImageUrl=taskSnapshot.getDownloadUrl().toString();

                }
               })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageViewCamera=findViewById(R.id.imageViewCamera);
        editTextDisplayName=findViewById(R.id.editTextDisplayName);
        buttonSave=findViewById(R.id.buttonSave);
        progressBar=findViewById(R.id.progressBar2);
        textViewVerified=findViewById(R.id.textViewVerified);
        toolbar=findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        setTitle("Your Profile");
        mAuth=FirebaseAuth.getInstance();
        loadUserInformation();
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();

            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();

            }
        });

    }

    private void loadUserInformation() {
        final FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            if(user.getDisplayName()!=null){
                editTextDisplayName.setText(user.getDisplayName() );

            }
            if(user.getPhotoUrl()!=null){
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageViewCamera);

            }
            if(user.isEmailVerified()){
                textViewVerified.setText("Email Verified");
            }else {
                textViewVerified.setText("Email Not Verified (Click here to verify)");
                textViewVerified.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {

                                    Toast.makeText(getApplicationContext(), "Verification Email Sent", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                    }
                });
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            finish();
            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.logoutId){
            mAuth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
