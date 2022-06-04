package com.app.blockchain;

public class Medication {
    String medicine;
    int intake_period;

    public Medication(String medicine, int intake_period){
        this.medicine = medicine;
        this.intake_period = intake_period;
    }
    @Override
    public String toString() {
        return medicine +","+ intake_period;
    }
}
