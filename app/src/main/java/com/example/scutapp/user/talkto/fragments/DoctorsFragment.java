package com.example.scutapp.user.talkto.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scutapp.R;
import com.example.scutapp.user.talkto.adapter.DoctorAdapter;
import com.example.scutapp.user.talkto.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class DoctorsFragment extends Fragment {

    private RecyclerView recyclerView;
    private static List<Doctor> mDoctors;
    private DoctorAdapter doctorAdapter;
    private FirebaseAuth fAuth;
    private static final String TAG = "Doctors";

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        DoctorsFragment tabFragment = new DoctorsFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fAuth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctors, container, false);

        fAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view_doctors);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDoctors = new ArrayList<>();
        readDoctors();

        return view;
    }

    private void readDoctors() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        fStore.collection("doctors").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Doctor doctor = document.toObject(Doctor.class);
                        mDoctors.add(doctor);
                    }
                    doctorAdapter = new DoctorAdapter(getActivity(), mDoctors, false);
                    recyclerView.setAdapter(doctorAdapter);
                }
            }
        });
    }
}
