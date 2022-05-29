package com.app.blockchain;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnD {
    private static final String initVector = "encryptionIntVec";

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
    public static HashMap<String,String> getKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator;
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom();

        keyPairGenerator.initialize(4096,secureRandom);

        KeyPair pair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = pair.getPublic();

        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        PrivateKey privateKey = pair.getPrivate();

        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

        HashMap<String,String> result = new HashMap<String,String>();
        result.put("Public",publicKeyString);
        result.put("Private",privateKeyString);
        return result;
    }

    public static String AESencrypt(String value,String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
//	    		return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String AESdecrypt(String encrypted,String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//	    		byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));


            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public static String rsaEncrypt(String privateKey, String message) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] privateBytes = Base64.getDecoder().decode(privateKey.getBytes());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        PrivateKey privKey;
        try {
            privKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE,privKey);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        //System.out.println("message bytes string" + Base64.getEncoder().encodeToString(messageBytes));
        byte[] encryptedMessage = encryptCipher.doFinal(messageBytes);

        return Base64.getEncoder().encodeToString(encryptedMessage);

    }
    public static String rsaDecrypt(String publicKey, String cipherText) throws
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedMessage =Base64.getDecoder().decode(cipherText);

        byte[] publicBytes = Base64.getDecoder().decode(publicKey.getBytes());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        PublicKey pubKey;
        try {
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }


        Cipher decryptionCipher = Cipher.getInstance("RSA");
        decryptionCipher.init(Cipher.DECRYPT_MODE,pubKey);
        byte[] decryptedMessage = decryptionCipher.doFinal(encryptedMessage);

        return new String(decryptedMessage,StandardCharsets.UTF_8);
    }

    public static String sha256(String data){
        MessageDigest digest = null;
        byte[] bytes = null;
        Logger logger = Logger.getLogger(Block.class.getName());
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes)
            buffer.append(String.format("%02x", b));

        return buffer.toString();
    }

}
