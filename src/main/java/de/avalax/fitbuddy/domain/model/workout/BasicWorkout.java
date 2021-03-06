package de.avalax.fitbuddy.domain.model.workout;

import java.util.ArrayList;
import java.util.List;

import de.avalax.fitbuddy.domain.model.ResourceException;
import de.avalax.fitbuddy.domain.model.exercise.BasicExercises;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.exercise.Exercises;

public class BasicWorkout implements Workout {
    private Exercises exercises;
    private String name;
    private WorkoutId workoutId;

    public BasicWorkout(WorkoutId workoutId, String name, List<Exercise> exercises) {
        this.workoutId = workoutId;
        this.exercises = new BasicExercises(exercises);
        this.name = name;
    }

    public BasicWorkout() {
        this.exercises = new BasicExercises(new ArrayList<>());
        this.name = "";
    }

    @Override
    public WorkoutId getWorkoutId() {
        return workoutId;
    }

    @Override
    public void setWorkoutId(WorkoutId workoutId) {
        this.workoutId = workoutId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name.trim();
        }
    }

    @Override
    public double getProgress(int position) throws ResourceException {
        Exercise exercise = exercises.get(position);
        return (position + exercise.getProgress()) / exercises.size();
    }

    @Override
    public Exercises getExercises() {
        return exercises;
    }

    @Override
    public String toString() {
        if (workoutId == null) {
            return "BasicWorkout [name=" + name + "]";
        }
        return "BasicWorkout [name=" + name + ", workoutId=" + workoutId.toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (workoutId == null) {
            return super.equals(obj);
        }
        return obj instanceof Workout && workoutId.equals(((Workout) obj).getWorkoutId());
    }

    @Override
    public int hashCode() {
        if (workoutId == null) {
            return super.hashCode();
        }
        return workoutId.hashCode();
    }
}
