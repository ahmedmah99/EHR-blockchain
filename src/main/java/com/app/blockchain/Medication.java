package com.app.blockchain;

public class Medication {
    int dose;
    int intake_period;

    public Medication(int dose, int intake_period){
        this.dose = dose;
        this.intake_period = intake_period;
    }
    @Override
    public String toString() {
        return dose +","+ intake_period;
    }
}
