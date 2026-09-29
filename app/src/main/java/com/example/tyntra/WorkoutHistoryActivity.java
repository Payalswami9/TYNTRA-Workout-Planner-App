package com.example.tyntra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryActivity extends AppCompatActivity {

    // RecyclerView
    RecyclerView recyclerViewWorkouts;

    // Adapter and workout list
    WorkoutAdapter workoutAdapter;
    List<Workout> workoutList;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // Empty state views
    LinearLayout emptyState;
    Button btnEmptyAddWorkout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect Java file with XML layout
        setContentView(R.layout.activity_workout_history);


        // =====================================================
        // 1. FIND VIEWS
        // =====================================================

        recyclerViewWorkouts =
                findViewById(R.id.recyclerViewWorkouts);

        emptyState =
                findViewById(R.id.emptyState);

        btnEmptyAddWorkout =
                findViewById(R.id.btnEmptyAddWorkout);


        // =====================================================
        // 2. SETUP RECYCLERVIEW
        // =====================================================

        recyclerViewWorkouts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Create empty workout list
        workoutList = new ArrayList<>();

        // Create adapter
        workoutAdapter =
                new WorkoutAdapter(workoutList);

        // Connect adapter to RecyclerView
        recyclerViewWorkouts.setAdapter(
                workoutAdapter
        );


        // =====================================================
        // 3. INITIALIZE FIREBASE
        // =====================================================

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();


        // =====================================================
        // 4. EMPTY STATE - ADD WORKOUT BUTTON
        // =====================================================

        btnEmptyAddWorkout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    WorkoutHistoryActivity.this,
                    AddWorkoutActivity.class
            );

            startActivity(intent);
        });


        // =====================================================
        // 5. LOAD WORKOUTS
        // =====================================================

        loadWorkouts();
    }


    // =========================================================
    // LOAD WORKOUTS FROM FIRESTORE
    // =========================================================

    private void loadWorkouts() {

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Get current user's UID
        String userId =
                mAuth.getCurrentUser().getUid();


        // Get workouts belonging to current user
        db.collection("workouts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {


                    // =================================================
                    // 6. CLEAR OLD DATA
                    // =================================================

                    workoutList.clear();


                    // =================================================
                    // 7. READ WORKOUTS FROM FIRESTORE
                    // =================================================

                    for (var document :
                            queryDocumentSnapshots.getDocuments()) {

                        Workout workout = new Workout(

                                // Workout document ID
                                document.getId(),

                                // User ID
                                document.getString("userId"),

                                // Exercise name
                                document.getString(
                                        "exerciseName"
                                ),

                                // Sets
                                document.getLong("sets")
                                        .intValue(),

                                // Reps
                                document.getLong("reps")
                                        .intValue(),

                                // Workout date
                                document.getString(
                                        "workoutDate"
                                )
                        );


                        // Add workout to list
                        workoutList.add(workout);
                    }


                    // =================================================
                    // 8. REFRESH RECYCLERVIEW
                    // =================================================

                    workoutAdapter.notifyDataSetChanged();


                    // =================================================
                    // 9. SHOW / HIDE EMPTY STATE
                    // =================================================

                    if (workoutList.isEmpty()) {

                        // No workouts
                        // Show empty state
                        emptyState.setVisibility(
                                View.VISIBLE
                        );


                        // Hide RecyclerView
                        recyclerViewWorkouts.setVisibility(
                                View.GONE
                        );

                    } else {

                        // Workouts exist
                        // Hide empty state
                        emptyState.setVisibility(
                                View.GONE
                        );


                        // Show RecyclerView
                        recyclerViewWorkouts.setVisibility(
                                View.VISIBLE
                        );
                    }

                })


                // =================================================
                // 10. FIRESTORE ERROR
                // =================================================

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            WorkoutHistoryActivity.this,
                            "Failed to load workouts: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // =========================================================
    // 11. REFRESH WHEN RETURNING TO HISTORY SCREEN
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        // Refresh workout history
        // when coming back from Add/Edit screen
        if (mAuth != null) {

            loadWorkouts();
        }
    }
}