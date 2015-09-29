package us.idinfor.smartcitizen.backend;

public class Activity{

    Integer id;
    String name;
    Integer duration;

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
    }
}
