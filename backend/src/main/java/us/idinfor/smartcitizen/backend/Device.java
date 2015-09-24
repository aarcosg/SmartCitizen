package us.idinfor.smartcitizen.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class Device {

    @Id
    Long id;
    /*@Index
    @Load
    Ref<User> user;*/
    @Index
    String deviceId;
    String gcmId;
    @Index
    Date time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*public User getUser() {
        return user.get();
    }

    public void setUser(User user) {
        this.user = Ref.create(user);
    }*/

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}