package com.random;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (args.length > 0) {
            if (args[0].equals("-g")) {
                Encryption.saveKeyPairBase64(args.length > 1 ? args[1] : ".\\");
            } else if (args[0].equals("-e") && args.length > 2) {
                String pubKeyPath = args[1];
                String originalStr = args[2];
                System.out.println(Encryption.encrypt(pubKeyPath, originalStr));
            } else if (args[0].equals("-d") && args.length > 2) {
                String privateKeyPath = args[1];
                String encryptedStr = args[2];
                System.out.println(Encryption.decrypt(privateKeyPath, encryptedStr));
            } else if (args[0].equals("-df") && args.length > 2) {
                String privateKeyPath = args[1];
                String filePath = args[2];
                Utils.saveToFile(Encryption.decrypt(privateKeyPath, Utils.getStringFromReader(new FileReader(filePath))));
            }
        }
    }

}
