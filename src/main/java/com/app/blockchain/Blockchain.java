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

    //the simulation of the blockchain is hashmap, where the key is the hash of the block and value is a block object
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
     * view the blockchain by traversing the blockchain from the last block to the previous ones
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

                String data = "";
                //if the clinicIDs matches, decrypt the patient Data
                if(b.clinicID.equals(clinicID)) {

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
                }
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
            block.previousHash = lastHash;

        HashMap<String,String> keys = RepositoryFNs.getPrivClinicKeys(block.clinicID);

        //Encrypt
        String AES_enc = EnD.AESencrypt(block.data,keys.get("clinicSymKey"));

        assert AES_enc != null;
        String dataHashed = EnD.sha256(AES_enc);

        String messageEnc="";
        try {
            messageEnc = EnD.rsaEncrypt(keys.get("privateKey"), dataHashed + AES_enc);
            block.EncryptedData = messageEnc;
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

                System.out.println("Block Hash --> " + block.hash);
                System.out.println("Previous Hash --> " + block.previousHash);
                
                System.out.println("Authorized by Center authority, The Transaction successfully added to the blockchain");
            }
            else
                System.out.println("Invalid Block");
        }
        else
            System.out.println("Data rejected by the center authority");
    }


    /**
     * this function  return the clinicId of the last clinic that inserted a specific patient in the blockchain
     * @param patientID is the patientId inquired about
     * @return and array contains the clinicId and a hash of the block of the last visit
     */
    public String[] getLastVisitClinicId(int patientID,String clinicId){

        boolean read = false;
        if(Objects.equals(lastHash, "0"))
            return null;

        String LastVisitHash = null;
        String clinicID = null;
        Block b = blockchain.get(lastHash);
        while(!(b == null)){
            if(b.patientID == patientID && !read) {
                LastVisitHash = b.hash;
                read=true;
            }
            if(b.patientID == patientID && Objects.equals(b.clinicID, clinicId)) {
                clinicID = b.clinicID;
                break;
            }

            b = blockchain.get(b.previousHash);
        }
        return new String[]{clinicID,LastVisitHash};
    }


    /**
     * get the hash of the last block in the blockchain
     * @return the hash as 64-bit String
     */
    public String getLastHash() {
        return lastHash;
    }

    /**
     * getter of the blockchain hashmap
     * @return Hashmap representing the blockchain
     */
    public HashMap<String, Block> getBlockchain() {return blockchain;}
}
