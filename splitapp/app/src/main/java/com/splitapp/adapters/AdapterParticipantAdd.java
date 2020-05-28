package com.splitapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.splitapp.R;
import com.splitapp.activities.GroupParticipantAddActivity;
import com.splitapp.models.ModelFriendList;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipantAdd extends RecyclerView.Adapter<AdapterParticipantAdd.HolderParticipantAdd> {

    private Context context;
    private ArrayList<ModelFriendList> friendsList;
    private String groupId,myGroupRole;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    public AdapterParticipantAdd(Context context,ArrayList<ModelFriendList> friendsList,String groupId,String myGroupRole){
        this.context=context;
        this.friendsList=friendsList;
        this.groupId=groupId;
        this.myGroupRole=myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_paticipant_add,parent,false);
        return new HolderParticipantAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantAdd holder, final int position) {
        final ModelFriendList modelFriendList=friendsList.get(position);
        String name=modelFriendList.getName();
        String email=modelFriendList.getEmail();
        final String uid=modelFriendList.getUid();


        holder.nameTv.setText(name);
        holder.emailTv.setText(email);

        //checkIfAlreadyExists(modelFriendList,holder);

        holder.relative_participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  //int pos=position;
                  Log.d("position", String.valueOf(position)) ;
                  //final ModelFriendList modelFriendList=friendsList.get(position);


                CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Groups");
                rootRef.document(groupId).collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            int var=0;
                            Log.d("var", String.valueOf(var));
                            for (QueryDocumentSnapshot document : task.getResult()){
                                final ModelFriendList modelFriendList=friendsList.get(position);
                                Log.d("docId",document.getId());
                                Log.d("frId",modelFriendList.getUid());

                                if(document.getId().equals(modelFriendList.getUid())){
                                    var++;
                                }


                            }
                            Log.d("var", String.valueOf(var));
                            if(var==1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Remove Participant")
                                        .setMessage("Are you sure to remove this user from this group?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final ModelFriendList modelFriendList = friendsList.get(position);
                                                removeParticipant(modelFriendList);
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                            else{
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Add participant")
                                        .setMessage("Add this user in this group?")
                                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Log.d("position", String.valueOf(position)) ;
                                                final ModelFriendList modelFriendList=friendsList.get(position);
                                                //Log.d("uid",modelFriendList.getUid());
                                                addParticipant(modelFriendList);
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }

                        }
                        else{

                        }
                    }
                });


                /*AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Add participant")
                        .setMessage("Add this user in this group?")
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.d("position", String.valueOf(position)) ;
                                final ModelFriendList modelFriendList=friendsList.get(position);
                                  //Log.d("uid",modelFriendList.getUid());
                                addParticipant(modelFriendList);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();*/

            }
        });
    }

    private void addParticipant(final ModelFriendList modelFriendList) {
        String timestamp=""+System.currentTimeMillis();
        final HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",modelFriendList.getUid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("transactionAmount","0");

        Log.d("uid",modelFriendList.getUid());
        //
        final CollectionReference rootRef1 = FirebaseFirestore.getInstance().collection("Groups");
        rootRef1.document(groupId).collection("Participants").document(modelFriendList.getUid()).set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Added successfully",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        //updating transactions collection
        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Groups");
        rootRef.document(groupId).collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if(modelFriendList.getUid().equals(document.getId())){

                        }
                        else{
                            final HashMap<String,String> hashMap1=new HashMap<>();
                            hashMap1.put("transactionAmount","0");
                            rootRef.document(groupId).collection("Participants").document(modelFriendList.getUid())
                                    .collection("transactions").document(document.getId()).set(hashMap1);
                            rootRef.document(groupId).collection("Participants").document(document.getId())
                                    .collection("transactions").document(modelFriendList.getUid()).set(hashMap1);
                        }
                    }
                }
            }
        });

        /*CollectionReference rootRef11 = FirebaseFirestore.getInstance().collection("Groups");
        rootRef.document(groupId).collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if(modelFriendList.getUid().equals(document.getId())){

                        }
                        else{
                            final HashMap<String,String> hashMap11=new HashMap<>();
                            hashMap11.put("transactionAmount","0");
                            rootRef1.document(groupId).collection("Participants").document(document.getId())
                                    .collection("transactions").document(modelFriendList.getUid()).set(hashMap11);
                        }
                    }
                }
            }
        });*/

        //rootRef1.document(groupId).collection("Participants").document(modelFriendList.getUid()).collection("transactions").

    }

    private void removeParticipant(final ModelFriendList modelFriendList) {

        final CollectionReference rootRef11 = FirebaseFirestore.getInstance().collection("Groups");
        rootRef11.document(groupId).collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        rootRef11.document(groupId).collection("Participants").document(document.getId())
                                .collection("transactions").document(modelFriendList.getUid()).delete();

                    }
                }
            }
        });

        final CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Groups");
        rootRef.document(groupId).collection("Participants").document(modelFriendList.getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Participant Removed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    /*private void checkIfAlreadyExists(ModelFriendList modelFriendList, final HolderParticipantAdd holder) {
        db.collection("Groups").document(groupId).collection("Participants").document(modelFriendList.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                }
                else{

                }
            }
        });

    }*/

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class HolderParticipantAdd extends RecyclerView.ViewHolder{

        private TextView nameTv,emailTv;
        private RelativeLayout relative_participants;

        public HolderParticipantAdd(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.personNameTv);
            emailTv=itemView.findViewById(R.id.emailTv);
            relative_participants=itemView.findViewById(R.id.relative_particiant_add);
        }
    }
}
