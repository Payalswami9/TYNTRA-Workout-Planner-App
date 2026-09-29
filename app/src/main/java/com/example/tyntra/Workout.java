package com.example.tyntra;

public class Workout {

    private String id;
    private String userId;
    private String exerciseName;
    private int sets;
    private int reps;
    private String workoutDate;

    public Workout() {
        // Required empty constructor for Firestore
    }

    public Workout(String id, String userId, String exerciseName,
                   int sets, int reps, String workoutDate) {

        this.id = id;
        this.userId = userId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.workoutDate = workoutDate;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public String getWorkoutDate() {
        return workoutDate;
    }
}
