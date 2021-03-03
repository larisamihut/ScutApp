package com.example.scutapp.doctor.doctorchat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scutapp.R;
import com.example.scutapp.doctor.doctorchat.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoctorChatsFragment extends Fragment {

    private static final String TAG = "Chat";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private FirebaseUser fuser;
    private FirebaseFirestore fStore;
    private static Context mContext;
    private ConstraintLayout constraintLayout;

    public Context getContext() {
        return mContext;
    }

    public DoctorChatsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        constraintLayout = view.findViewById(R.id.noMessage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fStore = FirebaseFirestore.getInstance();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        fStore.collection("chat_list").document(fuser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.getData() != null) {
                    List<String> list = new ArrayList<>();
                    Map<String, Object> map = documentSnapshot.getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        list.add(entry.getKey());
                        Log.d("TAG", entry.getKey());
                    }
                    chatList(list);
                } else {
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private void chatList(final List<String> list) {
        mUsers = new ArrayList<>();
        mUsers.clear();
        fStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    User user = document.toObject(User.class);
                    Log.d(TAG, "Doc: " + document.getData());
                    //display 1 doctor from chats
                    for (int i = 0; i < list.size(); i++) {
                        if (user.getId().equals(list.get(i))) {
                            Log.d("message from", "Doc: " + user.getId());
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getActivity(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }
        });
    }
}
