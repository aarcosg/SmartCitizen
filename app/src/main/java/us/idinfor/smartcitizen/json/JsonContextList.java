package us.idinfor.smartcitizen.json;


import java.util.List;

public class JsonContextList {

    String user;
    String deviceId;
    List<JsonContext> contexts;

    public JsonContextList(){}

    public JsonContextList(String user, String deviceId, List<JsonContext> contexts) {
        this.user = user;
        this.deviceId = deviceId;
        this.contexts = contexts;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<JsonContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<JsonContext> contexts) {
        this.contexts = contexts;
    }
}
