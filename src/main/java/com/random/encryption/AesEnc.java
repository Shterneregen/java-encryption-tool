package com.random.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AesEnc {

    private static final String RSA_ECB_PKCS_1_PADDING = "RSA/ECB/PKCS1Padding";
    private static final String AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String PBKDF_2_WITH_HMAC_SHA_256 = "PBKDF2WithHmacSHA256";
    private static final String AES = "AES";
    private static final int AES_KEY_SIZE = 128;
    private static final int IV_SIZE = AES_KEY_SIZE / 8;

    private AesEnc() {
    }

    public static void encryptFileWithKey(String pubKeyPath, String inputFile) throws Exception {
        PublicKey publicKey = KeyLoader.loadPublicKey(pubKeyPath);
        AesEnc.encryptFileWithKey(publicKey, inputFile);
    }

    public static void encryptFileWithKey(PublicKey publicKey, String inputFile) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        kgen.init(AES_KEY_SIZE);
        SecretKey secretKey = kgen.generateKey();

        SecureRandom srandom = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        srandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher rsaCipher = Cipher.getInstance(RSA_ECB_PKCS_1_PADDING);
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] secretKeyEncoded = rsaCipher.doFinal(secretKey.getEncoded());

        Cipher aesCipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encryptedFileName = aesCipher.doFinal(inputFile.getBytes(StandardCharsets.UTF_8));

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = format.format(new Date());

        try (FileInputStream in = new FileInputStream(inputFile);
             FileOutputStream out = new FileOutputStream(fileName)) {
            out.write(encryptedFileName.length);
            out.write(encryptedFileName);
            out.write(secretKeyEncoded);
            out.write(iv);
            processFile(aesCipher, in, out);
        }
    }

    public static void decryptFileWithKey(String privateKeyPath, String inputFile) throws Exception {
        PrivateKey privateKey = KeyLoader.loadPrivateKey(privateKeyPath);
        AesEnc.decryptFileWithKey(privateKey, inputFile);
    }

    public static void decryptFileWithKey(PrivateKey privateKey, String inputFile) throws Exception {
        try (FileInputStream in = new FileInputStream(inputFile)) {
            int fileNameLength = in.read();
            byte[] fileNameBytes = new byte[fileNameLength];
            in.read(fileNameBytes);

            Cipher rsaCipher = Cipher.getInstance(RSA_ECB_PKCS_1_PADDING);
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] secretKeyEncryptedBytes = new byte[AES_KEY_SIZE];
            in.read(secretKeyEncryptedBytes);
            byte[] secretKeyBytes = rsaCipher.doFinal(secretKeyEncryptedBytes);
            SecretKeySpec keySpec = new SecretKeySpec(secretKeyBytes, AES);

            byte[] iv = new byte[IV_SIZE];
            in.read(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher aesCipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
            aesCipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            String fileName = new String(aesCipher.doFinal(fileNameBytes));
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                processFile(aesCipher, in, out);
            }
        }
    }

    public static void encryptFileWithPassword(String inputFile, String password) throws Exception {
        byte[] salt = new byte[8];
        SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_256);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), AES);

        SecureRandom srandom = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        srandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher aesCipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
        aesCipher.init(Cipher.ENCRYPT_MODE, skey, ivSpec);

        byte[] encryptedFileName = aesCipher.doFinal(inputFile.getBytes(StandardCharsets.UTF_8));

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = format.format(new Date());

        try (FileInputStream in = new FileInputStream(inputFile);
             FileOutputStream out = new FileOutputStream(fileName)) {
            out.write(encryptedFileName.length);
            out.write(encryptedFileName);
            out.write(salt);
            out.write(iv);
            processFile(aesCipher, in, out);
        }
    }

    public static void decryptFileWithPassword(String inputFile, String password) throws Exception {
        try (FileInputStream in = new FileInputStream(inputFile)) {
            int fileNameLength = in.read();
            byte[] fileNameBytes = new byte[fileNameLength];
            in.read(fileNameBytes);
            byte[] salt = new byte[8];
            in.read(salt);
            byte[] iv = new byte[IV_SIZE];
            in.read(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_256);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), AES);

            Cipher aesCipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
            aesCipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));

            String fileName = new String(aesCipher.doFinal(fileNameBytes));
            try (FileOutputStream out = new FileOutputStream(fileName)) {
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
