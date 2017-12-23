package de.avalax.fitbuddy.port.adapter.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.avalax.fitbuddy.domain.model.exercise.Exercise;
import de.avalax.fitbuddy.domain.model.finished_exercise.FinishedExercise;
import de.avalax.fitbuddy.domain.model.finished_exercise.FinishedExerciseRepository;
import de.avalax.fitbuddy.domain.model.finished_workout.BasicFinishedWorkout;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkout;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkoutException;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkoutId;
import de.avalax.fitbuddy.domain.model.finished_workout.FinishedWorkoutRepository;
import de.avalax.fitbuddy.domain.model.workout.Workout;
import de.avalax.fitbuddy.domain.model.workout.WorkoutId;
import de.avalax.fitbuddy.domain.model.workout.WorkoutRepository;

public class SQLiteFinishedWorkoutRepository implements FinishedWorkoutRepository {
    private static final String TABLE_FINISHED_WORKOUT = "finished_workout";
    private SQLiteOpenHelper sqLiteOpenHelper;
    private FinishedExerciseRepository finishedExerciseRepository;
    private WorkoutRepository workoutRepository;

    public SQLiteFinishedWorkoutRepository(
            SQLiteOpenHelper sqLiteOpenHelper,
            FinishedExerciseRepository finishedExerciseRepository,
            WorkoutRepository workoutRepository) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
        this.finishedExerciseRepository = finishedExerciseRepository;
        this.workoutRepository = workoutRepository;
    }

    @Override
    public FinishedWorkoutId saveWorkout(Workout workout) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        long id = database.insertOrThrow(TABLE_FINISHED_WORKOUT, null, getContentValues(workout));
        FinishedWorkoutId finishedWorkoutId = new FinishedWorkoutId(String.valueOf(id));
        for (Exercise exercise : workout.getExercises()) {
            finishedExerciseRepository.save(finishedWorkoutId, exercise);
        }
        database.close();
        workout.setLastExecution(getDate());
        workout.setFinishedCount(workout.getFinishedCount()+1);
        workoutRepository.save(workout);
        return finishedWorkoutId;
    }

    @Override
    public FinishedWorkout load(FinishedWorkoutId finishedWorkoutId)
            throws FinishedWorkoutException {
        if (finishedWorkoutId == null) {
            throw new FinishedWorkoutException();
        }
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        String[] columns = {"id", "workout_id", "name", "created"};
        String[] args = {finishedWorkoutId.getId()};
        Cursor cursor = database.query(TABLE_FINISHED_WORKOUT, columns,
                "id=?", args, null, null, null);
        if (cursor.moveToFirst()) {
            FinishedWorkout finishedWorkout = createFinishedWorkout(cursor);
            cursor.close();
            database.close();
            return finishedWorkout;
        } else {
            cursor.close();
            database.close();
            throw new FinishedWorkoutException();
        }
    }

    @Override
    public List<FinishedWorkout> loadAll() {
        List<FinishedWorkout> finishedWorkouts = new ArrayList<>();
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        String[] columns = {"id", "workout_id", "name", "created"};
        Cursor cursor = database.query(TABLE_FINISHED_WORKOUT, columns,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                finishedWorkouts.add(createFinishedWorkout(cursor));
            } while (cursor.moveToNext());
            cursor.close();
            database.close();
        }
        return finishedWorkouts;
    }

    @Override
    public void delete(FinishedWorkoutId finishedWorkoutId) {
        if (finishedWorkoutId == null) {
            return;
        }
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.delete(TABLE_FINISHED_WORKOUT, "id=" + finishedWorkoutId.getId(), null);
        database.close();
    }

    @Override
    public long size() {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        long size = DatabaseUtils.queryNumEntries(db, TABLE_FINISHED_WORKOUT);
        db.close();
        return size;
    }

    private FinishedWorkout createFinishedWorkout(Cursor cursor) {
        FinishedWorkoutId finishedWorkoutId = new FinishedWorkoutId(cursor.getString(0));
        WorkoutId workoutId = new WorkoutId(cursor.getString(1));
        List<FinishedExercise> exercises = addFinishedExercises(finishedWorkoutId);
        String name = cursor.getString(2);
        String created = cursor.getString(3);

        return new BasicFinishedWorkout(finishedWorkoutId, workoutId, name, created, exercises);
    }

    private List<FinishedExercise> addFinishedExercises(FinishedWorkoutId finishedWorkoutId) {
        return finishedExerciseRepository.allSetsBelongsTo(finishedWorkoutId);
    }

    private ContentValues getContentValues(Workout workout) {
        ContentValues values = new ContentValues();
        values.put("name", workout.getName());
        if (workout.getWorkoutId() != null) {
            String workoutId = workout.getWorkoutId().getId();
            values.put("workout_id", workoutId);
        }
        return values;
    }

    protected long getDate() {
        return new Date().getTime();
    }
}
