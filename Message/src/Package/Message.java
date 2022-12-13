package Package;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Message implements Serializable {
    protected String author;
    protected String value;
    protected Object obj;
    protected int id;
    static int ID;

    public Message(String value, String author) {
        this.value = value;
        this.author = author;
//        this.sendingTime = this.generateTime();
        this.id = ID;
        this.obj = null;
        ++ID;
    }

    public Message(Object obj) {
        this.obj = obj;
        this.value = "";
//        this.sendingTime = this.generateTime();
        this.id = ID;
        ++ID;
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

    public String getValue() {
        return value;
    }

    public Object getObj() {
        return this.obj;
    }

    public String toString() {
//        return "[" + sendingTime + "] " + author + ": " + " -> " + value;
        return "[" + generateTime() + "] " + author + ": " + " -> " + value;
    }

    public String getNickname() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
/*
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String format_date = formatter.format(date); // Уже отформатированное время и дата

* */
