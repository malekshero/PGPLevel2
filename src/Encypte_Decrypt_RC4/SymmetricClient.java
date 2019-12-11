
package Encypte_Decrypt_RC4;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class SymmetricClient {
    private static String algorithm = "RC4";

    public static byte[] encrypt(String toEncrypt, String key) throws Exception {

        SecureRandom sr = new SecureRandom(key.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // create an instance of cipher
        Cipher cipher = Cipher.getInstance(algorithm);

        // initialize the cipher with the key
        cipher.init(Cipher.ENCRYPT_MODE, sk);

        // enctypt!
        byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
        System.out.println("we encrypte the transactions using rc4 random key");
        return encrypted;
    }

    public static String decrypt(byte[] toDecrypt, byte[] key) throws Exception {
        System.out.println("we decrypte the transactions");
        // create a binary key from the argument key (seed)
        SecureRandom sr = new SecureRandom(key);
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(sr);
        SecretKey sk = kg.generateKey();

        // do the decryption with that key
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, sk);
        byte[] decrypted = cipher.doFinal(toDecrypt);
        System.out.println("we decrypte the transactions using rc4 random key");
        return new String(decrypted);
    }


}
