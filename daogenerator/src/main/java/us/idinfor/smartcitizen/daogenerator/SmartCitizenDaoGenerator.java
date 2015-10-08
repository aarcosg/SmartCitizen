package us.idinfor.smartcitizen.daogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class SmartCitizenDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "us.idinfor.smartcitizen.model");

        Entity context = schema.addEntity("Context");
        context.addIdProperty();
        context.addStringProperty("user");
        context.addStringProperty("deviceId");
        context.addIntProperty("activity");
        context.addDoubleProperty("latitude");
        context.addDoubleProperty("longitude");
        context.addDateProperty("time");

        new DaoGenerator().generateAll(schema,"app/src/main/java");
    }
}
