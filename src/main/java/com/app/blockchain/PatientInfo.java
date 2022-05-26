package com.app.blockchain;

public class PatientInfo {
	String name;
	int id;
	int age;
	float weight;
	float height;
	Sex sex;
	int oxygen;
	@Override
	public String toString() {
		return "PatientInfo:name=" + name + ", id=" + id + ", age=" + age + ", weight=" + weight + ", height=" + height
				+ ", sex=" + sex + ", oxygen=" + oxygen;
	}
	public PatientInfo(String name, int id, int age, float weight, float height, Sex sex, int oxygen) {
		super();
		this.name = name;
		this.id = id;
		this.age = age;
		this.weight = weight;
		this.height = height;
		this.sex = sex;
		this.oxygen = oxygen;
	}
	
	

}
