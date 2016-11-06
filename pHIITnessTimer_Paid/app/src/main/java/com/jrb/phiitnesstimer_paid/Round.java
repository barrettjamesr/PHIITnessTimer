package com.jrb.phiitnesstimer_paid;

/**
 * Created by James on 20/04/2015.
 */
public class Round {
    Integer workoutNumber;
    Integer roundNumber;
    String roundName;
    Integer roundTime;

    public Round() {
        workoutNumber = 0;
        roundNumber = 0;
        roundName = "";
        roundTime = 0;

    }

    public void setWorkoutNumber(int wn) {
        workoutNumber = wn;
    }

    public void setRoundNumber(int rn) {
        roundNumber = rn;
    }

    public void setRoundName(String rn) {
        roundName= rn;
    }

    public void setRoundTime(int rt) {
        roundTime= rt;
    }

    public int getWorkoutNumber() {
        return workoutNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getRoundName() {
        return roundName;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public String getRoundDisplayTime() {
        return TimeFunctions.displayTime(roundTime);
    }

}
