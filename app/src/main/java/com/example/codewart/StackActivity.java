package com.example.codewart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class StackActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    StackAdapter adapter2;

    Button ask;


    String userID,currentUser;

    private FirebaseAuth uauth;
    FirebaseUser user;

    DatabaseReference dbrefreaduserid,reference,dbprofpic,dbbug;
    public static final String Shared_Pref = "SharedPref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        ask=findViewById(R.id.button2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        uauth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StackActivity.this,CreateStackActivity.class);
                startActivity(intent);
            }
        });



        FirebaseRecyclerOptions<StackModel> options2=new FirebaseRecyclerOptions.Builder<StackModel>().setQuery(FirebaseDatabase.getInstance().getReference().child("Stack").orderByKey(),StackModel.class).build();

        adapter2=new StackAdapter(options2);
        recyclerView.setAdapter(adapter2);









    }




    @Override
    protected void onStart(){
        super.onStart();
        adapter2.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter2.stopListening();
    }


}