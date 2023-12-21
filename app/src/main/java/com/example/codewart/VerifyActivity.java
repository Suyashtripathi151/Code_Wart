package com.example.codewart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifyActivity extends AppCompatActivity {
    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference dbref;

    String EmailPattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";



    EditText email;
    Button verify;
    TextView create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        email=findViewById(R.id.verinpemail);
        verify=findViewById(R.id.verbtnverify);
        create=findViewById(R.id.vertextcreate);

        uauth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = uauth.getCurrentUser();


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VerifyActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email2=email.getText().toString();

                if(email2.matches(EmailPattern)){

                    assert user != null;
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(VerifyActivity.this, "SENT", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(VerifyActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VerifyActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                        }
                    });


//

                }


                else{
                    verify.setError("Email is not Valid !");
                }




            }
        });




    }
}