package com.app.blockchain;

import com.app.Simulator;
import com.app.mongodb.RepositoryFNs;
import java.util.HashMap;
import java.util.Scanner;

public class CenterAuthority {

    Simulator simulator;

    String password = "c123";
    public CenterAuthority(Simulator simulator){this.simulator = simulator;}
    public boolean centerAuthority(String encData,String clinicID){
        //decrypts whole data using public key of clinic
        //generate hash for encrypted data
        //compare hash found to hash passed
        //decrypt data using clinic's symmetric key
        //validate data content

        HashMap<String, String> clinicKeys = RepositoryFNs.getCAClinicKeys(clinicID);
        String decDataAll = "";
        try {
            decDataAll = EnD.rsaDecrypt(clinicKeys.get("publicKey"), encData);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        String hashPassed = decDataAll.substring(0, 64);
        String dataEncrypted = decDataAll.substring(64);
        String hashed = EnD.sha256(dataEncrypted);

        if (hashed.equals(hashPassed)) {
            String decryptedData = EnD.AESdecrypt(dataEncrypted, clinicKeys.get("clinicSymKey"));
            if(decryptedData==null)
                return false;
            else{
                String[] splitted = decryptedData.split(";");
                if(splitted.length==2){
                    String patientInfo = splitted[1];
                    String[] patientInfoSplit = patientInfo.split(",");
                    
                    for(int i=0;i<patientInfoSplit.length;i++){
                        String theData = patientInfoSplit[i];
                        if(i==0){
                            if(theData.equals("")){
                                System.out.println("1");
                                return false;
                            }
                        }
                        else if(i==2){
                            int age = Integer.parseInt(theData);
                            if(age<0 || age>120){
                                System.out.println("2");
                                return false;
                            }
                        }
                        else if(i==3){
                            float weight = Float.parseFloat(theData);
                            if(weight<3 || weight>240){
                                System.out.println("3");
                                return false;
                            }
                        }
                        else if(i==4){
                            float height = Float.parseFloat(theData);
                            if(height<0.5 || height>2.35){
                                System.out.println("4");
                                return false;
                            }
                        }
                        else if(i==6){
                            int ox = Integer.parseInt(theData);
                            if(ox<75 || ox>100){
                                System.out.println("6");
                                return false;
                            }
                        }
                    }
                }
                String visitInfo = splitted[0];
                String[] visitInfoSplit = visitInfo.split(",");

                for(int i=0;i<visitInfoSplit.length;i++){
                    String theData = visitInfoSplit[i];
                    if(i==0){
                        int ox = Integer.parseInt(theData);
                        if(ox<75 || ox>100){
                            System.out.println("7");
                            return false;
                        }
                    }
                    else if(i==1){
                        float temp = Float.parseFloat(theData);
                        if(temp<35 || temp>42){
                            System.out.println("8");
                            return false;
                        }
                    }
                    else if(i==2){
                        if(!theData.equals("Periodic_checkup") && !theData.equals("Case_Management") && !theData.equals("Other")){
                            System.out.println("9");
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        else return false;


    }


    public void centerAuthorityViewBlockchain(){

        //get the blockchain
        HashMap<String,Block> blockchain = simulator.getBlockchain().getBlockchain();

        Block b =  blockchain.get(simulator.getBlockchain().getLastHash());
        while(!(b == null)){

            String clinicId = b.clinicID;
            HashMap<String, String> keys = RepositoryFNs.getPrivClinicKeys(clinicId);
            String data = EnD.AESdecrypt(b.EncryptedData, keys.get("clinicSymKey"));


            String[] block = {"BlockID", "Nonce", "TX", "Hash", "Prev Hash"};
            String[] value = {b.blockID, String.valueOf(b.nonce), data, b.hash, b.previousHash};

            System.out.print("                          Block:          Value:\n");
            System.out.print("                          -------------------\n");
            for (int i = 0; i < block.length; i++) {
                System.out.printf("                          %-15s%-15s\n", block[i], value[i]);
            }
            System.out.println();
            System.out.println("                                                                                |");
            System.out.println("                                                                                v");
            System.out.println();
            b = blockchain.get(b.previousHash);
        }
        System.out.println("                                                                          Genesis Block");
    }

    public void centerAuthorityViewPatient(String patientId){

        //get the blockchain
        HashMap<String,Block> blockchain = simulator.getBlockchain().getBlockchain();

        Block b =  blockchain.get(simulator.getBlockchain().getLastHash());

        while(!(b == null))
        {
            //check if the two patientID matches
            if(Integer.parseInt(patientId)==b.patientID){

                String data = "";
                String clinicId = b.clinicID;
                HashMap<String, String> keys = RepositoryFNs.getPrivClinicKeys(clinicId);
                String decryptedDataRSA = "";

                try {
                    decryptedDataRSA = EnD.rsaDecrypt(keys.get("publicKey"), b.EncryptedData);
                } catch (Exception e) {
                    System.out.println("Digital Signature Failed");
                }

                String hashPassed = decryptedDataRSA.substring(0, 64);
                String dataEncrypted = decryptedDataRSA.substring(64);
                String hashed = EnD.sha256(dataEncrypted);

                if (hashed.equals(hashPassed))
                    data = EnD.AESdecrypt(dataEncrypted, keys.get("clinicSymKey"));
                else
                    System.out.println("Could not validate Data Integrity");


                String[] block = {"BlockID", "Nonce", "TX", "Hash","Prev Hash"};
                String[] value = {b.blockID, String.valueOf(b.nonce), data, b.hash, b.previousHash};

                System.out.print("                          Block:          Value:\n");
                System.out.print("                          -------------------\n");
                for (int i=0; i<block.length; i++){
                    System.out.printf("                          %-15s%-15s\n", block[i],value[i]);
                }
                System.out.println();
                System.out.println("                                                                                |");
                System.out.println("                                                                                v");
                System.out.println();

                String pointer = b.lastVisitPointer;
                if(pointer==null)
                    break;
                b = blockchain.get(pointer);
            }
            else
                b = blockchain.get(b.previousHash);
        }
        System.out.println("                                                                          Genesis Block");
    }

    public void execute(int num){
        if(num == 1)
            centerAuthorityViewBlockchain();
        else if(num == 2) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter PatientId");
            String patientId = sc.next();
            centerAuthorityViewPatient(patientId);
        }
        else System.out.println("Wrong number, try again");
    }

    public String getPassword() {
        return password;
    }
}
