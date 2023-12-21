package com.example.codewart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.YEAR,year);

        String currentDateString= DateFormat.getDateInstance().format(c.getTime());

        TextView textViewdate=(TextView) findViewById(R.id.proftextViewdate);
        textViewdate.setText(currentDateString);
    }

    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference,dbUserdata;
    private StorageReference storageRef;
    String currentUser,userID,Fuserid,Fusername;

    Boolean isProfile;

    Boolean uploadOnlyData;

    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";

    Drawable a2,b2;


    private TextView txtname,txtemail,txtphone,txtabout,txtdob,txtgender,txtdate;
    private EditText inp_name,inp_email,inp_phone,inp_about,inp_link,inp_git;

    private Button update,menu;

    private ImageView profile;

    private ImageButton dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uauth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        storageRef = FirebaseStorage.getInstance().getReference();

        dbUserdata=reference.child("userdata");

        dob=findViewById(R.id.profbuttondate);
        update=findViewById(R.id.profupdatebutton);
        menu=findViewById(R.id.profbutton4);

        profile=findViewById(R.id.profimgProfile);

        inp_name = findViewById(R.id.profinp_name);
        inp_phone = findViewById(R.id.profinp_phone);
        inp_email = findViewById(R.id.profinp_email);
        inp_about=findViewById(R.id.profinp_about);
        inp_link=findViewById(R.id.profinp_linkedin);
        inp_git=findViewById(R.id.profinp_github);

////        txtabout=findViewById(R.id.proftextbio);
////        txtdob=findViewById(R.id.proftextdob);
////        txtgender=findViewById(R.id.proftextGender);
//        txtname = findViewById(R.id.proftextviewname);
//        txtphone = findViewById(R.id.proftextviewphone);
//        txtemail = findViewById(R.id.proftextViewemail);

        txtdate=findViewById(R.id.proftextViewdate);

        dbUserdata.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot= task.getResult();
                        String Fname=String.valueOf(dataSnapshot.child("name").getValue());
                        String Fphone=String.valueOf(dataSnapshot.child("phone").getValue());
                        String Femail=String.valueOf(dataSnapshot.child("email").getValue());
                        String Fabout=String.valueOf(dataSnapshot.child("about").getValue());
                        String Fdob=String.valueOf(dataSnapshot.child("dob").getValue());
                        String Flinked=String.valueOf(dataSnapshot.child("linkedIn").getValue());
                        String FGit=String.valueOf(dataSnapshot.child("Github").getValue());
                        Fuserid=String.valueOf(dataSnapshot.child("userid").getValue());
                        Fusername=String.valueOf(dataSnapshot.child("username").getValue());

                        if((dataSnapshot.child("imageUrl").exists())){
                            String Profpic=String.valueOf(dataSnapshot.child("imageUrl").getValue());
                            Picasso.get().load(Profpic).into(profile);
                            isProfile=true;
                        }
                        else {
                            isProfile=false;
                            profile.setImageResource(R.drawable.jester);
                            a2=profile.getDrawable();
                        }



                        inp_name.setText(Fname);
                        inp_phone.setText(Fphone);
                        inp_email.setText(Femail);
                        txtdate.setText(Fdob);
                        inp_about.setText(Fabout);
                        txtdate.setText(Fdob);
                        inp_git.setText(FGit);
                        inp_link.setText(Flinked);



                    }

                    else {
                        Toast.makeText(ProfileActivity.this, "NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(ProfileActivity.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });



        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker=new DatePickerFragement();
                datePicker.show(getSupportFragmentManager(),"PICK YOUR DATE OF BIRTH");
            }
        });

        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                b2=profile.getDrawable();
                if(!isProfile){
                    if(a2==b2){
//                      Toast.makeText(ProfileActivity.this, "You have not selected any picture", Toast.LENGTH_SHORT).show();
                        uploadOnlyData=true;
                        uploadProfData();
                    }
                    else{
                        uploadOnlyData=false;
                        uploadquery();
                    }
                }
                else {
                    uploadquery();
                }


            }});

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepic();
            }
        });

    }

    private void uploadquery() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbUserdata = reference.child("userdata");
        String key = dbUserdata.push().getKey();

        String name = inp_name.getText().toString();
        String phone = inp_phone.getText().toString();
        String emal = inp_email.getText().toString();
        String about=inp_about.getText().toString();
        String git=inp_git.getText().toString();
        String linked=inp_link.getText().toString();


        String DB=txtdate.getText().toString();

        // Convert the ImageView to a byte array
        byte[] imageData = imagetobyte(profile);

        // Create a reference to the location where you want to store the image in Firebase Storage
        StorageReference imageRef = storageRef.child("Profile Pictures/" +currentUser);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name",name);
                        map.put("phone",phone);
                        map.put("email",emal);
                        map.put("about",about);
                        map.put("imageUrl",imageUrl);
                        map.put("dob",DB);
                        map.put("linkedIn",linked);
                        map.put("Github",git);
                        map.put("username",Fusername);
                        map.put("userid",Fuserid);

                        // Save the image data to the Firebase Realtime Database
                        dbUserdata.child(currentUser).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Data uploaded successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(ProfileActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed to upload Data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
//                Toast.makeText(PictureOfWeek.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                Toast.makeText(ProfileActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadProfData(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        dbUserdata = reference.child("userdata");

        String name = inp_name.getText().toString();
        String phone = inp_phone.getText().toString();
        String email = inp_email.getText().toString();
        String about=inp_about.getText().toString();
        String DB=txtdate.getText().toString();
        String git=inp_git.getText().toString();
        String linked=inp_link.getText().toString();


        HashMap<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("phone",phone);
        map.put("email",email);
        map.put("dob",DB);
        map.put("about",about);
        map.put("linkedIn",linked);
        map.put("Github",git);
        map.put("username",Fusername);
        map.put("userid",Fuserid);


        // Save the image data to the Firebase Realtime Database
        dbUserdata.child(currentUser).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Data uploaded successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ProfileActivity.this,HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to upload Data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void choosepic(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.alertdialog,null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        ImageView gallery=dialogView.findViewById(R.id.gallery);
        ImageView camera=dialogView.findViewById(R.id.camera);
        TextView txtgallery=dialogView.findViewById(R.id.textTitle);
        TextView txtcamera=dialogView.findViewById(R.id.textcamera);

        AlertDialog alertDialogimage = builder.create();
        alertDialogimage.show();

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takegallery();
                alertDialogimage.cancel();

            }
        });



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPer()){
                    takecamera();
                    alertDialogimage.cancel();
                }

            }
        });



    }


    private void takegallery(){
        Intent pickphoto=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickphoto,1);

    }

    private void takecamera(){
        Intent pickpicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pickpicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(pickpicture,2);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch ((requestCode)){
            case 1:
                if(resultCode==RESULT_OK){
                    Uri selectedImageUri=data.getData();
                    profile.setImageURI(selectedImageUri);
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    Bundle bundle=data.getExtras();
                    Bitmap bitmapImage=(Bitmap) bundle.get("data");
                    profile.setImageBitmap(bitmapImage);
                }
        }
    }

    private boolean checkAndRequestPer() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA},20);
                return false;
            }
        }
        return true;
    }

    private void onRequestPermissionResult(int resultcode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(resultcode,permissions,grantResults);
        if(resultcode==20 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            takecamera();
        }
        else {
            Toast.makeText(this, "OKAY", Toast.LENGTH_SHORT).show();
        }
    }


    private byte[] imagetobyte(ImageView imageView){
        Bitmap bitmap;
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}