package com.random.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RsaEnc {

    private static final Logger LOG = Logger.getLogger(RsaEnc.class.getName());

    private static final String RSA = "RSA";
    private static final String EXT_PUBLIC = "pub";
    private static final String EXT_PRIVATE = "key";

    //<editor-fold desc="encrypt">
    public static String encrypt(String pubKeyPath, String plaintext) {
        try {
            return encrypt(loadPublicKey(pubKeyPath), plaintext);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        return "";
    }

    private static String encrypt(PublicKey publicKey, String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = blockCipher(bytes, Cipher.ENCRYPT_MODE, cipher);
            return byte2Hex(encrypted);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        return "no encrypt result";
    }
    //</editor-fold>

    //<editor-fold desc="decrypt">
    public static String decrypt(String privateKeyPath, String encryptedStr) {
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyPath);
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bts = hex2Byte(encryptedStr);
            byte[] decrypted = blockCipher(bts, Cipher.DECRYPT_MODE, cipher);
            String resStr = new String(decrypted, StandardCharsets.UTF_8);
            return removeTheTrash(resStr);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        return "no decrypt result";
    }
    //</editor-fold>

    //<editor-fold desc="Encryption logic">
    public static byte[] blockCipher(byte[] bytes, int mode, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        // string initialize 2 buffers.
        // scrambled will hold intermediate results
        byte[] scrambled = new byte[0];

        // toReturn will hold the total result
        byte[] toReturn = new byte[0];
        // if we encrypt we use 100 byte long blocks. Decryption requires 128 byte long blocks (because of RSA)
        int length = (mode == Cipher.ENCRYPT_MODE) ? 100 : 128;

        // another buffer. this one will hold the bytes that have to be modified in this step
        byte[] buffer = new byte[length];

        for (int i = 0; i < bytes.length; i++) {

            // if we filled our buffer array we have our block ready for de- or encryption
            if ((i > 0) && (i % length == 0)) {
                //execute the operation
                scrambled = cipher.doFinal(buffer);
                // add the result to our total result.
                toReturn = append(toReturn, scrambled);
                // here we calculate the length of the next buffer required
                int newLength = length;

                // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                if (i + length > bytes.length) {
                    newLength = bytes.length - i;
                }
                // clean the buffer array
                buffer = new byte[newLength];
            }
            // copy byte into our buffer.
            buffer[i % length] = bytes[i];
        }

        // this step is needed if we had a trailing buffer. should only happen when encrypting.
        // example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
        scrambled = cipher.doFinal(buffer);

        // final step before we can return the modified data.
        toReturn = append(toReturn, scrambled);

        return toReturn;
    }

    private static byte[] append(byte[] prefix, byte[] suffix) {
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i = 0; i < prefix.length; i++) {
            toReturn[i] = prefix[i];
        }
        for (int i = 0; i < suffix.length; i++) {
            toReturn[i + prefix.length] = suffix[i];
        }
        return toReturn;
    }

    public static String byte2Hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte aB : b) {
            stmp = Integer.toHexString(aB & 0xff);
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toLowerCase();
    }

    private static byte hex2Byte(char a1, char a2) {
        int k;
        if (a1 >= '0' && a1 <= '9') {
            k = a1 - 48;
        } else if (a1 >= 'a' && a1 <= 'f') {
            k = (a1 - 97) + 10;
        } else if (a1 >= 'A' && a1 <= 'F') {
            k = (a1 - 65) + 10;
        } else {
            k = 0;
        }
        k <<= 4;
        if (a2 >= '0' && a2 <= '9') {
            k += a2 - 48;
        } else if (a2 >= 'a' && a2 <= 'f') {
            k += (a2 - 97) + 10;
        } else if (a2 >= 'A' && a2 <= 'F') {
            k += (a2 - 65) + 10;
        } else {
            k += 0;
        }
        return (byte) (k & 0xff);
    }

    public static byte[] hex2Byte(String str) {
        int len = str.length();
        if (len % 2 != 0) {
            return new byte[0];
        }
        byte[] r = new byte[len / 2];
        int k = 0;
        for (int i = 0; i < str.length() - 1; i += 2) {
            r[k] = hex2Byte(str.charAt(i), str.charAt(i + 1));
            k++;
        }
        return r;
    }

    /**
     * Удаляет управляющие символы из строки.
     */
    public static String removeTheTrash(String s) {
        char[] buf = new char[1024];
        int length = s.length();
        char[] oldChars = (length < 1024) ? buf : new char[length];
        s.getChars(0, length, oldChars, 0);
        int newLen = 0;
        for (int j = 0; j < length; j++) {
            char ch = oldChars[j];
            if (ch >= ' ') {
                oldChars[newLen] = ch;
                newLen++;
            }
        }
        if (newLen != length) {
            s = new String(oldChars, 0, newLen);
        }
        return s;
    }
    //</editor-fold>

    //<editor-fold desc="I/O">

    void showKeyPair(KeyPair keyPair) {
        PublicKey pub = keyPair.getPublic();
        LOG.info("Public Key: " + getHexString(pub.getEncoded()));
        PrivateKey priv = keyPair.getPrivate();
        LOG.info("Private Key: " + getHexString(priv.getEncoded()));
    }

    private String getHexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte aB : b) {
            result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    public static void saveKeyPairBase64(String pathToSave, String keyPairName)
            throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
        kpg.initialize(1024);
        KeyPair keyPair = kpg.generateKeyPair();

        // Store Public Key.
        try (FileOutputStream fos = new FileOutputStream(pathToSave + keyPairName + "." + EXT_PUBLIC)) {
//                fos.write("-----BEGIN RSA PUBLIC KEY-----\n");
            fos.write(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()).getBytes("UTF-8"));
//                fos.write("\n-----END RSA PUBLIC KEY-----\n");
        }

        // Store Private Key.
        try (FileOutputStream fos = new FileOutputStream(pathToSave + keyPairName + "." + EXT_PRIVATE)) {
            fos.write(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()).getBytes("UTF-8"));
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

    public static void saveKeyPairBytes(String pathToSave, String keyPairName) throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
        kpg.initialize(1024);
        KeyPair keyPair = kpg.generateKeyPair();

        // Store Public Key.
        try (FileOutputStream fos = new FileOutputStream(pathToSave + keyPairName + "." + EXT_PUBLIC)) {
            fos.write(keyPair.getPublic().getEncoded());
        }

        // Store Private Key.
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
    //</editor-fold>
}
