package us.idinfor.smartcitizen.backend;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Activity{

    Integer id;
    String name;
    Date from;
    Date to;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public Long getDuration(){
        Long duration = Math.abs(getFrom().getTime() - getTo().getTime());
        Long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        return diffInMinutes;
    }

    public Activity() {
    }

    public enum Type{

        IN_VEHICLE (0,"IN_VEHICLE"),
        ON_BICYCLE (1,"ON_BICYCLE"),
        ON_FOOT (2,"ON_FOOT"),
        STILL (3,"STILL"),
        UNKNOWN (4,"UNKNOWN"),
        TILTING (5,"TILTING"),
        WALKING (7,"WALKING"),
        RUNNING (8,"RUNNING");

        private final int id;
        private final String name;
        Type(Integer id, String name){
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static Type getById(int id){
            for(Type type : values()){
                if(type.getId() == id) return type;
            }
            return Type.UNKNOWN;
        }
    }
}
