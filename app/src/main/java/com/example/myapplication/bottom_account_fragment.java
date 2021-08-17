package com.example.myapplication;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class bottom_account_fragment<bottom_favorite_profile_models> extends Fragment {
    private View view;
    private ImageView imageView;
    private static final int GET_GALLARY = 100;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private String imagePath;
    private Button bottom_event_fragment_image_change_profile;
    private Button bottom_event_fragment_details_save;
    private TextView input_name;
    private TextView input_phone_no;
    private TextView input_birth_date;
    private TextView input_sosock;
    private TextView input_debut_date;
    private TextView input_SNS;
    private TextView input_pr;
    private DatabaseReference mDatabase;

    private List<bottom_favorite_profile_model> models = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_account_fragment, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user != null ? user.getUid() : null; // 로그인한 유저 고유 uid


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtime 가져오기
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        input_name = view.findViewById(R.id.input_name);
        input_debut_date = view.findViewById(R.id.input_debut_date);
        input_phone_no = view.findViewById(R.id.input_phone_no);
        input_pr = view.findViewById(R.id.input_pr);
        input_SNS = view.findViewById(R.id.input_SNS);
        input_pr = view.findViewById(R.id.input_pr);
        input_birth_date = view.findViewById(R.id.input_birth_date);
        input_sosock = view.findViewById(R.id.input_sosock);
        imageView = (ImageView) view.findViewById(R.id.bottom_event_fragment_image);
        bottom_event_fragment_details_save = (Button) view.findViewById(R.id.bottom_event_fragment_details_save);
        bottom_event_fragment_image_change_profile = (Button) view.findViewById(R.id.bottom_event_fragment_image_change_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        mDatabase.child("target").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                models.clear();
                ;
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    bottom_favorite_profile_model bottom_favorite_profile_model = snapshot.getValue(com.example.myapplication.models.bottom_favorite_profile_model.class);
                    models.add(bottom_favorite_profile_model);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bottom_event_fragment_image_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            //앨범 선택
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, GET_GALLARY);
            }
        });

        bottom_event_fragment_details_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_favorite_profile_model model = new bottom_favorite_profile_model();

                upload(imagePath);
                model.name = String.valueOf(input_name.getText());
                model.email = mAuth.getCurrentUser().getEmail();      // 나중에 닉네임으로 나오게끔
                //model.phone = mAuth.ge();//valueOf(input_phone_no.getText());
                model.intro = String.valueOf(input_pr.getText());
                model.sns = String.valueOf(input_SNS.getText());
                model.team = String.valueOf(input_sosock.getText());
                model.birth = String.valueOf(input_birth_date.getText());
                model.debut = String.valueOf(input_debut_date.getText());
                model.icon = imagePath;
                FirebaseDatabase.getInstance().getReference("profile").push().setValue(model);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GET_GALLARY == requestCode && requestCode == RESULT_OK) {
            imagePath = getPath(data.getData());
            File file = new File(imagePath);
            imageView.setImageURI(Uri.fromFile(file));
        }
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, proj, null, null, null);

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