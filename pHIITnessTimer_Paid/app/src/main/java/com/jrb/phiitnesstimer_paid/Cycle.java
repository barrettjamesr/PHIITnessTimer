package com.jrb.phiitnesstimer_paid;

/**
 * Created by James on 20/04/2015.
 */
public class Cycle {
    Integer workoutNumber;
    Integer roundNumber;
    Integer exerciseNumber;
    String roundName;
    Integer reps;

    public Cycle() {
        workoutNumber = 0;
        roundNumber = 0;
        exerciseNumber = 0;
        roundName = "";
        reps = 0;

    }

    public void setWorkoutNumber(int wn) {
        workoutNumber = wn;
    }

    public void setRoundNumber(int rn) {
        roundNumber = rn;
    }

    public void setExerciseNumber(int en) {
        roundNumber = en;
    }

    public void setRoundName(String rn) {
        roundName= rn;
    }

    public void setRepCount(int rc) {
        reps= rc;
    }

    public int getWorkoutNumber() {
        return workoutNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getExerciseNumber() {
        return exerciseNumber;
    }

    public String getRoundName() {
        return roundName;
    }

    public int getRepCount() {
        return reps;
    }


}
