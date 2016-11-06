package com.jrb.phiitnesstimer_paid;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import android.content.Context;
import android.database.SQLException;

/**
 * Created by James on 19/04/2015.
 */
public class PHIIT_DataSource {
    // Database fields
    private SQLiteDatabase database;
    private PHIIT_DbHelper dbHelper;
    private Context appContext;

    private static final String CommaSep = ", ";
    private static final String OpenBrac = " (";
    private static final String CloseBrac = ")";
    private static final String SemiColon = "; ";
    private static final String All = "* ";
    private static final String SELECT_ = "SELECT ";
    private static final String FROM_ = " FROM ";
    private static final String WHERE_ = " WHERE ";
    private static final String AS_ = " AS ";
    private static final String MAX_ = "MAX(";
    private static final String TOP_ = "TOP(";
    private static final String SUM_ = "SUM(";
    private static final String COUNT_ = "COUNT(";
    private static final String EQUALS_ = " = ";
    private static final String SORT_ = " ORDER BY ";
    private static final String ASC = " ASC ";
    private static final String DESC = " DESC ";
    private static final String GROUP_ = " GROUP BY ";

    public PHIIT_DataSource (Context context) {
        dbHelper = new PHIIT_DbHelper(context);
        appContext = context;

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public String getField (Integer workout_number, String table_name, String column_name) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_
            + column_name
            + FROM_ + table_name
            + WHERE_ + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
            , null );
        if (c != null){
            c.moveToFirst();
            String str = String.valueOf(c.getInt(0));
            if(str.equals("0"))
                str = c.getString(0);
            c.close();
            return str;
        } else {
            return "";
        }
    }

    public int LastEntry (String table_name, String column_name) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_ + MAX_
                        + column_name + CloseBrac + AS_ + column_name + FROM_
                        + table_name
                , null );

        if (c != null){
            c.moveToFirst();
            int IntI = c.getInt(c.getColumnIndex(column_name));
            c.close();
            return IntI;
        } else {
            return 0;
        }
    }

    public int WorkoutNumRounds (Integer workout_number) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_ + COUNT_
            + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NUMBER + CloseBrac
            + FROM_ + DatabaseContract.RoundDetails.TABLE_NAME
            + WHERE_ + DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
            , null );

        if (c != null){
            c.moveToFirst();
            int IntI = c.getInt(0);
            c.close();
            return IntI;
        } else {
            return 0;
        }
    }

    // Returns total rest in milliseconds
    public int RestTime (Integer workout_number) {
        Cursor c1 = dbHelper.getReadableDatabase().rawQuery(SELECT_
            + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_REST_ROUND
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_COMPLETED
            + FROM_ + DatabaseContract.ArchivedWorkouts.TABLE_NAME
            + WHERE_ + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
            , null );
        Cursor c2 = dbHelper.getReadableDatabase().rawQuery(SELECT_ + COUNT_
            + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NUMBER + CloseBrac
            + FROM_ + DatabaseContract.RoundDetails.TABLE_NAME
            + WHERE_ + DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
            , null );
        if (c1 != null && c2 != null){
            c1.moveToFirst();
            c2.moveToFirst();
            int rounds = c2.getInt(0)-1;
            // for AMRAPrepeats, use cycles, not rounds
            if(c1.getInt(c1.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_COMPLETED))>0 )
                rounds = c1.getInt(c1.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_COMPLETED)) -1 ;
            int IntI = c1.getInt(c1.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_REST_ROUND)) * rounds * 1000;
            if(IntI<0) IntI = 0; // Where no rest round data set
            c1.close();
            c2.close();
            return IntI;
        } else {
            c1.close();
            c2.close();
            return 0;
        }
    }

    public int WorkTime (Integer workout_number) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_ + SUM_
            + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_TIME + CloseBrac
            + FROM_ + DatabaseContract.RoundDetails.TABLE_NAME
            + WHERE_ + DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
            , null );
        if (c != null){
            c.moveToFirst();
            int IntI = c.getInt(0);
            c.close();
            return IntI;
        } else {
            return 0;
        }
    }

    public ArrayList<Round> WorkoutRounds (Integer workout_number) {

        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_
            + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NAME
            + CommaSep + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_TIME
            + FROM_ + DatabaseContract.RoundDetails.TABLE_NAME
            + WHERE_ + DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
            , null  );

        if (c != null){
            c.moveToFirst();
            ArrayList<Round> rounds = new ArrayList<Round>();
            while(!c.isAfterLast()) {
                Round round = new Round();
                round.setRoundName(c.getString(c.getColumnIndex(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NAME)));
                round.setRoundTime(c.getInt(c.getColumnIndex(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_TIME)));
                rounds.add(round);
                c.moveToNext();
            }
            c.close();
            return rounds;
        } else {
            return null;
        }
    }

    public void saveRound (int workout_number, long[] time_splits, int rest_time) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        for(int i = 1; i < time_splits.length; i+=1){
            //Only log the rounds with a time set
            if(time_splits[i] !=0 ) {
                values.put(DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER, workout_number);
                values.put(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NUMBER, i);
                values.put(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NAME, appContext.getResources().getString(R.string.text_round_prefix) + i);
                // convert SystemTime.millis to just the times for this round
                // for rounds 2+ take out rest time
                if(i>1)
                    values.put(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_TIME, time_splits[i] - time_splits[i - 1] - rest_time);
                else
                    values.put(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_TIME, time_splits[i] - time_splits[i - 1]);

                long insert_id = database.insert(DatabaseContract.RoundDetails.TABLE_NAME, null, values);
            }
        }
    }

    public ArrayList<Cycle> RepCounts (Integer workout_number) {

        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_
                + DatabaseContract.RepCounts.COLUMN_NAME_ROUND_NAME
                + CommaSep + DatabaseContract.RepCounts.COLUMN_NAME_REP_COUNT
                + FROM_ + DatabaseContract.RepCounts.TABLE_NAME
                + WHERE_ + DatabaseContract.RepCounts.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
                , null);

        if (c != null){
            c.moveToFirst();
            ArrayList<Cycle> cycles= new ArrayList<Cycle>();
            while(!c.isAfterLast()) {
                Cycle cycle = new Cycle();
                cycle.setWorkoutNumber(workout_number);
                cycle.setRoundName(c.getString(c.getColumnIndex(DatabaseContract.RepCounts.COLUMN_NAME_ROUND_NAME)));
                cycle.setRepCount(c.getInt(c.getColumnIndex(DatabaseContract.RepCounts.COLUMN_NAME_REP_COUNT)));
                cycles.add(cycle);
                c.moveToNext();
            }
            c.close();
            return cycles;
        } else {
            return null;
        }
    }

    public ArrayList<Cycle> SummaryRepCounts (Integer workout_number) {
        final String strExercise = "Exercise ";
        final String strTotal = "TotalReps";

        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_
                + DatabaseContract.RepCounts.COLUMN_NAME_EXERCISE_NUMBER
                + CommaSep + SUM_ + DatabaseContract.RepCounts.COLUMN_NAME_REP_COUNT + CloseBrac + AS_ + strTotal
                + FROM_ + DatabaseContract.RepCounts.TABLE_NAME
                + WHERE_ + DatabaseContract.RepCounts.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
                + GROUP_ + DatabaseContract.RepCounts.COLUMN_NAME_EXERCISE_NUMBER
                , null);

        if (c != null){
            c.moveToFirst();
            ArrayList<Cycle> cycles= new ArrayList<Cycle>();
            while(!c.isAfterLast()) {
                Cycle cycle = new Cycle();
                cycle.setWorkoutNumber(workout_number);
                cycle.setRoundName(strExercise + c.getString(c.getColumnIndex(DatabaseContract.RepCounts.COLUMN_NAME_EXERCISE_NUMBER)));
                cycle.setRepCount(c.getInt(c.getColumnIndex(strTotal)));
                cycles.add(cycle);
                c.moveToNext();
            }
            c.close();
            return cycles;
        } else {
            return null;
        }
    }

    public int TotalReps (Integer workout_number) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_ + SUM_
                + DatabaseContract.RepCounts.COLUMN_NAME_REP_COUNT + CloseBrac
                + FROM_ + DatabaseContract.RepCounts.TABLE_NAME
                + WHERE_ + DatabaseContract.RepCounts.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
                , null );
        if (c != null){
            c.moveToFirst();
            int IntI = c.getInt(0);
            c.close();
            return IntI;
        } else {
            return 0;
        }
    }


    public void saveRound (int workout_number, long[][] time_splits) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        for(int i = 1; i < time_splits.length; i+=1) {
            for (int j = 1; j < time_splits[i].length; j += 1) {
                //Only log the rounds with a time set
                if (time_splits[i][j] != 0 && time_splits[i][j-1] != 0) {
                    values.put(DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER, workout_number);
                    values.put(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NUMBER, i * time_splits[i].length + j);
                    values.put(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NAME, appContext.getResources().getString(R.string.text_cycle_prefix) + i + CommaSep + appContext.getResources().getString(R.string.text_round_prefix) + j);
                    values.put(DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_TIME, time_splits[i][j] - time_splits[i][j-1]);

                    long insert_id = database.insert(DatabaseContract.RoundDetails.TABLE_NAME, null, values);
                }
            }
        }
    }

    public void saveReps (int workout_number, int[][] repCount) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        for(int i = 1; i < repCount.length; i+=1) {
            for (int j = 1; j < repCount[i].length; j+=1) {
                //Only log the rounds with a time set
                if (repCount[i][j] != 0) {
                    values.put(DatabaseContract.RepCounts.COLUMN_NAME_WORKOUT_NUMBER, workout_number);
                    values.put(DatabaseContract.RepCounts.COLUMN_NAME_EXERCISE_NUMBER, i);
                    values.put(DatabaseContract.RepCounts.COLUMN_NAME_ROUND_NUMBER, j);
                    values.put(DatabaseContract.RepCounts.COLUMN_NAME_ROUND_NAME, appContext.getResources().getString(R.string.text_exercise_prefix) + i + CommaSep + appContext.getResources().getString(R.string.text_round_prefix) + j);
                    values.put(DatabaseContract.RepCounts.COLUMN_NAME_REP_COUNT, repCount[i][j]);

                    long insert_id = database.insert(DatabaseContract.RepCounts.TABLE_NAME, null, values);
                }
            }
        }
    }

    public ArrayList<Workout> WorkoutArchive () {

        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_
            + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NAME
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_TYPE
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_DATE_RAN
            + FROM_ + DatabaseContract.ArchivedWorkouts.TABLE_NAME
            + SORT_ + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_DATE_RAN + DESC
            , null );

        if (c != null){
            c.moveToFirst();
            ArrayList<Workout> workouts = new ArrayList<Workout>();
            while(!c.isAfterLast()) {
                Workout workout = new Workout();
                workout.setWorkoutNumber(c.getInt(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER)));
                workout.setWorkoutName(c.getString(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NAME)));
                workout.setWorkoutType(c.getString(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_TYPE)));
                workout.setDateRan(c.getString(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_DATE_RAN)));
                workouts.add(workout);
                c.moveToNext();
            }
            c.close();
            return workouts;
        } else {
            return null;
        }
    }

    public void saveWorkout (Workout justCompleted) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER, justCompleted.getWorkoutNumber());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NAME, justCompleted.getWorkoutName());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_DATE_RAN, TimeFunctions.getDateTime());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_TYPE, justCompleted.getWorkoutType());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_ROUNDS_SET, justCompleted.getRoundsSet());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_ROUNDS_COMPLETED, justCompleted.getRoundsCompleted());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_SET, justCompleted.getExercisesSet());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_COMPLETED, justCompleted.getExercisesCompleted());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_TIME_CAP, justCompleted.getTimeCap());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_REST_ROUND, justCompleted.getRestRound());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_BUY_IN, justCompleted.getBuyin());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_CASH_OUT, justCompleted.getCashOut());
        values.put(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_NOTES, justCompleted.getNotes());
        long insert_id = database.insert(DatabaseContract.ArchivedWorkouts.TABLE_NAME, null, values);
    }

    public Workout WorkoutDetails (Integer workout_number) {
        Cursor c = dbHelper.getReadableDatabase().rawQuery(SELECT_ + All
                + FROM_ + DatabaseContract.ArchivedWorkouts.TABLE_NAME
                + WHERE_ + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER + EQUALS_ + workout_number
                , null );

        Workout currentWorkout = new Workout();

        if (c != null){
            c.moveToFirst();
            currentWorkout.setWorkoutNumber(c.getInt(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER)));
            currentWorkout.setWorkoutName(c.getString(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NAME)));
            currentWorkout.setWorkoutType(c.getString(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_TYPE)));
            currentWorkout.setRoundsSet(c.getInt(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_ROUNDS_SET)));
            currentWorkout.setRoundsCompleted(c.getInt(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_ROUNDS_COMPLETED)));
            currentWorkout.setExercisesSet(c.getInt(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_SET)));
            currentWorkout.setExercisesCompleted(c.getInt(c.getColumnIndex(DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_COMPLETED)));

            return currentWorkout;
        } else {
            return null;
        }
    }


}

