package de.avalax.fitbuddy.app;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import de.avalax.fitbuddy.app.edit.WorkoutAdapter;
import de.avalax.fitbuddy.core.workout.Workout;

import javax.inject.Inject;

public class ManageWorkoutActivity extends ListActivity implements ActionBar.OnNavigationListener {
    @Inject
    protected WorkoutSession workoutSession;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FitbuddyApplication) getApplication()).inject(this);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //TODO: fill dropdown with workouts
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
                android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
        //TODO: select current workout
        actionBar.setSelectedNavigationItem(0);

        //TODO: select from dropdown
        Workout workout = workoutSession.getWorkout();
        setListAdapter(WorkoutAdapter.newInstance(getApplication(),R.layout.row, workout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_workout_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return true;
    }

}