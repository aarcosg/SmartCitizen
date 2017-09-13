package es.us.hermes.smartcitizen.mvp.model.ztreamy;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * A Ztreamy event with all its fields.
 *
 */
public class Event {

    private static SimpleDateFormat RFC3339FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    @SerializedName("Application-Id")
    private String applicationId;
    @SerializedName("Event-Id")
    private String eventId;
    @SerializedName("Source-Id")
    private String sourceId;
    @SerializedName("Event-Type")
    private String eventType;
    @SerializedName("Syntax")
    private String syntax;
    @SerializedName("Timestamp")
    private String timestamp;
    @SerializedName("Body")
    private Map<String,Object> body;

    /**
     * Create an event with all the required parameters.
     *
     * @param eventId the id of the new event (normally a UUID).
     * @param sourceId the id of the source of this event (normally a UUID).
     * @param syntax the syntax of the event body (a MIME type).
     * @param applicationId the identifier of the application to which
     *            the event belongs.
     * @param eventType the application-specific type of event.
     * @param body the body of the event as a map. The map can contain nested
     *            maps for defining complex structures. When the event
     *            is not JSON, the map must contain a single key "value"
     *            with a String value.
     *
     */
    public Event(String eventId, String sourceId, String syntax,
                 String applicationId, String eventType,
                 Map<String,Object> body) {
        this.eventId = eventId;
        this.sourceId = sourceId;
        this.syntax = syntax;
        this.applicationId = applicationId;
        this.eventType = eventType;
        this.body = body;
        this.timestamp = createTimestamp();
    }

    /**
     * Create an event with an auto-generated event id.
     *
     * @param sourceId the id of the source of this event (normally a UUID).
     * @param syntax the syntax of the event body (a MIME type).
     * @param applicationId the identifier of the application to which
     *            the event belongs.
     * @param eventType the application-specific type of event.
     * @param body the body of the event as a map. The map can contain nested
     *            maps for defining complex structures. When the event
     *            is not JSON, the map must contain a single key "value"
     *            with a String value.
     *
     */

    public Event(String sourceId, String syntax, String applicationId,
                 String eventType, Map<String,Object> body) {
        this(createUUID(), sourceId, syntax, applicationId, eventType, body);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    /**
     * Create and return a new UUID.
     *
     */
    private static String createUUID() {
        return UUID.randomUUID().toString();
    }

    private static String createTimestamp() {
        return RFC3339FORMAT.format(new Date());
    }
}
