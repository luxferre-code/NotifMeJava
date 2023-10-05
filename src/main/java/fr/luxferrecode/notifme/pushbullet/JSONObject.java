package fr.luxferrecode.notifme.pushbullet;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

public class JSONObject {

    protected Map<String, String> map = new HashMap<String, String>();

    public void add(String key, String value) throws NullPointerException, KeyAlreadyExistsException {
        if(value == null || key == null) throw new NullPointerException("Value or key is null");
        if(!this.isIn(key)) map.put(key, value);
        else throw new KeyAlreadyExistsException("Key already exists");
    }

    public void modify(String key, String value) throws NullPointerException, KeyNotFoundException {
        if(value == null || key == null) throw new NullPointerException("Value or key is null");
        if(this.isIn(key)) map.put(key, value);
        else throw new KeyNotFoundException("Key not found");
    }

    public void delete(String key) throws NullPointerException, KeyNotFoundException {
        if(key == null) throw new NullPointerException("Key is null");
        if(this.isIn(key)) map.remove(key);
        else throw new KeyNotFoundException("Key not found");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString().replace("\n", "\\n");
    }

    public boolean isIn(String key) {
        return map.containsKey(key);
    }

}
