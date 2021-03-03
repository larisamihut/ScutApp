package com.example.scutapp.user.quiz;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.scutapp.R;
import com.example.scutapp.doctor.doctorchat.User;
import com.example.scutapp.doctor.map.models.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class QuizFragment extends Fragment {

    private boolean mLocationPermissionGranted = false;
    private static final String TAG = "Permission";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;
    private FirebaseFirestore fStore;
    private Button btnTest, btnNo1, btnNo2, btnNo3, btnYes1, btnYes2, btnYes3, btnWet, btnDry, btnNone;
    private int question1, question2, question3, question4, result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        btnTest = view.findViewById(R.id.btnTest);
        fStore = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = (question1 + question2 + question3 + question4);
                Intent intent = new Intent(getActivity(), TestResult.class);
                intent.putExtra("result", String.valueOf(result));
                startActivity(intent);
            }
        });
        buttonLogic(view);
        return view;
    }

    private void buttonLogic(View view) {
        btnNo1 = view.findViewById(R.id.btnNo1);
        btnNo1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btnNo1.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnYes1.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question1 = 0;
            }
        });

        btnNo2 = view.findViewById(R.id.btnNo2);
        btnNo2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btnNo2.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnYes2.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question2 = 0;
            }
        });

        btnNo3 = view.findViewById(R.id.btnNo3);
        btnNo3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btnNo3.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnYes3.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question3 = 0;
            }
        });

        btnYes1 = view.findViewById(R.id.btnYes1);
        btnYes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnYes1.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnNo1.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question1 = 25;
            }
        });

        btnYes2 = view.findViewById(R.id.btnYes2);
        btnYes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnYes2.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnNo2.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question2 = 25;
            }
        });

        btnYes3 = view.findViewById(R.id.btnYes3);
        btnYes3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnYes3.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnNo3.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question3 = 25;
            }
        });

        btnWet = view.findViewById(R.id.btnWet);
        btnNone = view.findViewById(R.id.btnNone);
        btnDry = view.findViewById(R.id.btnDry);

        btnWet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnWet.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnDry.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question4 = 15;
            }
        });

        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnWet.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnDry.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question4 = 0;
            }
        });


        btnDry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnDry.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_blue));
                btnWet.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                btnNone.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_color_primary));
                question4 = 25;
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

        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            // getChatrooms();
            Log.d(TAG, "Permission was given");
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
