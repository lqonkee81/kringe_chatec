package Package;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private String encrypt(String letter, PublicKey publicKey) throws Exception {
        byte[] letterToBytes = letter.getBytes();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(letterToBytes);
        return encode(encryptedBytes);
    }

    private String decrypt(String encryptedLetter, PrivateKey privateKey) throws Exception {
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

    public Message encrypt(Message msg, PublicKey puk) throws Exception {

        String value = msg.getValue();
        String author = msg.getAuthor();
        String sendingTime = msg.getSendingTime();

        value = encrypt(value, puk);
        author = encrypt(author, puk);
        sendingTime = encrypt(sendingTime, puk);

        msg.setValue(value);
        msg.setAuthor(author);
        msg.setSendingTime(sendingTime);

        return msg;
    }

    public Message decrypt(Message msg, PrivateKey prk) throws Exception {
        String value = msg.getValue();
        String author = msg.getAuthor();
        String sendingTime = msg.getSendingTime();

        value = decrypt(value, prk);
        author = decrypt(author, prk);
        sendingTime = decrypt(sendingTime, prk);

        msg.setValue(value);
        msg.setAuthor(author);
        msg.setSendingTime(sendingTime);

        return msg;
    }
}