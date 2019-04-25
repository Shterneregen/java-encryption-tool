package com.random.encryption;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Encryption {

    private Encryption() {
    }

    public static void encryptFileWithName(String pubKeyPath, String inputFile) throws Exception {
        PublicKey publicKey = RsaEnc.loadPublicKey(pubKeyPath);
        AesEnc.encryptFileWithName(publicKey, inputFile);
    }

    public static void decryptFileWithName(String privateKeyPath, String inputFile) throws Exception {
        PrivateKey privateKey = RsaEnc.loadPrivateKey(privateKeyPath);
        AesEnc.decryptFileWithName(privateKey, inputFile);
    }

    public static void encryptFile(String pubKeyPath, String inputFile) throws Exception {
        PublicKey publicKey = RsaEnc.loadPublicKey(pubKeyPath);
        try (FileInputStream in = new FileInputStream(inputFile);
             FileOutputStream out = new FileOutputStream(inputFile + ".enc")) {
            AesEnc.encryptFile(publicKey, in, out);
        }
    }

    public static void decrypFile(String privateKeyPath, String inputFile) throws Exception {
        PrivateKey privateKey = RsaEnc.loadPrivateKey(privateKeyPath);
        try (FileInputStream in = new FileInputStream(inputFile);
             FileOutputStream out = new FileOutputStream(inputFile + ".ver")) {
            AesEnc.decryptFile(privateKey, in, out);
        }
    }
}
