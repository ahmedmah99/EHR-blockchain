package com.app.blockchain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class VisitInfo {


	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	int oxygen;
	float temperature;
	Reason reason;
	String diagnosis;

	Medication[] prescription;
	String referral_specialist;
	Date date;
	String labTest;
	boolean isLabtest;

	//OXYGEN,TEMPERATURE,REASON,DIAGNOSIS,PRESCRIPTION,ReferralSpecialist,DATE,LabTest,isLabTest
	@Override
	public String toString() {
		return oxygen +","+ temperature + "," + reason + "," + diagnosis + "," + Arrays.toString(prescription) + ","
				+ referral_specialist + "," + dateFormat.format(date) + "," + labTest + "," + isLabtest;
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
