package us.idinfor.smartcitizen.json;


import java.util.List;

public class JsonListHermes<T> {

    String user;
    List<T> items;

    public JsonListHermes() {
    }

    public JsonListHermes(String user, List<T> items) {
        this.user = user;
        this.items = items;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
