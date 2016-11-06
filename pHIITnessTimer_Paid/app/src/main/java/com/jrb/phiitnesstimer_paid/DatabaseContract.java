package com.jrb.phiitnesstimer_paid;

import android.provider.BaseColumns;

/**
 * Created by James on 19/04/2015.
 */
public final class DatabaseContract {
    public static final String DATABASE_NAME = "PHIIT_Data";
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    /* Inner classes that define the tables/contents */
    public static abstract class RoundDetails implements BaseColumns {
        public static final String TABLE_NAME = "round_details";
        public static final String COLUMN_NAME_WORKOUT_NUMBER= "workout_number_id";
        public static final String COLUMN_NAME_ROUND_NUMBER = "round_number_id";
        public static final String COLUMN_NAME_ROUND_NAME = "round_name";
        public static final String COLUMN_NAME_ROUND_TIME = "round_time";

    }

    public static abstract class RepCounts implements BaseColumns {
        public static final String TABLE_NAME = "rep_counts";
        public static final String COLUMN_NAME_WORKOUT_NUMBER= "workout_number_id";
        public static final String COLUMN_NAME_ROUND_NUMBER = "round_number_id";
        public static final String COLUMN_NAME_EXERCISE_NUMBER = "exercise_number_id";
        public static final String COLUMN_NAME_ROUND_NAME = "round_name";
        public static final String COLUMN_NAME_REP_COUNT = "rep_count";

    }

    public static abstract class ArchivedWorkouts implements BaseColumns {
        public static final String TABLE_NAME = "archived_workouts";
        public static final String COLUMN_NAME_WORKOUT_NUMBER= "workout_number_id";
        public static final String COLUMN_NAME_WORKOUT_NAME = "workout_name";
        public static final String COLUMN_NAME_DATE_RAN = "date_ran";
        public static final String COLUMN_NAME_WORKOUT_TYPE = "workout_type";
        public static final String COLUMN_NAME_ROUNDS_SET = "rounds_set";
        public static final String COLUMN_NAME_ROUNDS_COMPLETED = "rounds_completed";
        public static final String COLUMN_NAME_EXERCISES_SET = "exercises_set";
        public static final String COLUMN_NAME_EXERCISES_COMPLETED = "exercises_completed";
        public static final String COLUMN_NAME_TIME_CAP = "time_cap";
        public static final String COLUMN_NAME_REST_ROUND = "rest_round";
        public static final String COLUMN_NAME_BUY_IN = "buy_in";
        public static final String COLUMN_NAME_CASH_OUT = "cash_out";
        public static final String COLUMN_NAME_NOTES = "notes";

    }

    public static abstract class SavedWorkouts implements BaseColumns {
        public static final String TABLE_NAME = "saved_workouts";
        public static final String COLUMN_NAME_WORKOUT_NAME = "workout_name_id";
        public static final String COLUMN_NAME_DATE_SAVED = "date_saved";
        public static final String COLUMN_NAME_WORKOUT_TYPE = "workout_type";
        public static final String COLUMN_NAME_STARTING_COUNT = "starting_count";
        public static final String COLUMN_NAME_TIME_CAP = "time_cap";
        public static final String COLUMN_NAME_REST_ROUND = "rest_round";
        public static final String COLUMN_NAME_COUNT_UD = "count_ud";
        public static final String COLUMN_NAME_TIME_WARNING = "time_warning";
        public static final String COLUMN_NAME_ROUNDS_SET = "rounds_set";
        public static final String COLUMN_NAME_EXERCISES_SET = "exercises_set";
        public static final String COLUMN_NAME_BUY_IN = "buy_in";
        public static final String COLUMN_NAME_CASH_OUT = "cash_out";
        public static final String COLUMN_NAME_NOTES = "notes";

    }

}