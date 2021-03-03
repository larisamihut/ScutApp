package com.example.scutapp.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.scutapp.Login;
import com.example.scutapp.R;
import com.example.scutapp.doctor.doctorchat.User;
import com.example.scutapp.doctor.doctorchat.adapter.UserAdapter;
import com.example.scutapp.user.talkto.model.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageDoctor extends AppCompatActivity {

    private static final String TAG = "Chat";
    private RecyclerView recyclerView;
    private Button logoutbtn;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    private FirebaseUser fuser;
    private FirebaseFirestore fStore;
    private List<String> usersList;
    private static Context mContext;
    private ConstraintLayout constraintLayout;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_doctor);

        recyclerView = findViewById(R.id.recycler_view);
        constraintLayout = findViewById(R.id.noMessage);
        logoutbtn = findViewById(R.id.logoutBtn);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        fStore.collection("chats").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                usersList.clear();
                for (DocumentChange document : snapshots.getDocumentChanges()) {
                    Chat chat = document.getDocument().toObject(Chat.class);
                    Log.d(TAG, "Doc from doclist " + document.getDocument().getData());
                    if (chat.getSender().equals(fuser.getUid())) {
                        usersList.add(chat.getReceiver());
                        Log.d(TAG, "userlist: " + chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        usersList.add(chat.getSender());
                        Log.d(TAG, "userlist: " + chat.getReceiver());
                    }
                }
                readChats();
            }
        });
    }


    private void readChats() {
        mUsers = new ArrayList<>();
        mUsers.clear();

        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        User user = document.toObject(User.class);
                        Log.d(TAG, "Doc: " + document.getData());
                        //display 1 user from chats
                        for (String id : usersList) {
                            if (user.getId().equals(id)) {
                                if (mUsers.size() != 0) {
                                    for (User user1 : mUsers) {
                                        if (user.getId().equals(id) && !mUsers.contains(user)) {
                                            mUsers.add(user);
                                        }
                                    }
                                } else {
                                    mUsers.add(user);
                                }
                            }
                        }
                    }
                    if (mUsers.size() == 0) {
                        constraintLayout.setVisibility(View.VISIBLE);
                    } else {
                        userAdapter = new UserAdapter(getApplicationContext(), mUsers, true);
                        recyclerView.setAdapter(userAdapter);
                    }

                }
            }
        });
    }
}

