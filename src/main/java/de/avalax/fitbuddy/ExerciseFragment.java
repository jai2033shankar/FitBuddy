package de.avalax.fitbuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import de.avalax.fitbuddy.edit.EditableExercise;
import de.avalax.fitbuddy.edit.EditExerciseActivity;
import de.avalax.fitbuddy.edit.ExistingEditableExercise;
import de.avalax.fitbuddy.edit.NewEditableExercise;
import de.avalax.fitbuddy.progressBar.ProgressBarOnTouchListener;
import de.avalax.fitbuddy.progressBar.VerticalProgressBar;
import de.avalax.fitbuddy.workout.Exercise;
import de.avalax.fitbuddy.workout.Workout;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ExerciseFragment extends RoboFragment {

    @Inject
    Context context;
    @Inject
    private Workout workout;
    @InjectView(R.id.leftProgressBar)
    private VerticalProgressBar repsProgressBar;
    @InjectView(R.id.rightProgressBar)
    private VerticalProgressBar setsProgressBar;
    private int exercisePosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        exercisePosition = getArguments().getInt("exerciseIndex");
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repsProgressBar.setOnTouchListener(new ProgressBarOnTouchListener(context,repsProgressBar, getMaxMoveForReps(workout)) {
            @Override
            public void onFlingEvent(int moved) {
                changeReps(moved);
            }

            @Override
            protected void onLongPressedLeftEvent() {
                addExerciseBeforeCurrentExercise();
            }

            @Override
            protected void onLongPressedRightEvent() {
                editCurrentExercise();
            }
        });

        setsProgressBar.setOnTouchListener(new ProgressBarOnTouchListener(context, setsProgressBar, getMaxMoveForSets(workout)) {
            @Override
            public void onFlingEvent(int moved) {
                changeSets(moved);
            }

            @Override
            protected void onLongPressedLeftEvent() {
                editCurrentExercise();
            }

            @Override
            protected void onLongPressedRightEvent() {
                addExerciseAfterCurrentExercise();
            }
        });

        setViews(exercisePosition);
    }

    private void addExerciseBeforeCurrentExercise() {
        //TODO: addExerciseBeforeCurrentExercise
        startActivity(getIntent(createNewEditableExercise()));
        Log.d("LongClick", "addExerciseBeforeCurrentExercise()");
    }

    private void addExerciseAfterCurrentExercise() {
        //TODO: addExerciseAfterCurrentExercise
        startActivity(getIntent(createNewEditableExercise()));
        Log.d("LongClick", "addExerciseAfterCurrentExercise()");
    }

    private void editCurrentExercise() {
        //TODO: editCurrentExercise
        startActivity(getIntent(createExistingEditableExercise()));
        Log.d("LongClick", "editCurrentExercise()");
    }

    private NewEditableExercise createNewEditableExercise() {
        NewEditableExercise newEditableExercise = new NewEditableExercise();
        //TODO: extract to resources
        newEditableExercise.setWeight(2.5);
        newEditableExercise.setWeightRaise(1.25);
        newEditableExercise.setReps(12);
        newEditableExercise.setSets(3);
        return newEditableExercise;
    }

    private EditableExercise createExistingEditableExercise() {
        Exercise exercise = workout.getExercise(exercisePosition);
        return new ExistingEditableExercise(exercise);
    }

    private Intent getIntent(EditableExercise editableExercise) {
        Intent intent = new Intent(getActivity().getApplicationContext(), EditExerciseActivity.class);
        intent.putExtra("exercise", editableExercise);
        return intent;
    }

    private int getMaxMoveForSets(Workout workout) {
        return workout.getExercise(exercisePosition).getMaxSets();
    }

    private int getMaxMoveForReps(Workout workout) {
        return workout.getExercise(exercisePosition).getMaxReps();
    }

    private void changeReps(int moved) {
        setReps(moved);
        setViews(exercisePosition);
    }

    private void changeSets(int moved) {
        setSet(workout.getExercise(exercisePosition).getSetNumber() + moved);
    }

    private void setReps(int moved) {
        Exercise exercise = workout.getExercise(exercisePosition);
        exercise.setReps(exercise.getReps() + moved);
    }

    private void setSet(int setNumber) {
        workout.getExercise(exercisePosition).setCurrentSet(setNumber);
        setViews(exercisePosition);
    }

    private void setViews(int exercisePosition) {
        repsProgressBar.setProgressBar(workout.getExercise(exercisePosition).getCurrentSet());
        setsProgressBar.setProgressBar(workout,exercisePosition);
    }
}
