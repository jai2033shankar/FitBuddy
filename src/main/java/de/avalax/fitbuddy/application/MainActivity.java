package de.avalax.fitbuddy.application;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnPageChange;
import de.avalax.fitbuddy.application.dialog.EditWeightDialogFragment;
import de.avalax.fitbuddy.application.manageWorkout.ManageWorkoutActivity;
import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.domain.model.workout.WorkoutNotFoundException;

import javax.inject.Inject;
import java.text.DecimalFormat;

public class MainActivity extends FragmentActivity implements EditWeightDialogFragment.DialogListener {
    private static final int MANAGE_WORKOUT = 1;
    @InjectView(R.id.pager)
    protected ViewPager viewPager;
    @Inject
    protected WorkoutSession workoutSession;
    @InjectView(R.id.workoutProgressBar)
    protected ProgressBar workoutProggressBar;
    protected String actionSwitchWorkout;
    private DecimalFormat decimalFormat;
    private String weightTitle;
    private MenuItem menuItem;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ButterKnife.inject(this);
        ((FitbuddyApplication) getApplication()).inject(this);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        this.menuItem = menu.findItem(R.id.action_change_weight);
        updatePage(this.index);
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(null);

        this.index = 0;
        this.decimalFormat = new DecimalFormat("###.###");
        this.weightTitle = getResources().getString(R.string.title_weight);
        try {
            workoutSession.switchToLastLoadedWorkout();
        } catch (WorkoutNotFoundException wnfe) {
            Log.d("MainActivity", wnfe.getMessage(), wnfe);
            startManageWorkoutActivity();
        }
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), workoutSession));

        actionSwitchWorkout = getResources().getString(R.string.action_switch_workout);
    }

    @OnPageChange(R.id.pager)
    protected void updatePage(int index) {
        this.index = index;
        Workout workout = workoutSession.getWorkout();
        if (workout == null || workout.getExercises().isEmpty()) {
            startManageWorkoutActivity();
        } else {
            //TODO: 3x times - unnamed exercise from resources & move to a ui helper
            Exercise exercise = workout.getExercises().get(index);
            setTitle(exercise.getName().length() > 0 ? exercise.getName() : "unnamed exercise");
            if (menuItem != null) {
                menuItem.setTitle(exerciseWeightText(index));
                updateWorkoutProgress(index);
            }
        }
    }

    private String exerciseWeightText(int index) {
        //TODO: helper method
        Exercise exercise = workoutSession.getWorkout().getExercises().get(index);
        if (exercise.getSets().isEmpty()) {
            return "-";
        }
        double weight = exercise.getCurrentSet().getWeight();
        if (weight > 0) {
            return String.format(weightTitle, decimalFormat.format(weight));
        } else {
            return "-";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_switch_workout) {
            startManageWorkoutActivity();
        }
        if (item.getItemId() == R.id.action_change_weight) {
            showEditDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == MANAGE_WORKOUT &&
                resultCode == Activity.RESULT_OK) {
            viewPager.getAdapter().notifyDataSetChanged();
            viewPager.invalidate();
            viewPager.setCurrentItem(0, true);
            updatePage(0);
        }
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        Exercise exercise = workoutSession.getWorkout().getExercises().get(index);
        if (exercise.getSets().isEmpty()) {
            return;
        }
        double weight = exercise.getCurrentSet().getWeight();
        EditWeightDialogFragment.newInstance(weight).show(fm, "fragment_edit_name");
    }

    protected void updateWorkoutProgress(int exerciseIndex) {
        Workout workout = workoutSession.getWorkout();
        workoutProggressBar.setProgress(calculateProgressbarHeight(workout.getProgress(exerciseIndex)));
    }

    private int calculateProgressbarHeight(double progess) {
        return (int) Math.round(progess * 100);
    }

    @Override
    public void onDialogPositiveClick(EditWeightDialogFragment editWeightDialogFragment) {
        workoutSession.getWorkout().getExercises().get(index).getCurrentSet().setWeight(editWeightDialogFragment.getWeight());
        updatePage(index);
    }

    private void startManageWorkoutActivity() {
        Intent intent = new Intent(this, ManageWorkoutActivity.class);
        startActivityForResult(intent, MANAGE_WORKOUT);
    }
}