package Chat;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSA {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public RSA() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (Exception ignored) {
        }
    }

    public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public String encrypt(String letter) throws Exception {
        byte[] letterToBytes = letter.getBytes();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(letterToBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedLetter) throws Exception {
        byte[] encryptedBytes = decode(encryptedLetter);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedLetter = cipher.doFinal(encryptedBytes);
        return new String(decryptedLetter, "UTF8");
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
}
