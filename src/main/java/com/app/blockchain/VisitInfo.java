package com.app.blockchain;

import java.util.Arrays;
import java.util.Date;

public class VisitInfo {
	
	int oxygen;
	float temperature;
	Reason reason;
	String diagnosis;
	Medication[] prescription;
	String referral_specialist;
	Date date;
	String labTest;
	boolean isLabtest;
	@Override
	public String toString() {
		return "VisitInfo [oxygen=" + oxygen + ", temperature=" + temperature + ", reason=" + reason + ", diagnosis="
				+ diagnosis + ", prescription=" + Arrays.toString(prescription) + ", referral_specialist="
				+ referral_specialist + ", date=" + date + ", labTest=" + labTest + ", isLabtest=" + isLabtest + "]";
	}
	public VisitInfo(int oxygen, float temperature, Reason reason, String diagnosis, Medication[] prescription,
			String referral_specialist, Date date, String labTest, boolean isLabtest) {
		super();
		this.oxygen = oxygen;
		this.temperature = temperature;
		this.reason = reason;
		this.diagnosis = diagnosis;
		this.prescription = prescription;
		this.referral_specialist = referral_specialist;
		this.date = date;
		this.labTest = labTest;
		this.isLabtest = isLabtest;
	}
	public VisitInfo(int oxygen) {
		super();
		this.oxygen = oxygen;
	}
	
}
