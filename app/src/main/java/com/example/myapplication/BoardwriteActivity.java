package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.example.myapplication.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BoardwriteActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int GET_GALLARY = 100;
    private TextView et_board_write_title;
    private TextView et_board_write_contents;
    private Button btn_board_add_photo;
    private Button btn_create_post;
    private ImageButton btn_board_back;
    private Switch sw_board_type;
    private String target;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private String imagePath;
    private ScrollView scroll;
    private BitmapDrawable bitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        Intent intent = getIntent();
        target = intent.getStringExtra("DB");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        et_board_write_title = findViewById(R.id.et_board_write_title);
        et_board_write_contents = findViewById(R.id.et_board_write_contents);
        btn_create_post = findViewById(R.id.btn_create_post);
        sw_board_type = findViewById(R.id.sw_board_type);
        btn_board_add_photo = (Button) findViewById(R.id.btn_board_add_photo);
        btn_board_back = (ImageButton) findViewById(R.id.board_write_back);
        imageView = (ImageView) findViewById(R.id.imageView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        //사진
        btn_board_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GET_GALLARY);
            }

        });
        btn_create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post();
                if (sw_board_type.isChecked()) {
                    post.type = "공지";
                } else {
                    post.type = "일반";
                }
                upload(imagePath);
                post.title = String.valueOf(et_board_write_title.getText());
                post.username = mAuth.getCurrentUser().getEmail();      // 나중에 닉네임으로 나오게끔
                post.date = new SimpleDateFormat("MM-dd hh:mm").format(new Date(System.currentTimeMillis()));
                post.view_cnt = 0;
                post.contents = String.valueOf(et_board_write_contents.getText());
                post.img = imagePath;
                FirebaseDatabase.getInstance().getReference("target").child(target).child("post").push().setValue(post);
                setResult(200);
                finish();
            }
        });

        btn_board_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GET_GALLARY == requestCode && data != null) {
            imagePath = getPath(data.getData());
            File file =new File(imagePath);
            imageView.setImageURI(Uri.fromFile(file));
        }
    }
    public String getPath(Uri uri){
        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri,proj, null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }
    private void upload(String uri){
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