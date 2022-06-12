package com.app.blockchain;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.security.*;

// Java program to generate
// a symmetric key


public class Block {
	public String hash;
	public int patientID;
	public String blockID;
	public String previousHash;
	public String visitInfo;
	public String patientInfo;

	public String lastVisitPointer;//the block hash of the last visit by a patient, will be null if no previous visits
	public String data;
	public long timeStamp = new Date().getTime();
	public int nonce;
	public String clinicID;
	public String EncryptedData;
	    public Block(VisitInfo visitInfo, PatientInfo patientInfo, String clinicID,int patientID, String lastVisitPointer) {
			this.visitInfo = visitInfo.toString();
			if(patientInfo!=null)
				this.patientInfo = patientInfo.toString();
			this.data = this.visitInfo + ";" + this.patientInfo;
	        this.patientID = patientID;
	        this.hash = calculateBlockHash();
	        this.clinicID = clinicID;
			this.lastVisitPointer = lastVisitPointer;
	    }

		public Block(String blockID,String EncryptedData, String ClinicID, String previousHash, String hash, int nonce, int patientID,
					 String lastVisitPointer){
			this.EncryptedData = EncryptedData;
			this.clinicID = ClinicID;
			this.previousHash = previousHash;
			this.hash = hash;
			this.nonce = nonce;
			this.patientID = patientID;
			this.lastVisitPointer = lastVisitPointer;
			this.blockID = blockID;

		}


	/**
	 * calculates the block hash
	 * @return the hash of the block
	 */
	    
	    public String calculateBlockHash() {
	        String dataToHash = previousHash 
	          + timeStamp
	          + Integer.toString(nonce) 
	          + data;
	        MessageDigest digest = null;
	        byte[] bytes = null;
	        Logger logger
            = Logger.getLogger(
            		Block.class.getName());
	        try {
	            digest = MessageDigest.getInstance("SHA-256");
	            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
	        } catch (NoSuchAlgorithmException ex) {
	            logger.log(Level.SEVERE, ex.getMessage());
	        }
	        StringBuilder buffer = new StringBuilder();
			assert bytes != null;
			for (byte b : bytes) {
	            buffer.append(String.format("%02x", b));
	        }
	        return buffer.toString();
//
	    }

		public void mineBlock(int prefix) {
	        String prefixString = new String(new char[prefix]).replace('\0', '0');
	        while (!hash.substring(0, prefix).equals(prefixString)) {
	            nonce++;
	            hash = calculateBlockHash();
	        }
	    }

		public boolean verifyBlock(int prefix){
			String prefixString = new String(new char[prefix]).replace('\0', '0');
			return this.hash.substring(0, prefix).equals(prefixString);
		}

}
