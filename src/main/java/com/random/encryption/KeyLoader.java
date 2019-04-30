package com.random.encryption;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    private static final String RSA = "RSA";
    private static final int RSA_KEYSIZE = 1024;
    private static final String EXT_PUBLIC = "pub";
    private static final String EXT_PRIVATE = "key";

    private KeyLoader() {
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
        kpg.initialize(RSA_KEYSIZE);
        return kpg.generateKeyPair();
    }

    public static void saveKeyPairBase64(String pathToSave, String keyPairName)
            throws NoSuchAlgorithmException, IOException {
        KeyPair keyPair = generateKeyPair();
        try (FileOutputStream fos = new FileOutputStream(pathToSave + keyPairName + "." + EXT_PUBLIC)) {
            fos.write(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()).getBytes(StandardCharsets.UTF_8));
        }
        try (FileOutputStream fos = new FileOutputStream(pathToSave + keyPairName + "." + EXT_PRIVATE)) {
            fos.write(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()).getBytes(StandardCharsets.UTF_8));
        }
    }

    static PrivateKey loadPrivateKey(String keyPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
        byte[] privateKeyBytes = Base64.getDecoder().decode(keyBytes);
        EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePrivate(spec);
    }

    static PublicKey loadPublicKey(String keyPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyBytes));
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePublic(spec);
    }

    public static void saveKeyPairBytes(String pathToSave, String keyPairName)
            throws IOException, NoSuchAlgorithmException {
        KeyPair keyPair = generateKeyPair();
        try (FileOutputStream fos = new FileOutputStream(pathToSave + keyPairName + "." + EXT_PUBLIC)) {
            fos.write(keyPair.getPublic().getEncoded());
        }
        try (FileOutputStream fos = new FileOutputStream(pathToSave + keyPairName + "." + EXT_PRIVATE)) {
            fos.write(keyPair.getPrivate().getEncoded());
        }
    }

    static PrivateKey loadPrivateKeyFromBytes(String keyPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
        EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePrivate(spec);
    }

    static PublicKey loadPublicKeyFromBytes(String keyPath)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(keyPath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        return kf.generatePublic(spec);
    }

}
