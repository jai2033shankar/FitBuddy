package de.avalax.fitbuddy.app;

import de.avalax.fitbuddy.core.workout.Workout;
import de.avalax.fitbuddy.datalayer.WorkoutDAO;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class WorkoutSessionTest {
    WorkoutSession workoutSession;

    WorkoutDAO workoutDAO;

    private Workout workout;

    @Before
    public void setUp() throws Exception {
        workoutDAO = mock(WorkoutDAO.class);
        workout = mock(Workout.class);
        when(workoutDAO.load()).thenReturn(workout);
        workoutSession = new WorkoutSession(workoutDAO);
    }

    @Test
    public void shouldInitialzeWithWorkoutFromWorkoutDAO() {
        assertThat(workoutSession.getWorkout(), equalTo(workout));
    }

    @Test
    public void testFinishWorkout_shouldCallWorkoutDAOSaveAndLoadNewWorkout() throws Exception {
        Workout newWorkout = mock(Workout.class);
        when(workoutDAO.load()).thenReturn(newWorkout);

        workoutSession.finishWorkout();

        verify(workoutDAO).save(workout);
        assertThat(workoutSession.getWorkout(), equalTo(newWorkout));
    }


}