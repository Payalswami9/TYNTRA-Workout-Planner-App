package com.example.tyntra;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);

        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WorkoutViewHolder holder,
            int position) {

        Workout workout = workoutList.get(position);

        // Show workout information
        holder.tvExerciseName.setText(
                workout.getExerciseName()
        );

        holder.tvSetsReps.setText(
                "Sets: " + workout.getSets()
                        + "\nReps: " + workout.getReps()
        );

        holder.tvWorkoutDate.setText(
                "Date: " + workout.getWorkoutDate()
        );

        // EDIT BUTTON
        holder.btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    EditWorkoutActivity.class
            );

            intent.putExtra(
                    "workoutId",
                    workout.getId()
            );

            intent.putExtra(
                    "exerciseName",
                    workout.getExerciseName()
            );

            intent.putExtra(
                    "sets",
                    workout.getSets()
            );

            intent.putExtra(
                    "reps",
                    workout.getReps()
            );

            intent.putExtra(
                    "workoutDate",
                    workout.getWorkoutDate()
            );

            v.getContext().startActivity(intent);
        });

        // DELETE BUTTON
        holder.btnDelete.setOnClickListener(v -> {

            new android.app.AlertDialog.Builder(
                    v.getContext()
            )
                    .setTitle("Delete Workout")
                    .setMessage(
                            "Are you sure you want to delete this workout?"
                    )
                    .setNegativeButton(
                            "CANCEL",
                            null
                    )
                    .setPositiveButton(
                            "DELETE",
                            (dialog, which) -> {

                                FirebaseFirestore
                                        .getInstance()
                                        .collection("workouts")
                                        .document(workout.getId())
                                        .delete()
                                        .addOnSuccessListener(unused -> {

                                            Toast.makeText(
                                                    v.getContext(),
                                                    "Workout deleted successfully!",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            int currentPosition =
                                                    holder.getAdapterPosition();

                                            if (currentPosition != RecyclerView.NO_POSITION) {

                                                workoutList.remove(
                                                        currentPosition
                                                );

                                                notifyItemRemoved(
                                                        currentPosition
                                                );
                                            }

                                        })
                                        .addOnFailureListener(e -> {

                                            Toast.makeText(
                                                    v.getContext(),
                                                    "Delete failed: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();

                                        });
                            }
                    )
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    // VIEW HOLDER
    public static class WorkoutViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvExerciseName;
        TextView tvSetsReps;
        TextView tvWorkoutDate;

        Button btnEdit;
        Button btnDelete;

        public WorkoutViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvExerciseName =
                    itemView.findViewById(
                            R.id.tvExerciseName
                    );

            tvSetsReps =
                    itemView.findViewById(
                            R.id.tvSetsReps
                    );

            tvWorkoutDate =
                    itemView.findViewById(
                            R.id.tvWorkoutDate
                    );

            btnEdit =
                    itemView.findViewById(
                            R.id.btnEdit
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }
    }
}