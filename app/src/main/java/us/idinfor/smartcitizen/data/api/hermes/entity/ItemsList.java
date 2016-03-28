package us.idinfor.smartcitizen.data.api.hermes.entity;


import java.util.List;

public class ItemsList<T> {

    String user;
    List<T> items;

    public ItemsList() {
    }

    public ItemsList(String user, List<T> items) {
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
