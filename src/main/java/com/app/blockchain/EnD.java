package com.app.blockchain;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

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

        keyPairGenerator.initialize(2048,secureRandom);

        KeyPair pair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = pair.getPublic();

        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        System.out.println("public key = "+ publicKeyString);

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

        Cipher encryptionCipher = Cipher.getInstance("RSA");
        byte[] publicBytes = Base64.getDecoder().decode(privateKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privKey;
        try {
            privKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        encryptionCipher.init(Cipher.ENCRYPT_MODE,privKey);

        byte[] encryptedMessage =
                encryptionCipher.doFinal(message.getBytes());

        String encryption = Base64.getEncoder().encodeToString(encryptedMessage);
        return encryption;

    }
    public static String rsaDecrypt(String publicKey, String cipherText) throws
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        byte[] encryptedMessage =Base64.getDecoder().decode(cipherText.getBytes());

        byte[] publicBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey;
        try {
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }


        Cipher decryptionCipher = Cipher.getInstance("RSA");
        decryptionCipher.init(Cipher.DECRYPT_MODE,pubKey);
        byte[] decryptedMessage = decryptionCipher.doFinal(encryptedMessage);

        String decryption = new String(decryptedMessage);
        return decryption;
    }
}
