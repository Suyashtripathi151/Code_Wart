package com.example.codewart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateStackActivity extends AppCompatActivity {
    private EditText inpproblem,inpplatform,inpdetails,inpsolution;
    private Button submit;
    private ImageView screenshot;

    private CheckBox IOT,ML,DS,AD,WD,OTHER,UIUX,CS,SQL;



    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference,dbUserdata,getDbUserdata;
    String currentUser,userID,Fusername;
   public String Fuserid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stack);

        uauth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();



        dbUserdata=reference.child("Stack");
        getDbUserdata=reference.child("userdata");



        inpdetails=findViewById(R.id.inpdesc);
        inpplatform=findViewById(R.id.inpide);
        inpproblem=findViewById(R.id.inperror);
        inpsolution=findViewById(R.id.inpsolution);

        submit=findViewById(R.id.createstacksubmit);
        AD=findViewById(R.id.stackcheckBox2);
        WD=findViewById(R.id.stackcheckBox3);
        SQL=findViewById(R.id.stackcheckBox4);

        UIUX=findViewById(R.id.stackcheckBox22);
        ML=findViewById(R.id.stackcheckBox33);
        CS=findViewById(R.id.stackcheckBox44);

        DS=findViewById(R.id.stackcheckBox222);
        IOT=findViewById(R.id.stackcheckBox333);
        OTHER=findViewById(R.id.stackcheckBox444);


        getDbUserdata.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot= task.getResult();
                        Fuserid=String.valueOf(dataSnapshot.child("username").getValue());

                    }

                    else {
                        Toast.makeText(CreateStackActivity.this, "NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(CreateStackActivity.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });


      submit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          uploadStackData();
          }
      });


    }

    private void uploadTagsData(String stackKey) {
        // Create a map to store the selected tags
        HashMap<String, Object> tagsMap = new HashMap<>();

        // Add the selected tags to the map
        tagsMap.put("IOT", IOT.isChecked());
        tagsMap.put("ML", ML.isChecked());
        tagsMap.put("DS", DS.isChecked());
        tagsMap.put("AD", AD.isChecked());
        tagsMap.put("WD", WD.isChecked());
        tagsMap.put("OTHER", OTHER.isChecked());
        tagsMap.put("UIUX", UIUX.isChecked());
        tagsMap.put("CS", CS.isChecked());
        tagsMap.put("SQL", SQL.isChecked());


        // Save the tags data to the "Tags" child under the corresponding stack key
        dbUserdata.child(stackKey).child("Tags").setValue(tagsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateStackActivity.this, "Data uploaded successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateStackActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateStackActivity.this, "Failed to upload Tags Data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void uploadStackData(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        String Error = inpproblem.getText().toString();
        String platform = inpplatform.getText().toString();
        String description = inpdetails.getText().toString();
        String solution=inpsolution.getText().toString();
        String key=dbUserdata.push().getKey();




        HashMap<String, Object> map = new HashMap<>();
        map.put("Error",Error);
        map.put("Platform",platform);
        map.put("Description",description);
        map.put("Solution",solution);
        map.put("userid",currentUser);
        map.put("username",Fuserid);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        map.put("time", currentTime);


        dbUserdata.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    uploadTagsData(key);
                } else {
                    Toast.makeText(CreateStackActivity.this, "Failed to upload Data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}