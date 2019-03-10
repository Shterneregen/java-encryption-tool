package com.random.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Date;

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

    public static void decryptFile(PrivateKey privateKey, InputStream in, OutputStream out) throws Exception {
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

    public static void encryptFileWithName(PublicKey publicKey, String inputFile) throws Exception {
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

        byte[] name = aesCipher.doFinal(inputFile.getBytes("UTF-8"));
        String fileName = String.format("%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS", new Date());

        try (FileInputStream in = new FileInputStream(inputFile);
             FileOutputStream out = new FileOutputStream(fileName)) {
            out.write(name.length);
            out.write(name);
            out.write(secretKeyEncoded);
            out.write(iv);
            processFile(aesCipher, in, out);
        }
    }

    public static void decryptFileWithName(PrivateKey privateKey, String inputFile) throws Exception {
        try (FileInputStream in = new FileInputStream(inputFile)) {
            int l = in.read();
            byte[] nb = new byte[l];
            in.read(nb);

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

            String name = new String(aesCipher.doFinal(nb));
            try (FileOutputStream out = new FileOutputStream(name)) {
                processFile(aesCipher, in, out);
            }
        }
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
}
