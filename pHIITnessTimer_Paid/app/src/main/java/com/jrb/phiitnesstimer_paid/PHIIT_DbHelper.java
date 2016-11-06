package com.jrb.phiitnesstimer_paid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by James on 19/04/2015.
 */
public class PHIIT_DbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "PHIIT.db";

    private static final String CommaSep = ", ";
    private static final String OpenBrac = " (";
    private static final String CloseBrac = ")";
    private static final String SemiColon = "; ";
    private static final String IntType = " INTEGER";
    private static final String TextType = " TEXT";
    private static final String NotNull = " NOT NULL";
    private static final String PKey = " PRIMARY KEY";
    private static final String CreateTable = "CREATE TABLE IF NOT EXISTS ";

    private static final String SQL_CREATE_ROUNDS_TABLE = CreateTable + DatabaseContract.RoundDetails.TABLE_NAME
            + OpenBrac + DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER + IntType + NotNull
            + CommaSep + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NUMBER + IntType + NotNull
            + CommaSep + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NAME + TextType + NotNull
            + CommaSep + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_TIME + IntType + NotNull
            + CommaSep + PKey + OpenBrac + DatabaseContract.RoundDetails.COLUMN_NAME_WORKOUT_NUMBER + CommaSep + DatabaseContract.RoundDetails.COLUMN_NAME_ROUND_NUMBER + CloseBrac
            + CloseBrac + SemiColon;
    private static final String SQL_CREATE_REPS_TABLE = CreateTable + DatabaseContract.RepCounts.TABLE_NAME
            + OpenBrac + DatabaseContract.RepCounts.COLUMN_NAME_WORKOUT_NUMBER + IntType + NotNull
            + CommaSep + DatabaseContract.RepCounts.COLUMN_NAME_ROUND_NUMBER + IntType + NotNull
            + CommaSep + DatabaseContract.RepCounts.COLUMN_NAME_EXERCISE_NUMBER + IntType + NotNull
            + CommaSep + DatabaseContract.RepCounts.COLUMN_NAME_ROUND_NAME + TextType + NotNull
            + CommaSep + DatabaseContract.RepCounts.COLUMN_NAME_REP_COUNT + IntType + NotNull
            + CommaSep + PKey + OpenBrac + DatabaseContract.RepCounts.COLUMN_NAME_WORKOUT_NUMBER + CommaSep + DatabaseContract.RepCounts.COLUMN_NAME_ROUND_NUMBER + CommaSep + DatabaseContract.RepCounts.COLUMN_NAME_EXERCISE_NUMBER + CloseBrac
            + CloseBrac + SemiColon;
    private static final String SQL_CREATE_ARCHIVE_TABLE = CreateTable + DatabaseContract.ArchivedWorkouts.TABLE_NAME
            + OpenBrac + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER + IntType + NotNull
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NAME + TextType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_DATE_RAN + TextType + NotNull
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_TYPE + TextType + NotNull
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_ROUNDS_SET + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_ROUNDS_COMPLETED + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_SET + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_EXERCISES_COMPLETED + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_TIME_CAP + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_REST_ROUND + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_BUY_IN + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_CASH_OUT + IntType
            + CommaSep + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_NOTES + TextType
            + CommaSep + PKey + OpenBrac + DatabaseContract.ArchivedWorkouts.COLUMN_NAME_WORKOUT_NUMBER + CloseBrac
            + CloseBrac + SemiColon;
    private static final String SQL_CREATE_SAVED_TABLE = CreateTable + DatabaseContract.SavedWorkouts.TABLE_NAME
            + OpenBrac + DatabaseContract.SavedWorkouts.COLUMN_NAME_WORKOUT_NAME + TextType + NotNull
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_DATE_SAVED + TextType + NotNull
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_WORKOUT_TYPE + TextType + NotNull
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_STARTING_COUNT + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_ROUNDS_SET + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_EXERCISES_SET + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_TIME_CAP + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_REST_ROUND + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_COUNT_UD + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_TIME_WARNING + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_BUY_IN + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_CASH_OUT + IntType
            + CommaSep + DatabaseContract.SavedWorkouts.COLUMN_NAME_NOTES + TextType
            + CommaSep + PKey + OpenBrac + DatabaseContract.SavedWorkouts.COLUMN_NAME_WORKOUT_NAME + CloseBrac
            + CloseBrac + SemiColon;

    public PHIIT_DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ROUNDS_TABLE);
        db.execSQL(SQL_CREATE_REPS_TABLE);
        db.execSQL(SQL_CREATE_ARCHIVE_TABLE);
        db.execSQL(SQL_CREATE_SAVED_TABLE);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Work out what to do on upgrades!!
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
