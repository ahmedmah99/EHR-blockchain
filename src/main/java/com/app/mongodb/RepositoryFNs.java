package com.app.mongodb;

import com.app.blockchain.Block;
import com.mongodb.client.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;

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

        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection(collectionName);
        ObjectId ObjectID = new ObjectId();
        Document doc = new Document("_id",ObjectID).append("privateKey",privateKey).append("publicKey",PublicKey).
                append("clinicSymKey",clinicSymetricKey).append("password",password);
        col.insertOne(doc);


        return String.valueOf(ObjectID);

    }

    public static ArrayList<Document> getBlockChain(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");

        MongoCollection<Document> col = db.getCollection("Blocks");
        return col.find().into(new ArrayList<>());
    }
    public static String getPrevHash(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Blocks");
        ArrayList<Document> temp = col.find().skip((int)col.countDocuments() - 1).into(new ArrayList<Document>());

        return temp.get(0).getString("prevHash");
    }
    public static void insertcol(String colName){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
        db.createCollection(colName);
    }
    public static HashMap<String,String> getCAClinicKeys(String clinicID){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
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
    public static HashMap<String,String> getPrivClinicKeys(String clinicID){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Clinics");
        Document d = col.find(new Document("_id", new ObjectId(clinicID))).first();
        HashMap<String,String> res= new HashMap<>();
        res.put("privateKey",d.getString("privateKey"));
        res.put("clinicSymKey",d.getString("clinicSymKey"));
        return res;
    }
    public static String getPrevBlockID(String patientID){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Blocks");
        ArrayList<Document> temp = col.find(new Document("patientID", patientID)).into(new ArrayList<Document>());
        if(temp.size()==0){
            return  null;
        }
        else{
            return ""+ temp.get(temp.size()-1).getObjectId("_id");
        }
    }

    //not finished
    public static String insertBlock(Block b){

        //previousHash, String clinicID,int patientID, String hash, String data, String prevBlockID, int nonce, timestamp
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Blocks");
        ObjectId ObjectID = new ObjectId();
        Document doc = new Document("_id",ObjectID).append("previousHash",b.previousHash).append("clinicID",b.clinicID).
                append("patientID",b.patientID).append("hash",b.hash).append("data",b.data).
                append("prevBlockID",b.prevBlockID).append("nonce",b.nonce).append("timestamp",b.timeStamp);
        col.insertOne(doc);


        return String.valueOf(ObjectID);
    }
    public static boolean authorizeClinic(String clinicID,String password){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://Miand:1234@cluster0.1iaxn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = mongoClient.getDatabase("EHRBlockChain");
//        db.createCollection(collectionName);
        MongoCollection<Document> col = db.getCollection("Clinics");
        Document filter = new Document("_id", new ObjectId(clinicID)).append("password",password);
        Document d = col.find(filter).first();
        return d!=null;
    }
}