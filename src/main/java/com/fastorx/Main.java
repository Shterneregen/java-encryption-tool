package com.fastorx;

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
                Encryption.generateAndSaveKeyPair(args.length > 1 ? args[1] : ".\\");
            } else if (args[0].equals("-e") && args.length > 2) {
                System.out.println(Encryption.encrypt(args[1], args[2]));
            } else if (args[0].equals("-d") && args.length > 2) {
                System.out.println(Encryption.decrypt(args[1], args[2]));
            } else if (args[0].equals("-df") && args.length > 2) {
                // [1] path to public key
                // [2] path file with encrypted string
                Utils.saveToFile(Encryption.decrypt(args[1], Utils.getStringFromReader(new FileReader(args[2]))));
            }
        } else {
//            String s = "";
//            try {
//                s = new String("Очень секретная строка, ну честно!".getBytes(), "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Original: " + s);
//            String e = encrypt(".\\key.pub", s);
//            System.out.println("Encrypted: " + e);
//            String d = decrypt(".\\key", e);
//            System.out.println("Decrypted: " + d);

//            System.out.println("Original: " + s);
            String e = "Очень секретная строка!!!";
            System.out.println("Encrypted: " + e);
            String d = Encryption.decrypt(".\\key", e);
            System.out.println("Decrypted: " + d);

//            String url = "http://global-time.info/" + e;
//            get(url);
        }
    }

}
