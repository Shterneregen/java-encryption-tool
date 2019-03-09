package com.random.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

public class AesEnc {

    public static void encryptFile(PublicKey publicKey, InputStream in, OutputStream out) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey secretKey = kgen.generateKey();

        SecureRandom srandom = new SecureRandom();
        byte[] iv = new byte[128 / 8];
        srandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] secretKeyEncoded = rsaCipher.doFinal(secretKey.getEncoded());

        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

        out.write(secretKeyEncoded);
        out.write(iv);
        processFile(aesCipher, in, out);
    }

    public static void decrypFile(PrivateKey privateKey, InputStream in, OutputStream out) throws Exception {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] b = new byte[128];
        in.read(b);
        byte[] keyb = rsaCipher.doFinal(b);
        SecretKeySpec skey = new SecretKeySpec(keyb, "AES");

        byte[] iv = new byte[128 / 8];
        in.read(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
        processFile(aesCipher, in, out);
    }

    private static void processFile(Cipher cipher, InputStream in, OutputStream out)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = cipher.update(ibuf, 0, len);
            if (obuf != null) out.write(obuf);
        }
        byte[] obuf = cipher.doFinal();
        if (obuf != null) out.write(obuf);
    }

//    public static void encryptFile(PublicKey publicKey, String inputFile) throws Exception {
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        kgen.init(128);
//        SecretKey secretKey = kgen.generateKey();
//
//        SecureRandom srandom = new SecureRandom();
//        byte[] iv = new byte[128 / 8];
//        srandom.nextBytes(iv);
//        IvParameterSpec ivspec = new IvParameterSpec(iv);
//
//        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        byte[] secretKeyEncoded = rsaCipher.doFinal(secretKey.getEncoded());
//
//        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
//
//        byte[] newName = aesCipher.doFinal(inputFile.getBytes("UTF-8"));
//        String name = newName.toString();
////        String name = Base64.getEncoder().encode(newName).toString();
////        byte[] newName = Base64.getEncoder().encode(inputFile.getBytes("UTF-8"));
////        String name = aesCipher.doFinal(newName).toString();
//
//        System.out.println("name: " + name);
//
//        try (FileInputStream in = new FileInputStream(inputFile);
//             FileOutputStream out = new FileOutputStream(name)) {
//            out.write(secretKeyEncoded);
//            out.write(iv);
//            processFile(aesCipher, in, out);
//        }
//
//    }

//    public static void decrypFile(PrivateKey privateKey, String inputFile) throws Exception {
//
//        try (FileInputStream in = new FileInputStream(inputFile)) {
//
//            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
//            byte[] b = new byte[128];
//            in.read(b);
//            byte[] keyb = rsaCipher.doFinal(b);
//            SecretKeySpec skey = new SecretKeySpec(keyb, "AES");
//
//            byte[] iv = new byte[128 / 8];
//            in.read(iv);
//            IvParameterSpec ivspec = new IvParameterSpec(iv);
//
//            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
////            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            aesCipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
//
////            byte[] nameBytes = Base64.getDecoder().decode(inputFile.getBytes());
////            String name = aesCipher.doFinal(nameBytes).toString();
//            byte[] nameBytes = aesCipher.update(inputFile.getBytes("UTF-8"));
//            String name = nameBytes.toString();
////            String name = Base64.getDecoder().decode(nameBytes).toString();
//
//            System.out.println("name: " + name);
//            try (FileOutputStream out = new FileOutputStream(name)) {
//                processFile(aesCipher, in, out);
//            }
//        }
//    }
}
