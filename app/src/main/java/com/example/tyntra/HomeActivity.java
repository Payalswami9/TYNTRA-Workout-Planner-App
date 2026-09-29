package com.example.tyntra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    TextView tvWelcome, tvWorkoutCount;

    Button btnAddWorkout, btnWorkoutHistory, btnLogout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        // Connect XML views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvWorkoutCount = findViewById(R.id.tvWorkoutCount);

        btnAddWorkout = findViewById(R.id.btnAddWorkout);
        btnWorkoutHistory = findViewById(R.id.btnWorkoutHistory);
        btnLogout = findViewById(R.id.btnLogout);


        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Load user information
        loadUserProfile();

        // Load workout count
        loadWorkoutCount();


        // --------------------------------
        // ADD WORKOUT
        // --------------------------------

        btnAddWorkout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    AddWorkoutActivity.class
            );

            startActivity(intent);
        });


        // --------------------------------
        // WORKOUT HISTORY
        // --------------------------------

        btnWorkoutHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    WorkoutHistoryActivity.class
            );

            startActivity(intent);
        });


        // --------------------------------
        // LOGOUT
        // --------------------------------

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Toast.makeText(
                    HomeActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    HomeActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();
        });
    }


    // =====================================================
    // REFRESH WORKOUT COUNT WHEN HOME BECOMES VISIBLE
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (mAuth != null) {
            loadWorkoutCount();
        }
    }


    // =====================================================
    // LOAD USER PROFILE
    // =====================================================

    private void loadUserProfile() {

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();


        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String name =
                                documentSnapshot.getString("name");

                        if (name != null && !name.isEmpty()) {

                            tvWelcome.setText(
                                    "Welcome back " + name + " 👋"
                            );
                        }
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            HomeActivity.this,
                            "Unable to load profile",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // =====================================================
    // LOAD WORKOUT COUNT
    // =====================================================

    private void loadWorkoutCount() {

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();


        db.collection("workouts")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int count = querySnapshot.size();

                    tvWorkoutCount.setText(
                            "Total Workouts: " + count
                    );
                })
                .addOnFailureListener(e -> {

                    tvWorkoutCount.setText(
                            "Total Workouts: 0"
                    );

                    Toast.makeText(
                            HomeActivity.this,
                            "Unable to load workout count: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}
