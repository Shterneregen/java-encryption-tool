import com.random.encryption.AesEnc;
import com.random.encryption.RsaEnc;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class EncryptionTest {

    private static final Logger LOG = Logger.getLogger(EncryptionTest.class.getName());
    private static final String RSA = "RSA";

    @Test
    public void testGenerate64() {
        try {
            KeyPair keyPair = generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            LOG.info(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            LOG.info(Base64.getEncoder().encodeToString(privateKey.getEncoded()));

//            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
//            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
//            System.out.println(Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded()));
//            System.out.println(Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Test
    public void testEncryption() {
        try {
            KeyPair keyPair = generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            String plaintext = "Секрет!";
            byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = RsaEnc.blockCipher(bytes, Cipher.ENCRYPT_MODE, cipher);
            String encryptedStr = RsaEnc.byte2Hex(encrypted);
            System.out.println(encryptedStr);

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bts = RsaEnc.hex2Byte(encryptedStr);
            byte[] decrypted = RsaEnc.blockCipher(bts, Cipher.DECRYPT_MODE, cipher);
            String res = RsaEnc.removeTheTrash(new String(decrypted, StandardCharsets.UTF_8));

            assertEquals(plaintext, res);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
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
            byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
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

            String res = RsaEnc.removeTheTrash(new String(decrypted, StandardCharsets.UTF_8));

            assertEquals(plaintext, res);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Test
    public void testEncryptFile() {
        try {
            AesEnc.encryptFileWithKey(
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "key.pub",
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "test.jpg"
            );
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Test
    public void testDecryptFile() {
        try {
            AesEnc.decryptFileWithKey(
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "key.key",
                    System.getProperty("user.dir") + "\\" + "test" + "\\" + "test.jpg.enc"
            );
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
        kpg.initialize(1024);
        return kpg.generateKeyPair();
    }
}
