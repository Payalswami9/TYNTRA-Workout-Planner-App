package com.example.tyntra;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddWorkoutActivity extends AppCompatActivity {

    EditText etExerciseName, etSets, etReps, etWorkoutDate;
    Button btnAddWorkout;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        etExerciseName = findViewById(R.id.etExerciseName);
        etSets = findViewById(R.id.etSets);
        etReps = findViewById(R.id.etReps);
        etWorkoutDate = findViewById(R.id.etWorkoutDate);

        btnAddWorkout = findViewById(R.id.btnAddWorkout);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Date picker
        etWorkoutDate.setOnClickListener(v -> showDatePicker());

        // Add workout
        btnAddWorkout.setOnClickListener(v -> addWorkout());
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String date = selectedDay + "/" +
                            (selectedMonth + 1) + "/" +
                            selectedYear;

                    etWorkoutDate.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void addWorkout() {

        String exerciseName =
                etExerciseName.getText().toString().trim();

        String setsText =
                etSets.getText().toString().trim();

        String repsText =
                etReps.getText().toString().trim();

        String workoutDate =
                etWorkoutDate.getText().toString().trim();

        // Validation
        if (exerciseName.isEmpty()) {
            etExerciseName.setError("Enter exercise name");
            return;
        }

        if (setsText.isEmpty()) {
            etSets.setError("Enter number of sets");
            return;
        }

        if (repsText.isEmpty()) {
            etReps.setError("Enter number of reps");
            return;
        }

        if (workoutDate.isEmpty()) {
            etWorkoutDate.setError("Select workout date");
            return;
        }

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get logged-in user's UID
        String userId = mAuth.getCurrentUser().getUid();

        // Create workout data
        Map<String, Object> workout = new HashMap<>();

        workout.put("userId", userId);
        workout.put("exerciseName", exerciseName);
        workout.put("sets", Integer.parseInt(setsText));
        workout.put("reps", Integer.parseInt(repsText));
        workout.put("workoutDate", workoutDate);

        // Save to Firestore
        db.collection("workouts")
                .add(workout)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            AddWorkoutActivity.this,
                            "Workout added successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Return to Home
                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AddWorkoutActivity.this,
                            "Failed to add workout: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}