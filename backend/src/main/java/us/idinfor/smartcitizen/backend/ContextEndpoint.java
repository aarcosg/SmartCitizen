package us.idinfor.smartcitizen.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
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
        name = "contextApi",
        version = "v1",
        resource = "context",
        namespace = @ApiNamespace(
                ownerDomain = "backend.smartcitizen.idinfor.us",
                ownerName = "backend.smartcitizen.idinfor.us",
                packagePath = ""
        )
)
public class ContextEndpoint {

    private static final Logger logger = Logger.getLogger(ContextEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Context.class);
        ObjectifyService.register(Device.class);
    }

    /**
     * Returns the {@link Context} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Context} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "context/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Context get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Context with ID: " + id);
        Context context = ofy().load().type(Context.class).id(id).now();
        if (context == null) {
            throw new NotFoundException("Could not find Context with ID: " + id);
        }
        return context;
    }

    /**
     * Inserts a new {@code Context}.
     */
    @ApiMethod(
            name = "insert",
            path = "context",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Context insert(Context context) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that context.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        context.setTime(new Date());
        ofy().save().entity(context).now();
        logger.info("Created Context with ID = " + context.getId() +". Context = " + context.getContext());

        return ofy().load().entity(context).now();
    }

    /**
     * Updates an existing {@code Context}.
     *
     * @param id      the ID of the entity to be updated
     * @param context the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Context}
     */
    @ApiMethod(
            name = "update",
            path = "context/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Context update(@Named("id") Long id, Context context) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(context).now();
        logger.info("Updated Context: " + context);
        return ofy().load().entity(context).now();
    }

    /**
     * Deletes the specified {@code Context}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Context}
     */
    @ApiMethod(
            name = "remove",
            path = "context/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Context.class).id(id).now();
        logger.info("Deleted Context with ID: " + id);
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
            path = "context",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Context> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Context> query = ofy().load().type(Context.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Context> queryIterator = query.iterator();
        List<Context> contextList = new ArrayList<Context>(limit);
        while (queryIterator.hasNext()) {
            contextList.add(queryIterator.next());
        }
        return CollectionResponse.<Context>builder().setItems(contextList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Context.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Context with ID: " + id);
        }
    }
}