package com.example.codewart;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class StackAdapter extends FirebaseRecyclerAdapter<StackModel, StackAdapter.myviewholder> {
    String Userkey;
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    String currentUser=user.getUid();
    DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbUserdata=ref.child("userdata");

    public StackAdapter(@NonNull FirebaseRecyclerOptions<StackModel> options) {
        super(options);
    }



    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull StackModel model) {

        int reversedPosition = getItemCount() - position - 1;
        StackModel reversedModel = getItem(reversedPosition);
        holder.bind(reversedModel);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String currentUser=user.getUid();
        Userkey=getRef(position).getKey();

        String a=model.getError();;
        String a2=model.getPlatform();




        assert Userkey != null;




    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.stackitem,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView subject,preview,deadline,solutions,postdate,user;
        ImageView profile;
        CardView card;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            subject=(TextView)itemView.findViewById(R.id.notifCompName);
            deadline=(TextView)itemView.findViewById(R.id.notifDeadline);
            preview=(TextView)itemView.findViewById(R.id.notificPreview);
            card=(CardView) itemView.findViewById(R.id.card);


        }

        public void bind(StackModel model) {



            subject.setText(model.getError());
            preview.setText(model.getPlatform());


            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.stackdetails,null);
                    builder.setCancelable(true);
                    builder.setView(dialogView);

                    String A=(model.getUserid());


                    TextView message=dialogView.findViewById(R.id.textView5);
                    TextView compName=dialogView.findViewById(R.id.jobCompName);
                    TextView compName2=dialogView.findViewById(R.id.jobCompName2);
                    TextView platformm=dialogView.findViewById(R.id.jobDeadline2);
                    TextView solutions=dialogView.findViewById(R.id.textView6);
                    TextView postdate=dialogView.findViewById(R.id.textView4);
                    TextView user=dialogView.findViewById(R.id.textView3);
                    ImageView profile=dialogView.findViewById(R.id.profimgProfile2);

                    dbUserdata.child(A).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().exists()){
                                    DataSnapshot dataSnapshot= task.getResult();
                                     String  Fusername=String.valueOf(dataSnapshot.child("username").getValue());
                                    user.setText(Fusername);
                                    if((dataSnapshot.child("imageUrl").exists())){
                                        String Profpic=String.valueOf(dataSnapshot.child("imageUrl").getValue());
                                        Picasso.get().load(Profpic).into(profile);

                                    }
                                }
                            }

                        }
                    });

                    message.setText(model.getDescription());
                    compName.setText(model.getError());
                    platformm.setText(model.getPlatform());
                    postdate.setText(model.getTime());




                    if(model.getSolution().isEmpty()){
                        solutions.setEnabled(false);
                        solutions.setVisibility(View.INVISIBLE);
                        compName2.setEnabled(false);
                        compName2.setVisibility(View.INVISIBLE);
                    }
                    else {
                        solutions.setText(model.getSolution());
                    }

                    compName.setMovementMethod(LinkMovementMethod.getInstance());

                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Check if the height is currently set to WRAP_CONTENT
                            ViewGroup.LayoutParams params = message.getLayoutParams();
                            int originalHeight =400;

                            if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                                // If it is WRAP_CONTENT, set it to the original height
                                params.height = originalHeight;
                            } else {
                                // If it is not WRAP_CONTENT, set it to WRAP_CONTENT
                                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            }

                            // Request a layout update
                            message.requestLayout();
                        }
                    });
                    solutions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Check if the height is currently set to WRAP_CONTENT
                            ViewGroup.LayoutParams params = solutions.getLayoutParams();
                            int originalHeight = 400;

                            if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                                // If it is WRAP_CONTENT, set it to the original height
                                params.height = originalHeight;
                            } else {
                                // If it is not WRAP_CONTENT, set it to WRAP_CONTENT
                                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            }

                            // Request a layout update
                            solutions.requestLayout();
                        }
                    });

                    AlertDialog alertDialogimage = builder.create();
                    alertDialogimage.show();
                }
            });






        }



    }

}
