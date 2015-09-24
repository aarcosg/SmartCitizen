package us.idinfor.smartcitizen.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "deviceApi",
        version = "v1",
        resource = "device",
        namespace = @ApiNamespace(
                ownerDomain = "backend.smartcitizen.idinfor.us",
                ownerName = "backend.smartcitizen.idinfor.us",
                packagePath = ""
        )
)
public class DeviceEndpoint {

    private static final Logger logger = Logger.getLogger(DeviceEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Device.class);
        ObjectifyService.register(User.class);
    }

    /**
     * Returns the {@link Device} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Device} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "device/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Device get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Device with ID: " + id);
        Device device = ofy().load().type(Device.class).id(id).now();
        if (device == null) {
            throw new NotFoundException("Could not find Device with ID: " + id);
        }
        return device;
    }

    /**
     * Inserts a new {@code Device}.
     */
    @ApiMethod(
            name = "insert",
            path = "device",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Device insert(Device device) throws NotFoundException{
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that device.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        Device record = findRecord(device.getDeviceId());
        if(record != null){
            record.setGcmId(device.getGcmId());
            return update(record.getId(),record);
        }
        device.time = new Date();
        ofy().save().entity(device).now();
        logger.info("Created Device with ID: " + device.getId());

        return ofy().load().entity(device).now();
    }

    /**
     * Updates an existing {@code Device}.
     *
     * @param id     the ID of the entity to be updated
     * @param device the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Device}
     */
    @ApiMethod(
            name = "update",
            path = "device/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Device update(@Named("id") Long id, Device device) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(device).now();
        logger.info("Updated Device: " + device);
        return ofy().load().entity(device).now();
    }

    /**
     * Deletes the specified {@code Device}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Device}
     */
    @ApiMethod(
            name = "remove",
            path = "device/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Device.class).id(id).now();
        logger.info("Deleted Device with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "device",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Device> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Device> query = ofy().load().type(Device.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Device> queryIterator = query.iterator();
        List<Device> deviceList = new ArrayList<Device>(limit);
        while (queryIterator.hasNext()) {
            deviceList.add(queryIterator.next());
        }
        return CollectionResponse.<Device>builder().setItems(deviceList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Device.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Device with ID: " + id);
        }
    }

    @ApiMethod(
            name = "user",
            path = "user/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<Device> userDevices(@Named("id") Long id) {
        User user = new User();
        user.setId(id);
        Ref<User> userKey = Ref.create(user);
        return ofy().load().type(Device.class).filter("user",userKey).list();
    }

    private Device findRecord(String deviceId) {
        return ofy().load().type(Device.class).filter("deviceId", deviceId).first().now();
    }
}