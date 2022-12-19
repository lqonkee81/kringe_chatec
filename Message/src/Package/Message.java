package Package;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.login.AccountLockedException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Message implements Serializable, Cloneable {
    protected String author;
    protected String value;
    protected String sendingTime;
    protected Object obj;
    protected int id;
    static int ID;

    public Message(String value, String author) {
        this.value = value;
        this.author = author;
        this.id = ID;
        this.sendingTime = generateTime();
        ++ID;
    }

    public Message(Message msg) {
        this.value = msg.value;
        this.author = msg.author;
        this.id = msg.id;
        this.sendingTime = msg.sendingTime;
    }

    private String generateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String format_date = formatter.format(date); // Уже отформатированное время и дата
        return format_date;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setSendingTime(String sendingTime) {
        this.sendingTime = sendingTime;
    }

    public String getValue() {
        return value;
    }

    public Object getObj() {
        return this.obj;
    }

    public String getAuthor() {
        return author;
    }

    public String getSendingTime() {
        return sendingTime;
    }

    public String toString() {
        return "[" + sendingTime + "] " + author + ": " + " -> " + value;
    }

    public String getNickname() {
        return author;
    }

    public Message clone() throws CloneNotSupportedException {
        return (Message) super.clone();
    }
}