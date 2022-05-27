package com.app.user;

import com.app.blockchain.Block;
import com.app.blockchain.EnD;
import com.app.mongodb.RepositoryFNs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

public class Clinic {



    Hashtable<Integer,String> functions;
    String clinicID;
    String password;

    public Clinic(String password){

        functions = new Hashtable<Integer,String>();
        clinicFN();


        String semKey = EnD.generateString();
        HashMap<String, String> keys = new HashMap<>();
        try {
            keys = EnD.getKeys();
        }
        catch (NoSuchAlgorithmException e){
            System.out.println(e.getMessage());
        }
        this.clinicID = RepositoryFNs.insertClinic("Clinics",keys.get("Private"),keys.get("Public"),semKey,password);
        this.password = password;
    }

    /**
     * execute the service name of this clinic
     * @param FN
     */
    public void execute(int FN) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        String fnc = functions.get(FN);
//        Class<?> cls = Class.forName(this.getClass().getName());
//        Method m = this.getClass().getMethod(fnc,Class.forName("java.lang.String"));
//        m.invoke(this, "2");

        if(fnc.equals("getBlockChain")){getBlockChain();}

        else if(fnc.equals("viewPatientTx")){
            Scanner sc = new Scanner(System.in);

            System.out.println("Enter the patient ID");
            String patientId = sc.next();
            viewPatientTx(patientId);
        }
        else if(fnc.equals("insertIntoBlockChain")){insertIntoBlockChain();}
    }


    /**
     * insert a transaction in a block in the block chain
     */
    public void insertIntoBlockChain(){
        Block block = new Block(null,null,clinicID,0);
         block.insertBlockIntoDB();
         //block.blockID
    }

    public void viewPatientTx(String patientID){

        try {
            RepositoryFNs.viewPatientTx(patientID);
        }
        catch (Exception e){
            System.out.println("Wrong patient ID");
        }

    }

    public void getBlockChain(){
        RepositoryFNs.getBlockChain();
    }

    public String getClinicID() {
        return clinicID;
    }

    public void clinicFN(){

        functions.put(1,"getBlockChain");
        functions.put(2,"viewPatientTx");
        functions.put(3,"insertIntoBlockChain");
    }




}
