package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Intent.getIntent;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.myapplication.models.Post;
import com.example.myapplication.models.Target;
import com.example.myapplication.models.User;
import com.example.myapplication.models.bottom_favorite_profile_model;
import com.example.myapplication.models.bottom_home_data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Interner;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.sdk.story.model.BirthdayType;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class accountActivity extends Activity {

    private static final int GET_GALLARY = 100;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private EditText input_name;
    private EditText input_phone_no;
    private EditText input_birth_date;
    private EditText input_sosock;
    private EditText input_debut_date;
    private EditText input_SNS;
    private EditText input_pr;
    private ImageView input_image;
    private String Uid;
    private String imagePath;
    private Button account_image_change_profile;
    private Button account_details_save;
    private String uri_imagepath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        input_name = findViewById(R.id.input_name);
        input_debut_date = findViewById(R.id.input_debut_date);
        input_phone_no = findViewById(R.id.input_phone_no);
        input_pr = findViewById(R.id.input_pr);
        input_SNS = findViewById(R.id.input_SNS);
        input_pr = findViewById(R.id.input_pr);
        input_birth_date = findViewById(R.id.input_birth_date);
        input_sosock = findViewById(R.id.input_sosock);
        input_image = findViewById(R.id.account_image);
        account_image_change_profile = (Button) findViewById(R.id.account_image_change_profile);
        account_details_save = (Button) findViewById(R.id.account_details_save);

        Uid = mAuth.getUid();
        Log.d(TAG, "Uid : " + Uid);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users");
        databaseReference.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String phone_no = snapshot.child("phone").getValue().toString();
                String birth_date = snapshot.child("birth").getValue().toString();
                //이미지 받아오기
                String icon = snapshot.child("photoURL").getValue().toString();
                uri_imagepath = icon;
                File file = new File(icon);
                String strFileName = file.getName();
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://supportapp-f34a1.appspot.com");
                StorageReference storageReference = storage.getReference();
                storageReference.child("images/" + strFileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "img uri : " + uri);
                        Glide.with(accountActivity.this).load(uri).into(input_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                    }
                });
                //String sosock = snapshot.child("sosock").getValue().toString();
                //String debut_date = snapshot.child("debut_date").getValue().toString();
                //String SNS = snapshot.child("SNS").getValue().toString();
                //String pr = snapshot.child("pr").getValue().toString();
                //Glide.with(accountActivity.this).load(icon).into(input_image);
                input_name.setText(name);
                input_phone_no.setText(phone_no);
                input_birth_date.setText(birth_date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("accountActivity error", String.valueOf(error.toException())); //에러 시 출력
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        account_image_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GET_GALLARY);
            }

        });
        account_details_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = input_name.getText().toString();
                String phone = input_phone_no.getText().toString();
                String birth = input_birth_date.getText().toString();
                upload(imagePath);
                database = FirebaseDatabase.getInstance();
                databaseReference = database.getReference("Users");
                databaseReference.child(Uid).child("name").setValue(name);
                databaseReference.child(Uid).child("phone").setValue(phone);
                databaseReference.child(Uid).child("birth").setValue(birth);
                if(imagePath == null) {
                    databaseReference.child(Uid).child("photoURL").setValue(uri_imagepath);
                }
                else
                    databaseReference.child(Uid).child("photoURL").setValue(imagePath);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GET_GALLARY == requestCode && data != null) {
            imagePath = getPath(data.getData());
            File file = new File(imagePath);
            input_image.setImageURI(Uri.fromFile(file));
        }
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }

    private void upload(String uri) {
        StorageReference storageRef = storage.getReferenceFromUrl("gs://supportapp-f34a1.appspot.com");
        Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
            }
        });
    }
}