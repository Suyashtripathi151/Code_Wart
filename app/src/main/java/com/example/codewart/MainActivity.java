package com.example.codewart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth uauth;
    FirebaseUser user;
    public DatabaseReference reference,dbrefUserdata,dbrefUsername;

    public String currentUser,USERNAME;

    private EditText inpemail,inpphone,inppass,inpconpass,inpusername,inpfullname;
    private CheckBox terms;
    private Button signup;
    private TextView login;

    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inpconpass=findViewById(R.id.inpconfpass);
        inpemail=findViewById(R.id.inpemail2);
        inpphone=findViewById(R.id.inpphone);
        inppass=findViewById(R.id.inppass);
        inpusername=findViewById(R.id.inpuserid);
        inpfullname=findViewById(R.id.inpfullname);

        terms=findViewById(R.id.checkBox);
        signup=findViewById(R.id.btnsignup);
        login=findViewById(R.id.textalready);

        uauth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        dbrefUserdata=reference.child("userdata");
        dbrefUsername=reference.child("username");




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!terms.isChecked()){
                        Toast.makeText(MainActivity.this, "Please agree to our terms & conditions to continue", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        checkNonEmpty();
                    }
                }
            });


    }


    private void verifyUsername(){
            String Username=inpusername.getText().toString();
//           currentUser = uauth.getCurrentUser().getUid();

            dbrefUsername.child(Username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().exists()){
                            DataSnapshot dataSnapshot= task.getResult();
                            Toast.makeText(MainActivity.this, "Usermail Already Taken", Toast.LENGTH_SHORT).show();

                        }

                        else {
                            Toast.makeText(MainActivity.this, "AVAIL", Toast.LENGTH_SHORT).show();
                            registerUser();

                        }
                    }
                    else {
                            Toast.makeText(MainActivity.this,task.getException()+ "", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }


    private void checkNonEmpty() {
        String username=inpusername.getText().toString();
        String email=inpemail.getText().toString();
        String name=inpfullname.getText().toString();
        String password=inppass.getText().toString();
        String conpass=inpconpass.getText().toString();
        String phoone=inpphone.getText().toString();

        EditText[] allEditexts={inpfullname,inpusername,inppass,inpconpass,inpphone,inpemail};
        String[] allData={name,username,password,conpass,phoone,email};

        if(email.isEmpty() || password.isEmpty()  || conpass.isEmpty()  || username.isEmpty() ||name.isEmpty() || phoone.isEmpty()){
            for(int i=0;i<+4;i++){
                if (allData[i].isEmpty()) {
                    allEditexts[i].setError("This Field Is Missing ! ");
                }
            }
        }

        else{
            if(email.matches(EmailPattern2)){
                inpemail.setError("Email is not Valid !");
            }
            else if (password.length()<6) {
                inppass.setError("Password must be atleast 6 digit long");
            }

            else if(!conpass.matches(password)){
                inpconpass.setError("Password do not match");
            }

            else if (username.length() < 6 || username.length() > 11) {
                inpusername.setError("Username must be between 6 and 11 characters");
            }

            else if (username.matches("^[a-zA-Z][a-zA-Z0-9]{0,3}$")) {
                inpusername.setError("Username must start with an alphabet and have at most 4 numeric values");
            }

            else{
                verifyUsername();
            }
        }


    }

    private void registerUser() {
        uauth.createUserWithEmailAndPassword(inpemail.getText().toString(),inppass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    currentUser = uauth.getCurrentUser().getUid();
                    uploadUserdata();
                    Toast.makeText(MainActivity.this, "Verifying User ...", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(MainActivity.this, "Registration Failed !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Registration Failed !"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void uploadUserdata(){

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering User");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String username=inpusername.getText().toString();
        String email=inpemail.getText().toString();
        String name=inpfullname.getText().toString();
        String password=inppass.getText().toString();
        String phoone=inpphone.getText().toString();
        String key =dbrefUserdata.push().getKey();

        HashMap<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("phone",phoone);
        map.put("email",email);
        map.put("password",password);
        map.put("userid",currentUser);
        map.put("username",username);

        HashMap<String,Object> map2=new HashMap<>();
        map2.put("userid",currentUser);
        map2.put("username",username);




        dbrefUserdata.child(currentUser).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    uploadUsername();
                    Toast.makeText(MainActivity.this, "Failed !"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(MainActivity.this, "Failed !"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            public void uploadUsername() {
                dbrefUsername.child(username).setValue(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "DONE !", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this,VerifyActivity.class);
                            startActivity(intent);
                        }

                        else {
                            Toast.makeText(MainActivity.this, "Failed !"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });










    }
}