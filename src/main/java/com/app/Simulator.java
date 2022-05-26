package com.app;

import com.app.mongodb.RepositoryFNs;

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

    public static void main(String[] args){

//        Clinic clinic1 = new Clinic("123");
//        Clinic clinic2 = new Clinic("1234");

//        System.out.println(Main.getPrevHash());



//        String dec = EnD.AESencrypt("aaa",EnD.generateString());
//        System.out.println(EnD.AESdecrypt(dec,EnD.generateString()));
//        String b = "a";
//        System.out.println(b.split(";")[0]);
//        System.out.println(Main.getCAClinicKeys("6289151fee65a9451b2e8cd0"));
        enterBlock();

    }
    public static void enterBlock(){
        System.out.println("Enter Your ID");
        Scanner I_id = new Scanner(System.in);
        String id = I_id.nextLine();

        System.out.println("Enter Your Password");
        Scanner I_password = new Scanner(System.in);
        String password = I_password.nextLine();

        if(RepositoryFNs.authorizeClinic(id,password)){
            System.out.println("Enter Patient ID");
            Scanner I_pID = new Scanner(System.in);
            String pID = I_pID.nextLine();
        }
        else{
            System.out.println("Invalid Credentials");
        }

    }

}
