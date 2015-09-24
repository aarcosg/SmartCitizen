/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package us.idinfor.smartcitizen.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static us.idinfor.smartcitizen.backend.OfyService.ofy;


/**
 * An endpoint to send messages to devices registered with the backend
 * <p/>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p/>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
        name = "messagingApi",
        version = "v1",
        //resource = "device",
        namespace = @ApiNamespace(
                ownerDomain = "backend.moodlecontext.idinfor.us",
                ownerName = "backend.moodlecontext.idinfor.us",
                packagePath = ""
        )
)
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());

    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    public void sendMessages(@Named("message") String message) throws IOException {
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        List<Device> devices = ofy().load().type(Device.class).limit(10).list();
        for (Device device : devices) {
            Result result = sender.send(msg, device.getGcmId(), 5);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + device.getId());
                String canonicalGCMId = result.getCanonicalRegistrationId();
                if (canonicalGCMId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + device.getGcmId() + " updating to " + canonicalGCMId);
                    device.setGcmId(canonicalGCMId);
                    ofy().save().entity(device).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + device.getGcmId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(device).now();
                } else {
                    log.warning("Error when sending message : " + error);
                }
            }
        }
    }

    public void sendMessage(@Named("message") String message, @Named("deviceId") Long deviceId) throws IOException {
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        Device device = ofy().load().key(Key.create(Device.class, deviceId)).now();

        Result result = sender.send(msg, device.getGcmId(), 5);
        if (result.getMessageId() != null) {
            log.info("Message sent to " + device.getId());
            String canonicalGCMId = result.getCanonicalRegistrationId();
            if (canonicalGCMId != null) {
                // if the regId changed, we have to update the datastore
                log.info("Registration Id changed for " + device.getGcmId() + " updating to " + canonicalGCMId);
                device.setGcmId(canonicalGCMId);
                ofy().save().entity(device).now();
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                log.warning("Registration Id " + device.getGcmId() + " no longer registered with GCM, removing from datastore");
                // if the device is no longer registered with Gcm, remove it from the datastore
                ofy().delete().entity(device).now();
            } else {
                log.warning("Error when sending message : " + error);
            }
        }
    }
}
