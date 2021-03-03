package com.example.scutapp.user.talkto.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scutapp.R;
import com.example.scutapp.user.talkto.adapter.DoctorAdapter;
import com.example.scutapp.user.talkto.model.Chat;
import com.example.scutapp.user.talkto.model.Doctor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ChatsFragment extends Fragment {

    private static final String TAG = "Chat";
    private RecyclerView recyclerView;

    private DoctorAdapter doctorAdapter;

    private List<Doctor> mDoctors;
    private FirebaseUser fuser;
    private FirebaseFirestore fStore;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        ChatsFragment tabFragment = new ChatsFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
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
                }
            }
        });
        return view;
    }

    private void chatList(final List<String> list) {
        mDoctors = new ArrayList<>();
        mDoctors.clear();
        fStore.collection("doctors").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Doctor doctor = document.toObject(Doctor.class);
                    Log.d(TAG, "Doc: " + document.getData());
                    //display 1 doctor from chats
                    for (int i = 0; i < list.size(); i++) {
                        if (doctor.getId().equals(list.get(i))) {
                            mDoctors.add(doctor);
                        }
                    }
                }
                doctorAdapter = new DoctorAdapter(getActivity(), mDoctors, true);
                recyclerView.setAdapter(doctorAdapter);
            }
        });
    }
}
