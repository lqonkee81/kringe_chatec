package Package;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class BasePackage implements Serializable {
    protected String value;
    protected String sendingTime;
    protected Object obj;
    protected int id;
    static int ID;

    public BasePackage(String value) {
        this.value = value;
        this.sendingTime = LocalDateTime.now().toString();
        this.id = ID;
        this.obj = null;
        ++ID;
    }

    public BasePackage(Object obj) {
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
        return "{" + sendingTime + "}: " + id + " -> " + value;
    }
}
