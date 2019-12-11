package Encrypte_Decrypte_RSA;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RSA {

    public final String ALGORITHM = "RSA";

    public RSA() {
    }
    public byte[] encrypt(byte[] text, PublicKey key) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
        }
        System.out.println("encrypte key using rsa");
        return cipherText;
    }

    public byte[] decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
        }
        System.out.println("Decrypte key using rsa and key is : "+dectyptedText);
        return dectyptedText;

    }

    //////////////////////////////////////////////////return String
 //public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
 //    Cipher encryptCipher = Cipher.getInstance("RSA");
 //    encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

 //    byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(UTF_8));
 //    System.out.println("done RSA");
 //    return Base64.getEncoder().encodeToString(cipherText);
 //}

 public static String decryptString(String cipherText, PrivateKey privateKey) throws Exception {
     byte[] bytes = Base64.getDecoder().decode(cipherText);

     Cipher decriptCipher = Cipher.getInstance("RSA");
     decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

     return new String(decriptCipher.doFinal(bytes), UTF_8);
 }
}