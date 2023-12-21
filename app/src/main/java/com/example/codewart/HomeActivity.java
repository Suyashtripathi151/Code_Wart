package com.example.codewart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private TextView name,txt1,txt2,txt3,txt4;

    private ImageView profile,btn1,btn2,btn3,btn4;
    private Button menu;

    private ImageSlider powslider;

    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference, dbpow,dbrefreaduserid;
    String currentUser,userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        powslider = findViewById(R.id.image_slider);

        uauth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        name=findViewById(R.id.textUsername);


        btn1=findViewById(R.id.imgBtn1);
        btn2=findViewById(R.id.imgBtn2);
        btn3=findViewById(R.id.imgBtn3);
        btn4=findViewById(R.id.imgBtn4);

        menu=findViewById(R.id.button4);

        profile=findViewById(R.id.imgProfile);

        dbpow = reference.child("Slider");

        getuserid();


        powslide();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "TESTING GIT", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(HomeActivity.this,StackActivity.class);
                startActivity(intent);
            }
        });

    }
    private void powslide() {

        final List<SlideModel> remoteimages = new ArrayList<>();

        dbpow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren())
                    remoteimages.add(new SlideModel(data.child("Url")
                            .getValue().toString(), data.child("Title").getValue().toString(), ScaleTypes.FIT));
                powslider.setImageList(remoteimages, ScaleTypes.FIT);
                powslider.setSlideAnimation(AnimationTypes.CUBE_OUT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }




    private void getuserid() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Your Data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        dbrefreaduserid = reference.child("userdata");
        dbrefreaduserid.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        userID = String.valueOf(dataSnapshot.child("name").getValue());
                        String img= String.valueOf(dataSnapshot.child("imageUrl").getValue());
                        name.setText(userID);
                        Picasso.get().load(img).into(profile);
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}