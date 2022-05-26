package com.app.user;

import com.app.blockchain.EnD;
import com.app.mongodb.RepositoryFNs;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class Clinic {

    String clinicID;


    String password;

    public Clinic(String password){

        String semKey = EnD.generateString();
        HashMap<String, String> keys = new HashMap<>();
        try {
            keys = EnD.getKeys();
        }
        catch (NoSuchAlgorithmException e){
            System.out.println(e.getMessage());
        }
        String id = RepositoryFNs.insertClinic("Clinics",keys.get("Private"),keys.get("Public"),semKey,password);
        this.clinicID = id;
        this.password = password;


    }

    public String getClinicID() {
        return clinicID;
    }


}
