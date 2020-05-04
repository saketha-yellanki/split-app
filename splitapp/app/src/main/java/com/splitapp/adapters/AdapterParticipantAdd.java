package com.splitapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public void onBindViewHolder(@NonNull HolderParticipantAdd holder, int position) {
        final ModelFriendList modelFriendList=friendsList.get(position);
        String name=modelFriendList.getName();
        String email=modelFriendList.getEmail();
        final String uid=modelFriendList.getUid();

        holder.nameTv.setText(name);
        holder.emailTv.setText(email);

        //checkIfAlreadyExists(modelFriendList,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference rootRef = FirebaseFirestore.getInstance().collection("Groups");
                rootRef.document(groupId).collection("Participants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                //final String g_id = document.getId();
                                //final String g_role = document.get("role").toString();
                                //final String g_time = document.get("timestamp").toString();
                                String[] options;

                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Choose an option");
                                if(myGroupRole.equals("creator")){
                                    options=new String[]{"Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            removeParticipant(modelFriendList);
                                        }
                                    }).show();
                                }
                                else{
                                }
                            }

                        }
                        else{
                                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                                builder.setTitle("Add participant")
                                        .setMessage("Add this user in this group?")
                                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
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
                });

            }
        });
    }

    private void addParticipant(ModelFriendList modelFriendList) {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",modelFriendList.getUid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",""+timestamp);

        db.collection("Groups").document(groupId).collection("Participants").document(modelFriendList.getUid()).set(hashMap)
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
    }

    private void removeParticipant(ModelFriendList modelFriendList) {
        db.collection("Groups").document(groupId).collection("Participants").document(modelFriendList.getUid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

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

        public HolderParticipantAdd(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.personNameTv);
            emailTv=itemView.findViewById(R.id.emailTv);
        }
    }
}
