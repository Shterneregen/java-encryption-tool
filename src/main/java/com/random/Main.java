package com.random;

import com.random.encryption.Encryption;
import com.random.encryption.RsaEnc;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            int index = 0;
            String mode = args[index++];
            String[] params = Arrays.copyOfRange(args, index, args.length);
            if ("-g".equals(mode)) {
                saveKeyPairBase64(params);
            } else if ("-e".equals(mode)) {
                encrypt(params);
            } else if ("-d".equals(mode)) {
                decrypt(params);
            } else if ("-sf".equals(mode)) {
                saveToFileFromFileWithEncryptedStr(params);
            } else if ("-ef".equals(mode)) {
                encryptFile(params);
            } else if ("-df".equals(mode)) {
                decryptFile(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveKeyPairBase64(String[] args) {
        String keyPairName = args.length > 0 ? args[0] : "key";
        String pathToSave = args.length > 1 ? args[1] : ".\\";
        RsaEnc.saveKeyPairBase64(pathToSave, keyPairName);
    }

    private static void encrypt(String[] args) {
        if (args.length < 2) {
            System.out.println("Wrong params");
            return;
        }
        String pubKeyPath = args[0];
        String originalStr = args[1];
        System.out.println(RsaEnc.encrypt(pubKeyPath, originalStr));
    }

    private static void decrypt(String[] args) {
        if (args.length < 2) {
            System.out.println("Wrong params");
            return;
        }
        String privateKeyPath = args[0];
        String encryptedStr = args[1];
        System.out.println(RsaEnc.decrypt(privateKeyPath, encryptedStr));
    }

    private static void saveToFileFromFileWithEncryptedStr(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Wrong params");
            return;
        }
        String privateKeyPath = args[0];
        String filePath = args[1];
        Utils.saveToFile(RsaEnc.decrypt(privateKeyPath, Utils.getStringFromReader(new FileReader(filePath))));
    }

    private static void encryptFile(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Wrong params");
            return;
        }
        String pubKeyPath = args[0];
        String originalFile = args[1];
        Encryption.encryptFileWithName(pubKeyPath, originalFile);
    }

    private static void decryptFile(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Wrong params");
            return;
        }
        String privateKeyPath = args[0];
        String encryptedFile = args[1];
        Encryption.decrypFileWithName(privateKeyPath, encryptedFile);
    }

}
