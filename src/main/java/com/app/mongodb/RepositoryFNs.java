package com.app.mongodb;

import com.app.blockchain.Block;
import com.mongodb.client.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RepositoryFNs {
    public static void main(String[] args){


    }

    /**
     * insert private and publinc keys into the collectionName
     * @param collectionName
     * @param privateKey
     * @param PublicKey
     */
    public static String insertClinic(String collectionName, String privateKey,String PublicKey,
                                      String clinicSymetricKey, String password){

        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection(collectionName);
        ObjectId ObjectID = new ObjectId();
        Document doc = new Document("_id",ObjectID).append("privateKey",privateKey).append("publicKey",PublicKey).
                append("clinicSymKey",clinicSymetricKey).append("password",password);
        col.insertOne(doc);


        return String.valueOf(ObjectID);

    }

    public static FindIterable<Document> getBlockChain(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");

        MongoCollection<Document> col = db.getCollection("Blocks");
        FindIterable<Document> blocks =  col.find();
        return blocks;
    }


    public static String getPrevHash(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Blocks");
        ArrayList<Document> temp = col.find().skip((int)col.countDocuments() - 1).into(new ArrayList<Document>());

        return temp.get(0).getString("prevHash");
    }
    public static void insertcol(String colName){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
        db.createCollection(colName);
    }
    public static HashMap<String,String> getCAClinicKeys(String clinicID){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Clinics");
        Document d = col.find(new Document("_id", new ObjectId(clinicID))).first();
        System.out.println(d.getObjectId("_id"));
        HashMap<String,String> res= new HashMap<>();
        res.put("publicKey",d.getString("publicKey"));
        res.put("clinicSymKey",d.getString("clinicSymKey"));
        return res;
    }

    public static FindIterable<Document> getClinicInfo(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Clinics");
        FindIterable<Document> clinics = col.find();
        return clinics;
    }


    public static HashMap<String,String> getPrivClinicKeys(String clinicID){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Clinics");

        Document d = col.find(new Document("_id", new ObjectId(clinicID))).first();
        HashMap<String,String> res= new HashMap<>();
        res.put("privateKey",d.getString("privateKey"));
        res.put("clinicSymKey",d.getString("clinicSymKey"));
        return res;
    }

    /**
     * insert block transactions data in a noSQl database
     * @param b the block
     * @return the id of the block inserted
     */
    public static String insertBlock(Block b){

        //previousHash, String clinicID,int patientID, String hash, String data, String prevBlockID, int nonce, timestamp
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Blocks");
        ObjectId ObjectID = new ObjectId();
        Document doc = new Document("_id",ObjectID).append("previousHash",b.previousHash).append("clinicID",b.clinicID).
                append("patientID",b.patientID).append("hash",b.hash).append("data",b.EncryptedData).
                append("nonce",b.nonce).append("timestamp",b.timeStamp).append("lastVisit",b.lastVisitPointer);
        col.insertOne(doc);

        return String.valueOf(ObjectID);
    }

    /**
     * authorize the clinic by its id and password
     * @param clinicID is the clinic id
     * @param password is the password of the clinic
     * @return
     */
    public static boolean authorizeClinic(String clinicID,String password){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Clinics");

        try {
            Document filter = new Document("_id", new ObjectId(clinicID)).append("password",password);
            Document d = col.find(filter).first();
            return d!=null;
        }

        catch (Exception e){
            System.out.print(" , Invalid Credentials, Wrong ID or Password");
        }
        return false;
    }


    /**
     * number of blocks in the blockchian
     * @return
     */
    public static long blockCount(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://MMKH:gawafa@cluster0.5cebb.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Blocks");

        long num = col.estimatedDocumentCount();
        if(num == 0)
            return num;
        else
            return num-1;
    }


}
