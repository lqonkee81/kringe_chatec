package Package;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    protected String author;
    protected String value;
    protected String sendingTime;
    protected Object obj;
    protected int id;
    static int ID;

    public Message(String value, String author) {
        this.value = value;
        this.author = author;
        this.sendingTime = LocalDateTime.now().toString();
        this.id = ID;
        this.obj = null;
        ++ID;
    }

    public Message(Object obj) {
        this.obj = obj;
        this.value = "";
        this.sendingTime = LocalDateTime.now().toString();
        this.id = ID;
        ++ID;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Object getObj() {
        return this.obj;
    }

    public String toString() {
        return "[" + sendingTime + "] " + author + ": " + " -> " + value;
    }

    public String getNickname() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
