package com.app;

import com.app.mongodb.RepositoryFNs;
import com.app.user.Clinic;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Scanner;

// Traverse blockchain to find previous block
// Encrypt Data using symmetric key
// Encrypt Data using private key
// CA verifies digital signature
// CA decrypt data using symmetric key
// CA verifies data in block
// Mine Block
// Validate and add to chains of other clinics

public class Simulator {

    Hashtable<String,Clinic> existingClinics;

    public Simulator(){
        existingClinics = new Hashtable<>();
        clinics();
    }

    public void run() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        Scanner sc = new Scanner(System.in);
        while(true)
        {
            System.out.println("Enter 1 to continue, -1 to exit");
            int x = sc.nextInt();
            if(x == -1)
                break;
            else if (x == 1) {
                Clinic clinicAuth = authorizeClinic();

                if(clinicAuth != null){

                    while(true){
                        System.out.println("---- AUTHORIZED CLINIC ---- \n Enter '2' to continue, -1 for Main Menu");
                        int choice = sc.nextInt();
                        if(choice == -1)
                            break;
                        else if(choice == 2){
                            ClinicFunctions();
                            int cNum = sc.nextInt();
                            clinicAuth.execute(cNum);
                        }
                    }
                }
            }

        }

    }

    /**
     * autherize the clinic
     * @return true if clinic is legitimate, false otherwise
     */
    public Clinic authorizeClinic(){
        System.out.println("Enter your Clinic ID");
        Scanner I_id = new Scanner(System.in);
        String id = I_id.nextLine();

        System.out.println("Enter Your Password");
        Scanner I_password = new Scanner(System.in);
        String password = I_password.nextLine();

        boolean exist = RepositoryFNs.authorizeClinic(id, password);
        if(exist)
            return existingClinics.get(password);
        else return null;
    }

    public static void ClinicFunctions(){
        System.out.println("1 --> View the blockchain");
        System.out.println("2 --> View a patient tx");
        System.out.println("3 --> Insert a Transaction");
    }

    public void clinics(){
        existingClinics.put("123",new Clinic("123"));
        existingClinics.put("a123",new Clinic("a123"));
        existingClinics.put("b123",new Clinic("b123"));
    }


}
