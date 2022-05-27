package com.app.blockchain;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

import com.app.mongodb.RepositoryFNs;

import java.util.logging.Level;

import java.security.*;

// Java program to generate
// a symmetric key


public class Block {
	public static final String AES = "AES";


	public String hash;
	  	public int patientID;
	public String blockID;

	  	//visitInfo;patientInfo;visitInfo;patient;patientInfo;
		public String previousHash;
	public VisitInfo visitInfo;
	public PatientInfo patientInfo;
	public String data;
	public long timeStamp = new Date().getTime();
	public int nonce;
	public String clinicID;



	//
	    public Block(VisitInfo visitInfo, PatientInfo patientInfo, String clinicID,int patientID) {
	    		this.visitInfo = visitInfo;
	    		this.patientInfo = patientInfo;
				if(patientInfo==null){
					this.data = this.visitInfo+"";
				}
				else{
					this.data = this.visitInfo + ";" + this.patientInfo;
				}
	        this.patientID = patientID;
	        this.hash = calculateBlockHash();
	        this.clinicID = clinicID;
	    }

		public void insertBlockIntoDB(){
//			Check patient has previous data

			this.previousHash= RepositoryFNs.getPrevHash();

			HashMap<String,String> keys = RepositoryFNs.getPrivClinicKeys(this.clinicID);
			//Encrypt
			String dataEncrypted = EnD.AESencrypt(this.data,keys.get("clinicSymKey"));
			String dataHashed = sha(dataEncrypted);
			String messageEnc="";
			try {
				messageEnc = EnD.rsaEncrypt(keys.get("privateKey"), dataHashed + dataEncrypted);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
//			check CA validation
			if(centerAuthority(messageEnc,this.clinicID)){
				//mine & validate block mining
				mineBlock(3);
				if(verifyBlock(3)){
					//Add to DB
					this.blockID = RepositoryFNs.insertBlock(this);
				}
				else{
					System.out.println("Invalid Block");
				}
			}
			else{
				System.out.println("Invalid Data");
			}
		}
		public Boolean centerAuthority(String encData,String clinicID){
			//decrypts whole data using public key of clinic
			//generate hash for encrypted data
			//compare hash found to hash passed
			//decrypt data using clinic's symmetric key
			//validate data content
			HashMap<String,String> clinicKeys = RepositoryFNs.getCAClinicKeys(clinicID);
			String decDataAll="";
			try {
				decDataAll = EnD.rsaDecrypt(clinicKeys.get("publicKey"),encData);
			} catch (Exception e) {
				System.out.println(e.getMessage());;
			}
			String hashPassed = decDataAll.substring(0,64);
			String data = decDataAll.substring(64);
			String hashed= sha(hashPassed);
			if(hashed.equals(hashPassed)){
				String decryptedData = EnD.AESdecrypt(data,clinicKeys.get("clinicSymKey"));
				if(decryptedData==null){
					return false;
				}
				else{
					String[] splitted = decryptedData.split(";");
					if(splitted.length==2){
						String patientInfo = splitted[1];
						String[] patientInfoSplit = patientInfo.split(",");

						for(int i=0;i<patientInfoSplit.length;i++){
							int equalsI = patientInfoSplit[i].indexOf("=");
							String theData = patientInfoSplit[i].substring(equalsI+1);
							if(i==0){
								if(theData.equals("")){
									return false;
								}
							}
							else if(i==2){
								int age = Integer.parseInt(theData);
								if(age<0 || age>120){
									return false;
								}
							}
							else if(i==3){
								float weight = Float.parseFloat(theData);
								if(weight<3 || weight>240){
									return false;
								}
							}
							else if(i==4){
								float height = Float.parseFloat(theData);
								if(height<0.5 || height>2.35){
									return false;
								}
							}
							else if(i==5){
								if(!theData.equals("F") || !theData.equals("M")){
									return false;
								}
							}
							else if(i==6){
								int ox = Integer.parseInt(theData);
								if(ox<75 || ox>100){
									return false;
								}
							}
						}
					}
					String visitInfo = splitted[0];
					String[] visitInfoSplit = visitInfo.split(",");

					for(int i=0;i<visitInfoSplit.length;i++){
						int equalsI = visitInfoSplit[i].indexOf("=");
						String theData = visitInfoSplit[i].substring(equalsI+1);
						if(i==0){
							int ox = Integer.parseInt(theData);
							if(ox<75 || ox>100){
								return false;
							}
						}
						else if(i==1){
							float temp = Float.parseFloat(theData);
							if(temp<35 || temp>42){
								return false;
							}
						}
						else if(i==2){
							if(!theData.equals("Periodic_checkup") || !theData.equals("Case_Management") || !theData.equals("Other")){
								return false;
							}
						}
					}
					return true;
				}
			}
			else {
				return false;
			}
		}


		public String sha(String data){
			MessageDigest digest = null;
			byte[] bytes = null;
			Logger logger
					= Logger.getLogger(
					Block.class.getName());
			try {
				digest = MessageDigest.getInstance("SHA-256");
				bytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
			} catch (NoSuchAlgorithmException ex) {
				logger.log(Level.SEVERE, ex.getMessage());
			}
			StringBuffer buffer = new StringBuffer();
			for (byte b : bytes) {
				buffer.append(String.format("%02x", b));
			}
			System.out.println(buffer.toString());
			return buffer.toString();
		}


	/**
	 * calculates the block hash
	 * @return
	 */
	    
	    public String calculateBlockHash() {
	        String dataToHash = previousHash 
	          + Long.toString(timeStamp) 
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
	        StringBuffer buffer = new StringBuffer();
	        for (byte b : bytes) {
	            buffer.append(String.format("%02x", b));
	        }
			System.out.println(buffer.toString());
	        return buffer.toString();
//
	    }

	    ////////////////////////////////////////////////////////////////////////////////////////////
	    ////////////////////////////////////////////////////////////////////////////////////////////
	    
		public String mineBlock(int prefix) {
	        String prefixString = new String(new char[prefix]).replace('\0', '0');
	        while (!hash.substring(0, prefix).equals(prefixString)) {
	            nonce++;
	            hash = calculateBlockHash();
	        }
	        return hash;
	    }

	    ////////////////////////////////////////////////////////////////////////////////////////////
	    ////////////////////////////////////////////////////////////////////////////////////////////
	    		

	    
	    //private static final String key = "aesEncryptionKey";


	    @Override
		public String toString() {
			return "Block [hash=" + hash + ", patientID=" + patientID + ", blockID=" + blockID + ", previousHash="
					+ previousHash + ", visitInfo=" + visitInfo + ", patientInfo=" + patientInfo + ", data=" + data
					+ ", timeStamp=" + timeStamp + ", nonce=" + nonce + "]";
		}

		public boolean verifyBlock(int prefix){
			String prefixString = new String(new char[prefix]).replace('\0', '0');
			return this.hash.substring(0, prefix).equals(prefixString);
		}


	    






	    
//	    public static void main(String[] args) {
//	    	 int prefix = 3;
//	    	 PatientInfo patientInfo = new PatientInfo("a", 1,12, 12, 12, Sex.Female, 10);
//	    	 VisitInfo visitInfo = new VisitInfo(12);
//
//			Block newBlock = new Block(
//					visitInfo,
//					patientInfo,
//					"0",
//					"2");
//
//
//	 	    	newBlock.mineBlock(prefix);
//	 	    	System.out.println(newBlock);
////	 	    	String enc = encrypt("mahmoud", "aesEncryptionKey");
////	 	    	System.out.println(enc);
////	 	    	System.out.println(decrypt(enc, "aesEncryptionKey"));
//
//		    	 KeyPairGenerator keyPairGenerator;
//				try {
//					keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//					SecureRandom secureRandom = new SecureRandom();
//
//			         keyPairGenerator.initialize(2048,secureRandom);
//
//			         KeyPair pair = keyPairGenerator.generateKeyPair();
//
//			         PublicKey publicKey = pair.getPublic();
//
//			         String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
//
//			         System.out.println("public key = "+ publicKeyString);
//
//			         PrivateKey privateKey = pair.getPrivate();
//
//			         String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
//
//			         System.out.println("private key = "+ privateKeyString);
//
////			         String cipher = rsaEncrypt(privateKey,"Hello");
//
////			         rsaDecrypt(publicKey,cipher);
//
//					Main c = new Main();
////					String SysmetricKey = generateString();
////					c.insertClinic("RSA-keys",privateKeyString,publicKeyString,SysmetricKey,newBlock.clinicID);
//
//
//				} catch (NoSuchAlgorithmException  e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//	    }

	public static void main(String[] args){
			PatientInfo patientInfo = new PatientInfo("aklwDKL", 191,12, 12, 12, Sex.Female, 10);
			VisitInfo visitInfo = new VisitInfo(121293);

//			Block newBlock = new Block(
//					visitInfo,
//					patientInfo,
//					"0",
//					"2");
//		System.out.println(newBlock.calculateBlockHash().length());
	}

}
