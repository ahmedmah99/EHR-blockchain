package com.app.user;

import com.app.Simulator;
import com.app.blockchain.*;
import com.app.mongodb.RepositoryFNs;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;
import java.util.*;

public class Clinic {

    Hashtable<Integer,String> functions;
    String clinicID;
    String password;

    Simulator simulator;

    public Clinic(String password, Simulator simulator){

        this.simulator = simulator;
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

    public Clinic(String password, String clinicID,Simulator simulator){

        this.password = password;
        this.clinicID = clinicID;
        this.simulator = simulator;
        functions = new Hashtable<Integer,String>();
        clinicFN();
    }

    /**
     * execute a service
     * @param FN function name to be executed
     */
    public void execute(int FN) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        String fnc = functions.get(FN);


        //view the whole blockchain
        switch (fnc) {
            case "getBlockChain":
                getBlockChain();
                break;


            //view a certain patient past data
            case "viewPatientTx": {
                Scanner sc = new Scanner(System.in);

                System.out.println("Enter the patient ID");
                String patientId = sc.next();
                viewPatientTx(patientId);
                break;
            }

            //inset a transaction into the blockchain
            case "insertIntoBlockChain": {

                VisitInfo visitInfo = null;
                PatientInfo patientInfo = null;

                Scanner sc = new Scanner(System.in);
                System.out.println("Enter your ID");
                int id = sc.nextInt();

                //check if patient exists already in the blockchain

                String[] history = getLastVisitClinicId(id);
                String setByClinicId = (history != null ? history[0] : null);
                String lastVisitBlockHash = (history != null ? history[1] : null);

                if (setByClinicId!= null && !Objects.equals(setByClinicId, this.clinicID)) {
                    System.out.println("This clinic can not insert this patient with id "+ id);
                    break;
                }

                if (setByClinicId == null) {
                    do {
                        System.out.println("Patient Info?  (Name # Age # Weight # Height # Sex # Oxygen )");
                        sc = new Scanner(System.in);
                        String info = sc.next();
                        patientInfo = getPatientInfo(info, id);
                    } while (patientInfo == null);
                }

                do {
                    System.out.println("Visit Info?  (Oxygen #Temperture # Reason # Diagnosis # prescription(dose1,intake2;dose2,intake2) # Ref # date # LabTest # isLabTest)");
                    sc = new Scanner(System.in);
                    String info = sc.next();
                    visitInfo = getVisitInfo(info);
                } while (visitInfo == null);

                insertIntoBlockchain(visitInfo, patientInfo, id, lastVisitBlockHash);
                break;
            }
        }
    }

    /**
     * insert a transaction in a block in the blockchain
     */
    public void insertIntoBlockchain(VisitInfo visitInfo, PatientInfo patientInfo,int patientId,String lastVisit){

        Block block = new Block(visitInfo,patientInfo,this.clinicID,patientId,lastVisit);
         this.simulator.getBlockchain().insertInBlockChain(block);
    }

    public String[] getLastVisitClinicId(int patientID){
        return simulator.getBlockchain().getLastVisitClinicId(patientID);
    }

    public void viewPatientTx(String patientID){
        simulator.getBlockchain().viewPatientData(clinicID,patientID);
    }

    public void getBlockChain(){
        simulator.getBlockchain().viewBlockchain();
    }

    public String getClinicID() {
        return clinicID;
    }

    public void clinicFN(){

        functions.put(1,"getBlockChain");
        functions.put(2,"viewPatientTx");
        functions.put(3,"insertIntoBlockChain");
    }


    public PatientInfo getPatientInfo(String patientInfo,int patientId){
        String[] p = patientInfo.split("#");

        PatientInfo patientInfo1 = null;
        //NAME # ID # AGE # WEIGHT # HEIGHT # SEX # OXYGEN
        if(p.length==6) {
            try {
                patientInfo1 = new PatientInfo(p[0], patientId, Integer.parseInt(p[1]), Float.parseFloat(p[2]),
                        Float.parseFloat(p[3]), Sex.valueOf(p[4]), Integer.parseInt(p[5]));
            }
            catch (Exception e){
                System.out.println("Invalid Structure of the Data");
            }


        }
        else
            System.out.println("Make sure you've entered all the data");

        return patientInfo1;
    }


    public VisitInfo getVisitInfo(String visitInfo){
        String[] v = visitInfo.split("#");

        VisitInfo visitInfo1 = null;
                                                                                //2,4;4,2dx
        //int oxygen # float temperature # Reason reason #String diagnosis #Medication[] prescription #
        //			String referral_specialist # Date date # String labTest # boolean isLabtest
        String[] medications;
        String[] medicationsItems;

        if(v.length==9) {

            try {
                medications = v[4].split(";");
                Medication[] medications1 = new Medication[medications.length];

                for (int i = 0; i < medications1.length; i++) {
                    medicationsItems = medications[i].split(",");
                    Medication med = new Medication(Integer.parseInt(medicationsItems[0]), Integer.parseInt(medicationsItems[1]));
                    medications1[i] = med;
                }


                    visitInfo1 = new VisitInfo(Integer.parseInt(v[0]), Float.parseFloat(v[1]), Reason.valueOf(v[2]), v[3], medications1, v[5],
                            new Date(), v[7], Boolean.getBoolean(v[8]));
            }
            catch (Exception e){
                System.out.println("Invalid Structure of the data");
            }

        }
        else
            System.out.println("Make sure you've entered all the data");

        return visitInfo1;
    }



}
