import com.random.encryption.Encryption;
import com.random.encryption.RsaEnc;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class EncryptionTest {

    private static String RSA = "RSA";

    @Test
    public void testGenerate64() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(1024);
            KeyPair keyPair = kpg.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            System.out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            System.out.println(Base64.getEncoder().encodeToString(privateKey.getEncoded()));

//            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
//            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
//            System.out.println(Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded()));
//            System.out.println(Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEncryption() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(1024);
            KeyPair keyPair = kpg.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            String plaintext = "Секрет!";
            byte[] bytes = plaintext.getBytes("UTF-8");
            byte[] encrypted = RsaEnc.blockCipher(bytes, Cipher.ENCRYPT_MODE, cipher);
            String encryptedStr = RsaEnc.byte2Hex(encrypted);
            System.out.println(encryptedStr);

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bts = RsaEnc.hex2Byte(encryptedStr);
            byte[] decrypted = RsaEnc.blockCipher(bts, Cipher.DECRYPT_MODE, cipher);
//            String resStr = new String(decrypted, "UTF-8");
//            System.out.println(resStr);
            String res = RsaEnc.removeTheTrash(new String(decrypted, "UTF-8"));
            System.out.println(res);
            assert (plaintext.equals(res));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | UnsupportedEncodingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrivateKeyLoad() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(1024);
            KeyPair keyPair = kpg.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            String plaintext = "Секрет!";
            byte[] bytes = plaintext.getBytes("UTF-8");
            byte[] encrypted = RsaEnc.blockCipher(bytes, Cipher.ENCRYPT_MODE, cipher);
            String encryptedStr = RsaEnc.byte2Hex(encrypted);
            System.out.println("encryptedStr: " + encryptedStr);
            byte[] keyBytes = Base64.getEncoder().withoutPadding().encode(privateKey.getEncoded());

            byte[] privateKeyBytes = Base64.getDecoder().decode(keyBytes);
            EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PrivateKey privateKeyNew = kf.generatePrivate(spec);

            cipher.init(Cipher.DECRYPT_MODE, privateKeyNew);
            byte[] bts = RsaEnc.hex2Byte(encryptedStr);
            byte[] decrypted = RsaEnc.blockCipher(bts, Cipher.DECRYPT_MODE, cipher);
//            String resStr = new String(decrypted, "UTF-8");
//            System.out.println(resStr);
            String res = RsaEnc.removeTheTrash(new String(decrypted, "UTF-8"));

            assert (plaintext.equals(res));
            System.out.println(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEncryptFile() {
        try {
            Encryption.encryptFileWithName(
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "key.pub",
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "test.jpg"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDecryptFile() {
        try {
            Encryption.decrypFileWithName(
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "key.key",
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "test.jpg.enc"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
