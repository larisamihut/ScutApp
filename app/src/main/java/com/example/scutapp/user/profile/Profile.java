package com.example.scutapp.user.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.scutapp.R;
import com.example.scutapp.doctor.doctorchat.User;
import com.example.scutapp.doctor.map.models.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile extends Fragment {
    public static final String TAG = "Profile";
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private FirebaseAuth fAuth;
    private Button saveBtn, btnNone, btnHealthy, btnInfected;
    private FirebaseFirestore fStore;
    private FirebaseUser fUser;
    private ProgressBar progressBar;
    private String userId, state;

    private boolean mLocationPermissionGranted = false;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;


    public Profile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        saveBtn = view.findViewById(R.id.saveBtn);
        btnHealthy = view.findViewById(R.id.btnHealthy);
        btnInfected = view.findViewById(R.id.btnInfected);
        btnNone = view.findViewById(R.id.btnNone);
        progressBar = view.findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        buttonLogic();
        fetchData();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                if (!TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
                    updatePassword();
                }

                progressBar.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private void fetchData() {
        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(requireActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                fullNameEditText.setText(documentSnapshot.getString("fullName"));
                emailEditText.setText(documentSnapshot.getString("email"));
                phoneNumberEditText.setText(documentSnapshot.getString("phone"));
                state = documentSnapshot.getString("state");

                switch (state) {
                    case "Healthy": {
                        btnHealthy.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                        break;
                    }
                    case "Infected": {
                        btnInfected.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                        break;
                    }
                    case "None": {
                        btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                        break;
                    }
                    default:
                        break;
                }

            }
        });

    }

    private void updatePassword() {
        String password = passwordEditText.getText().toString().trim();
        if (password.length() < 6) {
            passwordEditText.setError("Password must be >= 6 characters");
        }

        fUser.updatePassword(passwordEditText.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "User Profile has been updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData() {
        DocumentReference documentReference = fStore.collection("users").document(userId);
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullNameEditText.getText().toString());
        user.put("email", emailEditText.getText().toString());
        user.put("phone", phoneNumberEditText.getText().toString());
        user.put("state", state);

        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "User Profile has been updated", Toast.LENGTH_SHORT).show();
            }
        });

        String email = emailEditText.getText().toString().trim();
        fUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "User Profile has been updated", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void buttonLogic() {
        btnHealthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnHealthy.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnInfected.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                state = "Healthy";
            }
        });

        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                btnHealthy.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnInfected.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                state = "None";
            }
        });

        btnInfected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnInfected.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_white));
                btnHealthy.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                state = "Infected";
            }
        });
    }


    private void getLastKnownLocation() {
        Log.d(TAG, "getLatKnownLocation: called");

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "onComplete: latitude:" + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude:" + geoPoint.getLongitude());

                    mUserLocation.setGeo_point(geoPoint);
                    mUserLocation.setTimestamp(null); // Automatically set the timestamp
                    saveUserLocation();
                }
            }
        });
    }

    private void getUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();

            DocumentReference userRef = fStore.collection("users").document(FirebaseAuth.getInstance().getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully get the user details");
                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);
                        getLastKnownLocation();
                    }
                }
            });
        }
    }

    private void saveUserLocation() {
        if (mUserLocation != null) {
            DocumentReference locationRef = fStore.collection("user_locations").document(FirebaseAuth.getInstance().getUid());
            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "saveUserLocation: \n inserted user location into database"
                                + "\n latitude" + mUserLocation.getGeo_point().getLatitude()
                                + "\n longitude:" + mUserLocation.getGeo_point().getLongitude());
                    }
                }
            });

        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //gps enabled?
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    //is the device able to use google services?
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            // getChatrooms();
            Log.d(TAG, "Permisiunea a fost data");
            getUserDetails();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    //getChatrooms();
                    Log.d(TAG, "Permisiunea a fost data");
                    getUserDetails();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    public void onResume() {
        super.onResume();
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
                Log.d(TAG, "Permisiunea a fost data");
                getUserDetails();
            } else {
                getLocationPermission();
            }
        }
    }

}


