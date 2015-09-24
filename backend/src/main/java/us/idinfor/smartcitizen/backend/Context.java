package us.idinfor.smartcitizen.backend;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.Date;

@Entity
public class Context {

    @Id
    Long id;
    @Index
    @Load
    Ref<Device> device;
    String context;
    @Index
    GeoPt location;
    @Index
    Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device.get();
    }

    public void setDevice(Device device) {
        this.device = Ref.create(device);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public GeoPt getLocation() {
        return location;
    }

    public void setLocation(GeoPt location) {
        this.location = location;
    }
}
