package com.example.scutapp.user.talkto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scutapp.R;

import com.example.scutapp.user.talkto.adapter.MessageAdapter;
import com.example.scutapp.user.talkto.model.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Conversation extends AppCompatActivity {
    TextView username;
    FirebaseUser fuser;
    DocumentReference documentReference;
    private FirebaseFirestore fStore;
    ImageButton btn_send;
    EditText text_send;
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    Intent intent;
    String doctorid;
    private static final String TAG = "Chats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        doctorid = intent.getStringExtra("doctorid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        documentReference = fStore.collection("doctors").document(doctorid);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), doctorid, msg);
                } else {
                    Toast.makeText(Conversation.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                username.setText(documentSnapshot.getString("fullName"));
            }
        });

        fStore.collection("chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                assert value != null;
                readMessage(fuser.getUid(), doctorid);
            }
        });

    }

    private void sendMessage(String sender, final String receiver, String message) {

        DocumentReference documentReference = fStore.collection("chats").document();
        Map<String, Object> chat = new HashMap<>();
        chat.put("sender", sender);
        chat.put("receiver", receiver);
        chat.put("message", message);
        chat.put("timestamp", Timestamp.now());
        //insert user in the database
        documentReference.set(chat);

        final DocumentReference chatReference1 = fStore.collection("chat_list").document(fuser.getUid());
        chatReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Map<String, Object> receiver1 = new HashMap<>();
                receiver1.put(doctorid, doctorid);
                if (documentSnapshot.getData() != null) {
                    chatReference1.update(receiver1);
                } else {
                    chatReference1.set(receiver1);
                }
            }
        });

        final DocumentReference chatReference2 = fStore.collection("chat_list").document(doctorid);
        chatReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Map<String, Object> receiver2 = new HashMap<>();
                receiver2.put(fuser.getUid(), fuser.getUid());
                if (documentSnapshot.getData() != null) {
                    chatReference2.update(receiver2);
                } else {
                    chatReference2.set(receiver2);
                }
            }
        });

    }

    private void readMessage(final String myid, final String doctorid) {
        mchat = new ArrayList<>();

        fStore.collection("chats").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    Chat chat = document.toObject(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(doctorid) ||
                            chat.getReceiver().equals(doctorid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(Conversation.this, mchat);
                recyclerView.setAdapter(messageAdapter);

            }
        });
    }
}