package com.app.blockchain;
import com.app.Simulator;
import com.app.mongodb.RepositoryFNs;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import java.util.*;


public class Blockchain {

    Simulator simulator;
    //private long lastBlock;
     private String lastHash;
    //List<Block> blockchain1 = new ArrayList<>();

    HashMap<String,Block> blockchain = new HashMap<String,Block>();

    public Blockchain(Simulator simulator){
        this.simulator = simulator;
        lastHash = "0";
        blockchainInit();
        blockchain.put("0",null);
    }

    /**
     * initialize the blockchain
     */
    private void blockchainInit(){
        FindIterable<Document> blocks=  RepositoryFNs.getBlockChain();

        try (MongoCursor<Document> cursor = blocks.iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Block block = new Block(document.get("_id").toString(), (String) document.get("data"), (String) document.get("clinicID"),
                        (String) document.get("previousHash"),(String) document.get("hash"), (Integer) document.get("nonce"),
                        (Integer) document.get("patientID"), (String) document.get("lastVisit"));

                blockchain.put(block.hash, block);
                this.lastHash = block.hash;
            }
        }
    }



    /**
     * view the blockchain
     */
    public void viewBlockchain(){

        Block b = blockchain.get(lastHash);
        while(!(b == null)){

            String[] block = {"BlockID", "Nonce", "TX", "Hash","Prev Hash"};
            String[] value = {b.blockID, String.valueOf(b.nonce), b.EncryptedData, b.hash, b.previousHash};

            System.out.print("                          Block:          Value:\n");
            System.out.print("                          -------------------\n");
            for (int i=0; i<block.length; i++){
                System.out.printf("                          %-15s%-15s\n", block[i],value[i]);
            }
            System.out.println();
            System.out.println("                                                                                |");
            System.out.println("                                                                                v");
            System.out.println();
            b = blockchain.get(b.previousHash);
        }
        System.out.println("                                                                          Genesis Block");

    }

    /**
     * view a certain patient transactions in the blockchain, if the patient data is inserted by the same clinic, auto decrypt will take place
     * @param clinicID the clinicId of the clinic
     * @param patientId the patientId
     */
    public void viewPatientData(String clinicID, String patientId){

        HashMap<String,String> keys = RepositoryFNs.getPrivClinicKeys(clinicID);

        Block b = blockchain.get(lastHash);
        while(!(b == null))
        {
            //check if the two patientID matches
            if(Integer.parseInt(patientId)==b.patientID){

                String data;
                //if the clinicIDs matches, decrypt the patient Data
                if(b.clinicID.equals(clinicID))
                    data = EnD.AESdecrypt(b.EncryptedData,keys.get("clinicSymKey"));
                else
                    data = b.EncryptedData;

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

    /**
     * insert a block in the blockchain
     * @param block to be added
     */
    public void insertInBlockChain(Block block)
    {

        if(Objects.equals(lastHash, "0"))
            block.previousHash = "0";
        else
            block.previousHash = blockchain.get(lastHash).hash;

        HashMap<String,String> keys = RepositoryFNs.getPrivClinicKeys(block.clinicID);
        //Encrypt
        block.EncryptedData = EnD.AESencrypt(block.data,keys.get("clinicSymKey"));

        assert block.EncryptedData != null;

        String dataHashed = EnD.sha256(block.EncryptedData);
        String messageEnc="";
        try {
            messageEnc = EnD.rsaEncrypt(keys.get("privateKey"), dataHashed + block.EncryptedData);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
//
//		check CA validation
        if(simulator.getCenterAuthority().centerAuthority(messageEnc,block.clinicID)){
            //mine & validate block mining
            block.mineBlock(3);
            if(block.verifyBlock(3)){
                //Add to DB
                block.blockID = RepositoryFNs.insertBlock(block);
                blockchain.put(block.hash,block);
                lastHash = block.hash;
                System.out.println("Authorized by Center authority, The Transaction successfully added to the blockchain");
            }
            else
                System.out.println("Invalid Block");
        }
        else
            System.out.println("Data rejected by the center authority");
    }

    /**
     * get the has of the previous visit
     */
    public String[] getLastVisitClinicId(int patientID){

        if(Objects.equals(lastHash, "0"))
            return null;

        String LastVisitHash = null;
        String clinicID = null;
        Block b = blockchain.get(lastHash);
        while(!(b == null)){
            if(b.patientID == patientID) {
                LastVisitHash = b.hash;
                clinicID = b.clinicID;
                break;
            }
            b = blockchain.get(b.previousHash);
        }
        return new String[]{clinicID,LastVisitHash};
    }

    public String getLastHash() {
        return lastHash;
    }

    public HashMap<String, Block> getBlockchain() {return blockchain;}
}
