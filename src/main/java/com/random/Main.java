package com.random;

import com.random.encryption.Encryption;
import com.random.encryption.RsaEnc;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final String WRONG_PARAMS = "Wrong params";

    public static void main(String[] args) {
        try {
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        try {
            int index = 0;
            String mode = args[index++];
            String[] params = Arrays.copyOfRange(args, index, args.length);
            if ("-g".equals(mode)) {
                saveKeyPairBase64(params);
            } else if ("-e".equals(mode)) {
                encryptString(params);
            } else if ("-d".equals(mode)) {
                decryptString(params);
            } else if ("-sf".equals(mode)) {
                saveToFileFromFileWithEncryptedStr(params);
            } else if ("-ef".equals(mode)) {
                encryptFile(params);
            } else if ("-df".equals(mode)) {
                decryptFile(params);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void saveKeyPairBase64(String[] args) {
        String keyPairName = args.length > 0 ? args[0] : "key";
        String pathToSave = args.length > 1 ? args[1] : ".\\";
        RsaEnc.saveKeyPairBase64(pathToSave, keyPairName);
    }

    private static void encryptString(String[] args) {
        if (args.length < 2) {
            LOG.severe(WRONG_PARAMS);
            return;
        }
        String pubKeyPath = args[0];
        String originalStr = args[1];
        LOG.info(RsaEnc.encrypt(pubKeyPath, originalStr));
    }

    private static void decryptString(String[] args) {
        if (args.length < 2) {
            LOG.severe(WRONG_PARAMS);
            return;
        }
        String privateKeyPath = args[0];
        String encryptedStr = args[1];
        LOG.info(RsaEnc.decrypt(privateKeyPath, encryptedStr));
    }

    private static void saveToFileFromFileWithEncryptedStr(String[] args) throws IOException {
        if (args.length < 2) {
            LOG.severe(WRONG_PARAMS);
            return;
        }
        String privateKeyPath = args[0];
        String filePath = args[1];
        Utils.saveToFile(RsaEnc.decrypt(privateKeyPath, Utils.getStringFromReader(new FileReader(filePath))));
    }

    private static void encryptFile(String[] args) throws Exception {
        if (args.length < 2) {
            LOG.severe(WRONG_PARAMS);
            return;
        }
        String pubKeyPath = args[0];
        String originalFile = args[1];
        Encryption.encryptFileWithName(pubKeyPath, originalFile);
    }

    private static void decryptFile(String[] args) throws Exception {
        if (args.length < 2) {
            LOG.severe(WRONG_PARAMS);
            return;
        }
        String privateKeyPath = args[0];
        String encryptedFile = args[1];
        Encryption.decryptFileWithName(privateKeyPath, encryptedFile);
    }

}
