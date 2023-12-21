package com.example.codewart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText inpemail,inpppass;
    private Button login;
    private TextView forgot,create;
    int a;

    public DatabaseReference reference,dbrefUserdata;

    String currentUser,USERNAME;


    FirebaseAuth uauth;
    FirebaseUser user;
    public static final String Shared_Pref="SharedPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uauth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        dbrefUserdata=reference.child("userdata");

        forgot=findViewById(R.id.textforget);

        inpemail=findViewById(R.id.inpemail2);
        inpppass=findViewById(R.id.inppass2);
        login=findViewById(R.id.btnlogin2);
        create=findViewById(R.id.textcreate);
        a=1;

        checkBox(a);




        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void checkBox(int a){
        SharedPreferences sharedPreferences=getSharedPreferences(Shared_Pref,MODE_PRIVATE);
        String check=sharedPreferences.getString("Pass","");
        if(a==1){
            if(check.equals("true")){
                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
       else {
            Toast.makeText(this, "login your account after verifying", Toast.LENGTH_SHORT).show();
        }

    }

    public void login(){
        String Password=inpppass.getText().toString();
        String Usermail=inpemail.getText().toString();




        uauth.signInWithEmailAndPassword(Usermail,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = uauth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        SharedPreferences sharedPreferences=getSharedPreferences(Shared_Pref,MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Pass","true");
                        editor.apply();
                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please Verify Your E-Mail", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(LoginActivity.this,VerifyActivity.class);
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,e.getMessage()+ "", Toast.LENGTH_SHORT).show();

            }
        });

    }
}