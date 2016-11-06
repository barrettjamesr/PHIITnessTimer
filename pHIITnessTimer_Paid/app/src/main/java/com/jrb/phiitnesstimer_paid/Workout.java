package com.jrb.phiitnesstimer_paid;

/**
 * Created by James on 20/04/2015.
 */
public class Workout {
    int workoutNumber;
    String workoutName;
    String dateRan;
    String workoutType;
    int roundsSet;
    int roundsCompleted;
    int exercisesSet;
    int exercisesCompleted;
    int timeCap;
    int restRound;
    Boolean buyIn;
    Boolean cashOut;
    String notes;

    public Workout() {
        workoutNumber= 0;
        workoutName= "";
        dateRan= "";
        workoutType= "";
        roundsSet= -1;
        roundsCompleted= -1;
        exercisesSet= -1;
        exercisesCompleted= -1;
        timeCap= -1;
        restRound= -1;
        buyIn= false;
        cashOut= false;
        notes= "";

    }

    public void setWorkoutNumber(int wn) {
        workoutNumber = wn;
    }

    public void setWorkoutName(String wn) {
        workoutName = wn;
    }

    public void setWorkoutType(String wt) {
        workoutType= wt;
    }

    public void setDateRan(String dr) {
        dateRan = dr;
    }

    public void setRoundsSet(int rs) {
        roundsSet = rs;
    }

    public void setRoundsCompleted(int rc) {
        roundsCompleted = rc;
    }

    public void setExercisesSet(int es) {
        exercisesSet= es;
    }

    public void setExercisesCompleted(int ec) {
        exercisesCompleted= ec;
    }

    public void setTimeCap(int tc) {
        timeCap = tc;
    }

    public void setRestRound(int wn) {
        restRound = wn;
    }

    public void setBuyIn(Boolean bi) {
        buyIn= bi;
    }

    public void setCashOut(Boolean co) {
        cashOut = co;
    }

    public void setNotes(String n) {
        notes= n;
    }

    public int getWorkoutNumber() {
        return workoutNumber;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public String getDateRan() {
        return dateRan;
    }

    public int getRoundsSet() {
        return roundsSet;
    }

    public int getRoundsCompleted() {
        return roundsCompleted;
    }

    public int getExercisesSet() {
        return exercisesSet;
    }

    public int getExercisesCompleted() {
        return exercisesCompleted;
    }

    public int getTimeCap() {
        return timeCap;
    }

    public int getRestRound() {
        return restRound;
    }

    public Boolean getBuyin() {
        return buyIn;
    }

    public Boolean getCashOut() {
        return cashOut;
    }

    public String getNotes() {
        return notes;
    }

}
