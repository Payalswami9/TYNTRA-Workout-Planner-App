package com.example.tyntra;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditWorkoutActivity extends AppCompatActivity {

    EditText etExerciseName, etSets, etReps, etWorkoutDate;
    Button btnUpdateWorkout;

    FirebaseFirestore db;

    String workoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_workout);

        etExerciseName = findViewById(R.id.etExerciseName);
        etSets = findViewById(R.id.etSets);
        etReps = findViewById(R.id.etReps);
        etWorkoutDate = findViewById(R.id.etWorkoutDate);

        btnUpdateWorkout = findViewById(R.id.btnUpdateWorkout);

        db = FirebaseFirestore.getInstance();

        // Get workout information from previous screen
        workoutId = getIntent().getStringExtra("workoutId");

        String exerciseName =
                getIntent().getStringExtra("exerciseName");

        int sets =
                getIntent().getIntExtra("sets", 0);

        int reps =
                getIntent().getIntExtra("reps", 0);

        String workoutDate =
                getIntent().getStringExtra("workoutDate");

        // Show existing values
        etExerciseName.setText(exerciseName);
        etSets.setText(String.valueOf(sets));
        etReps.setText(String.valueOf(reps));
        etWorkoutDate.setText(workoutDate);

        // Date picker
        etWorkoutDate.setOnClickListener(v -> showDatePicker());

        // Update
        btnUpdateWorkout.setOnClickListener(v -> updateWorkout());
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

    private void updateWorkout() {

        String exerciseName =
                etExerciseName.getText().toString().trim();

        String setsText =
                etSets.getText().toString().trim();

        String repsText =
                etReps.getText().toString().trim();

        String workoutDate =
                etWorkoutDate.getText().toString().trim();

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

        Map<String, Object> updatedWorkout = new HashMap<>();

        updatedWorkout.put("exerciseName", exerciseName);
        updatedWorkout.put("sets", Integer.parseInt(setsText));
        updatedWorkout.put("reps", Integer.parseInt(repsText));
        updatedWorkout.put("workoutDate", workoutDate);

        db.collection("workouts")
                .document(workoutId)
                .update(updatedWorkout)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            EditWorkoutActivity.this,
                            "Workout updated successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EditWorkoutActivity.this,
                            "Failed to update workout: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}